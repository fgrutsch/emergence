package com.fgrutsch.emergence.core.vcs.bitbucketcloud

import cats.syntax.all.*
import com.fgrutsch.emergence.core.vcs.bitbucketcloud.Encoding.given
import com.fgrutsch.emergence.core.vcs.model.{MergeStrategy, *}
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.{DecodingFailure, Json}
import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

class EncodingSpec extends BaseSpec with TableDrivenPropertyChecks {

  test("decode PullRequest successfully") {
    val input = """{
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
    }"""

    val result = parse(input).value.as[PullRequest]
    result.value mustBe {
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      )
    }
  }

  test("decode BuildStatus successfully") {
    val input = """{
        "name": "Build and Test",
        "state": "SUCCESSFUL"
    }"""

    val result = parse(input).value.as[BuildStatus]
    result.value mustBe { BuildStatus(BuildStatusName("Build and Test"), BuildStatusState.Success) }
  }

  test("decode BuildStatusState successfully") {
    val table = Table(
      "input"      -> "expected",
      "SUCCESSFUL" -> BuildStatusState.Success.asRight,
      "INPROGRESS" -> BuildStatusState.InProgress.asRight,
      "FAILED"     -> BuildStatusState.Failed.asRight,
      "STOPPED"    -> BuildStatusState.Stopped.asRight,
      "invalid"    -> DecodingFailure("Unknown build status state: 'invalid'", Nil).asLeft
    )

    forAll(table) { case (input, expected) =>
      val jsonInput = Json.fromString(input)
      val result    = jsonInput.as[BuildStatusState]
      result mustBe { expected }
    }
  }

  test("encode MergeStrategy successfully") {
    val table = Table[MergeStrategy, String](
      "input"                   -> "expepcted",
      MergeStrategy.MergeCommit -> "merge_commit",
      MergeStrategy.Squash      -> "squash",
      MergeStrategy.FastForward -> "fast_forward"
    )

    forAll(table) { case (input, expected) =>
      val result = input.asJson
      result mustBe { Json.fromString(expected) }
    }
  }

}
