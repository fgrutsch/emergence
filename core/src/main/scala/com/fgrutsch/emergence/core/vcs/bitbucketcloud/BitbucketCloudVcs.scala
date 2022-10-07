/*
 * Copyright 2022 Emergence contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fgrutsch.emergence.core.vcs.bitbucketcloud

import cats.MonadThrow
import cats.syntax.all.*
import com.fgrutsch.emergence.core.vcs.*
import com.fgrutsch.emergence.core.vcs.bitbucketcloud.DiffStatResponse.*
import com.fgrutsch.emergence.core.vcs.bitbucketcloud.Encoding.given
import com.fgrutsch.emergence.core.vcs.model.*
import io.circe.JsonObject
import sttp.client3.*
import sttp.client3.circe.*
import sttp.model.HeaderNames.Location
import sttp.model.Uri

object BitbucketCloudVcs {

  private val asRedirect = {
    ignore
      .mapWithMetadata { (s, m) => Either.cond(m.isRedirect, s, s) }
  }

}

final class BitbucketCloudVcs[F[_]](using backend: SttpBackend[F, Any], settings: VcsSettings, F: MonadThrow[F])
    extends VcsAlg[F] {

  private val bitbucketRequest = basicRequest.auth.basic(settings.user.login, settings.user.secret)

  override def listPullRequests(repo: Repository): F[List[PullRequest]] = {
    val uri = settings.apiHost
      .addPath("repositories", repo.owner, repo.name, "pullrequests")
      .addParam("state", "OPEN")
      .addParam("pagelen", "50")

    bitbucketRequest
      .get(uri)
      .response(asJson[Page[PullRequest]])
      .send(backend)
      .flatMap(r => F.fromEither(r.body.map(_.items)))
  }

  override def listBuildStatuses(repo: Repository, pr: PullRequest): F[List[BuildStatus]] = {
    val uri =
      settings.apiHost.addPath("repositories", repo.owner, repo.name, "pullrequests", pr.number.toString, "statuses")

    bitbucketRequest
      .get(uri)
      .response(asJson[Page[BuildStatus]])
      .send(backend)
      .flatMap(r => F.fromEither(r.body.map(_.items)))
  }

  override def mergePullRequest(
      repo: Repository,
      number: PullRequestNumber,
      mergeStrategy: MergeStrategy,
      closeSourceBranch: Boolean): F[Unit] = {
    val uri  = settings.apiHost.addPath("repositories", repo.owner, repo.name, "pullrequests", number.toString, "merge")
    val body = MergePullRequestRequest(closeSourceBranch, mergeStrategy)

    bitbucketRequest
      .post(uri)
      .body(body)
      .response(asJson[JsonObject]) // Verifies 2xx response
      .send(backend)
      .flatMap(r => F.fromEither(r.body.map(_ => ())))
  }

  override def mergeCheck(repo: Repository, number: PullRequestNumber): F[MergeCheck] = {
    val uri =
      settings.apiHost.addPath("repositories", repo.owner, repo.name, "pullrequests", number.toString, "diffstat")

    val diffStatRequest = bitbucketRequest
      .get(uri)
      .followRedirects(false)
      .response(BitbucketCloudVcs.asRedirect)

    val parseRedirectUri = (response: Response[Either[Unit, Unit]]) => {
      response
        .header(Location)
        .toRight(s"Header expected: '$Location'")
        .flatMap(uri => Uri.parse(uri).leftMap(_ => s"Not a valid URI: '$uri'"))
    }

    for {
      resp1  <- diffStatRequest.send(backend)
      newUrl <- F.fromEither(parseRedirectUri(resp1).leftMap(new IllegalArgumentException(_)))
      resp2  <- bitbucketRequest.get(newUrl).response(asJson[Page[DiffStatResponse]]).send(backend)
      result <- F.fromEither(resp2.body)
    } yield MergeCheck.cond(result.items.forall(_.isMergeable()), "PR has merge conflicts.")
  }

  override def findEmergenceConfigFile(repo: Repository): F[Option[RepoFile]] = {
    val uri =
      settings.apiHost.addPath("repositories", repo.owner, repo.name, "src", "master", settings.repositoryConfigName)

    bitbucketRequest
      .get(uri)
      .response(asEither(ignore, asStringAlways).map(_.toOption))
      .send(backend)
      .map(_.body.map(RepoFile(_)))
  }

}
