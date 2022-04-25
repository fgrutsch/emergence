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

package com.fgrutsch.emergence.core.condition

import cats.data.{NonEmptyList, ValidatedNel}
import cats.syntax.all.*
import com.fgrutsch.emergence.core.condition.Condition
import com.fgrutsch.emergence.core.condition.ConditionMatcher.*
import com.fgrutsch.emergence.core.vcs.model.*

class ConditionMatcherAlg[F[_]] {

  def checkConditions(conditions: List[Condition], input: Input): ValidatedNel[String, Unit] = {

    val checkConditionMatches: Condition => ValidatedNel[String, Unit] = {
      case c: Condition.BuildSuccessAll.type => c.matches(input.buildStatuses)(using bsaMatcher)
      case c: Condition.Author               => c.matches(input.pullRequest.author)
      case c: Condition.SourceBranch         => c.matches(input.pullRequest.sourceBranchName)
      case c: Condition.TargetBranch         => c.matches(input.pullRequest.targetBranchName)
    }

    NonEmptyList.fromList(conditions) match {
      case Some(conds) =>
        val empty = ().validNel[String]
        conds.foldLeft(empty) { (acc: ValidatedNel[String, Unit], a: Condition) =>
          (checkConditionMatches(a), acc).mapN((_, _) => ())
        }

      case None =>
        "No conditions provided. At least one condition required in order to execute a merge.".invalidNel
    }
  }

  private given bsaMatcher: ConditionMatcher[Condition.BuildSuccessAll.type, List[BuildStatus]] = {
    case (condition, Nil) => "No build statuses. At least one required for this condition.".invalidNel
    case (condiition, input) =>
      input
        .map { bs =>
          if (bs.state.isSuccess) ().validNel
          else s"Build is not succesful: '${bs.name}'".invalidNel
        }
        .sequence
        .map(_ => ())
  }

  private given ConditionMatcher[Condition.Author, Author] =
    (condition, input) => ConditionOperator.matches(condition, input.underlying)

  private given ConditionMatcher[Condition.SourceBranch, BranchName] =
    (condition, input) => ConditionOperator.matches(condition, input.underlying)

  private given ConditionMatcher[Condition.TargetBranch, BranchName] =
    (condition, input) => ConditionOperator.matches(condition, input.underlying)

}
