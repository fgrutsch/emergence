package com.fgrutsch.emergence.core.vcs.bitbucketcloud

import cats.effect.IO
import cats.syntax.all.*
import com.fgrutsch.emergence.core.vcs.model.*
import io.circe.parser.*
import sttp.client3.Response
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client3.testing.SttpBackendStub
import sttp.model.{Header, Method, StatusCode}
import testutil.BaseSpec

class BitbucketCloudVcsSpec extends BaseSpec {

  private given SttpBackendStub[IO, Any] = AsyncHttpClientCatsBackend
    .stub[IO]
    .whenRequestMatches { r =>
      r.uri.path.startsWith("repositories" :: "owner" :: "name" :: "pullrequests" :: Nil) &&
      r.method == Method.GET &&
      r.uri.paramsMap == Map("state" -> "OPEN", "pagelen" -> "50")
    }
    .thenRespond(
      parse("""
        {
            "values": [
                {
                    "id": 1,
                    "title": "Test",
                    "source": {
                        "branch": {
                            "name": "update/abc"
                        }
                    },
                    "destination": {
                        "branch": {
                            "name": "master"
                        }
                    },
                    "author": {
                        "nickname": "fgrutsch"
                    }
                }
            ]
        }
        """).value.toString
    )
    .whenRequestMatches { r =>
      r.uri.path.startsWith("repositories" :: "owner" :: "name" :: "pullrequests" :: "1" :: "statuses" :: Nil) &&
      r.method == Method.GET
    }
    .thenRespond(
      parse("""
        {
            "values": [
                {
                    "name": "Build and Test",
                    "state": "SUCCESSFUL"
                }
            ]
        }
        """).value.toString
    )
    .whenRequestMatches { r =>
      r.uri.path.startsWith("repositories" :: "owner" :: "name" :: "pullrequests" :: "1" :: Nil) &&
      r.method == Method.POST
    }
    .thenRespond(
      parse("""
        {
          "id": 1,
          "title": "Test",
          "source": {
              "branch": {
                  "name": "update/abc"
              }
          },
          "destination": {
              "branch": {
                  "name": "master"
              }
          },
          "author": {
              "nickname": "fgrutsch"
          }
        }
        """).value.toString
    )
    .whenRequestMatches { r =>
      r.uri.path.startsWith("repositories" :: "owner" :: "name" :: "pullrequests" :: "1" :: "diffstat" :: Nil) &&
      r.method == Method.GET
    }
    .thenRespond(
      Response(
        "",
        StatusCode.Found,
        "Found",
        Header("Location", s"${vcsSettings.apiHost}/repositories/owner/name/diffstat/commit-hash") :: Nil
      )
    )
    .whenRequestMatches { r =>
      r.uri.path.startsWith("repositories" :: "owner" :: "name" :: "diffstat" :: "commit-hash" :: Nil) &&
      r.method == Method.GET
    }
    .thenRespond(
      parse("""
        {
          "values": [
            {
              "status": "modified"
            }
          ]
        }
        """).value.toString
    )
    .whenRequestMatches { r =>
      r.uri.path.startsWith("repositories" :: "owner" :: "name" :: "src" :: "master" :: ".emergence.yml" :: Nil) &&
      r.method == Method.GET
    }
    .thenRespond(".emergence.yml-file-content")

  private val bitbucketCloudVcs = new BitbucketCloudVcs[IO]

  test("listPullRequests") {
    val result = bitbucketCloudVcs.listPullRequests(Repository("owner", "name")).unsafeRunSync()
    result mustBe {
      List(
        PullRequest(
          PullRequestNumber(1),
          PullRequestTitle("Test"),
          BranchName("update/abc"),
          BranchName("master"),
          Author("fgrutsch")
        )
      )
    }
  }

  test("listBuildStatuses") {
    val result = bitbucketCloudVcs.listBuildStatuses(Repository("owner", "name"), PullRequestNumber(1)).unsafeRunSync()
    result mustBe { List(BuildStatus(BuildStatusName("Build and Test"), BuildStatusState.Success)) }
  }

  test("mergePullRequest") {
    val result = bitbucketCloudVcs
      .mergePullRequest(Repository("owner", "name"), PullRequestNumber(1), MergeStrategy.Squash, true)
      .unsafeRunSync()
    result mustBe { () }
  }

  test("mergeCheck") {
    val result = bitbucketCloudVcs.mergeCheck(Repository("owner", "name"), PullRequestNumber(1)).unsafeRunSync()
    result mustBe { MergeCheck.Accept }
  }

  test("findEmergenceConfig") {
    val result = bitbucketCloudVcs.findEmergenceConfigFile(Repository("owner", "name")).unsafeRunSync()
    result mustBe { RepoFile(".emergence.yml-file-content").some }
  }

}
