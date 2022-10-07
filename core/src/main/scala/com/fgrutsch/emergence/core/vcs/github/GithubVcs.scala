package com.fgrutsch.emergence.core.vcs.github

import com.fgrutsch.emergence.core.vcs.VcsAlg
import com.fgrutsch.emergence.core.vcs.model.*
import com.fgrutsch.emergence.core.vcs.VcsSettings
import com.fgrutsch.emergence.core.vcs.github.Encoding.given
import sttp.client3.*
import sttp.client3.circe.*
import cats.syntax.all.*
import cats.MonadThrow
import sttp.model.HeaderNames
import cats.data.Nested

class GithubVcs[F[_]](using backend: SttpBackend[F, Any], settings: VcsSettings, F: MonadThrow[F]) extends VcsAlg[F] {

  private val githubRequest = basicRequest.auth
    .basic(settings.user.login, settings.user.secret)
    .header(HeaderNames.Accept, "application/vnd.github.v3+json")

  override def listPullRequests(repo: Repository): F[List[PullRequest]] = {
    val uri = settings.apiHost
      .addPath("repos", repo.owner, repo.name, "pulls")
      .addParam("state", "open")
      .addParam("per_page", "100")

    githubRequest
      .get(uri)
      .response(asJson[List[PullRequest]])
      .send(backend)
      .flatMap(r => F.fromEither(r.body))
  }

  override def listBuildStatuses(repo: Repository, pr: PullRequest): F[List[BuildStatus]] = {
    val getCheckSuitesRequest = () => {
      val uri = settings.apiHost
        .addPath("repos", repo.owner, repo.name, "commits", pr.sourceBranchName.toString, "check-suites")

      githubRequest
        .get(uri)
        .response(asJson[List[CheckSuiteResponseItem]])
        .send(backend)
        .flatMap(r => F.fromEither(r.body))
    }

    val getCheckRunsRequest = (id: CheckSuiteResponseItem.Id) => {
      val uri = settings.apiHost
        .addPath("repos", repo.owner, repo.name, "check-suites", id.toString, "check-runs")

      githubRequest
        .get(uri)
        .response(asJson[List[CheckRunResponseItem]])
        .send(backend)
        .flatMap(r => F.fromEither(r.body))
    }

    // for {
    //   checkSuites <- getCheckSuitesRequest()
    //   checkRuns <- checkSuites.map(cs => getCheckRunsRequest(cs.id)).sequence
    // } yield {
    //   checkRuns.flatten
    //   .map {
    //     case CheckRunResponseItem()
    //   }
    // }

    ???
  }

  override def mergePullRequest(
      repo: Repository,
      number: PullRequestNumber,
      mergeStrategy: MergeStrategy,
      closeSourceBranch: Boolean): F[Unit] = ().pure[F]

  override def mergeCheck(repo: Repository, number: PullRequestNumber): F[MergeCheck] = ???

  override def findEmergenceConfigFile(repo: Repository): F[Option[RepoFile]] = none.pure[F]

}
