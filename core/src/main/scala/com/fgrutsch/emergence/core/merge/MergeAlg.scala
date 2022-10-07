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

package com.fgrutsch.emergence.core.merge

import cats.MonadThrow
import cats.data.Validated.{Invalid, Valid}
import cats.effect.kernel.{Concurrent, Temporal}
import cats.syntax.all.*
import com.fgrutsch.emergence.core.condition.{ConditionMatcherAlg, Input}
import com.fgrutsch.emergence.core.configuration.{EmergenceConfig, MergeConfig}
import com.fgrutsch.emergence.core.utils.logging.*
import com.fgrutsch.emergence.core.vcs.VcsAlg
import com.fgrutsch.emergence.core.vcs.model.{MergeCheck, PullRequest, Repository}
import fs2.Stream
import org.typelevel.log4cats.Logger

class MergeAlg[F[_]: Temporal: Concurrent](using
    logger: Logger[F],
    vcsAlg: VcsAlg[F],
    conditionMatcherAlg: ConditionMatcherAlg[F],
    F: MonadThrow[F]) {

  def mergePullRequests(repo: Repository, emergenceConfig: EmergenceConfig): F[Unit] = {
    Stream
      .evals(vcsAlg.listPullRequests(repo))
      .evalTap(pr => logger.info(highlight(s"Processing pull request #${pr.number}")))
      .evalFilter(pr => filterByConditions(repo, emergenceConfig, pr))
      .evalFilter(pr => filterByMergeCheck(repo, pr))
      .evalMap(pr => executeMerge(repo, emergenceConfig, pr))
      .metered(emergenceConfig.merge.flatMap(_.throttle).getOrElse(MergeConfig.Default.throttle))
      .compile
      .drain
  }

  private def filterByConditions(repo: Repository, emergenceConfig: EmergenceConfig, pr: PullRequest) = {
    for {
      buildStatuses <- vcsAlg.listBuildStatuses(repo, pr)
      _             <- logger.info(s"Pull request has build statuses: ${bulletPointed(buildStatuses)}")
      input         <- Input(pr, buildStatuses).pure[F]
      matchResult   <- conditionMatcherAlg.checkConditions(emergenceConfig.conditions, input).pure[F]
      _ <- matchResult match {
        case Invalid(e) => logger.info(s"Ignoring pull request as not all conditions match: ${bulletPointed(e.toList)}")
        case Valid(_)   => logger.info("Pull request matches all configured conditions.")
      }
    } yield matchResult.isValid
  }

  private def filterByMergeCheck(repo: Repository, pr: PullRequest) = {
    vcsAlg
      .mergeCheck(repo, pr.number)
      .flatMap {
        case MergeCheck.Accept =>
          F.pure(true)
        case MergeCheck.Decline(reason) =>
          logger.info(s"Ignoring as merge check for PR failed. Reason: $reason").map(_ => false)
      }
  }

  private def executeMerge(repo: Repository, emergenceConfig: EmergenceConfig, pr: PullRequest) = {
    val strategy = emergenceConfig.merge
      .flatMap(_.strategy)
      .getOrElse(MergeConfig.Default.strategy)

    val closeSourceBranch = emergenceConfig.merge
      .flatMap(_.closeSourceBranch)
      .getOrElse(MergeConfig.Default.closeSourceBranch)

    for {
      _ <- vcsAlg.mergePullRequest(repo, pr.number, strategy, closeSourceBranch)
      _ <- logger.info(s"Merged pull request #${pr.number}")
    } yield ()
  }

}
