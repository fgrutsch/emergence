/*
 * Copyright 2021 Emergence contributors
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

import com.fgrutsch.emergence.core.vcs.model.BranchName._
import com.fgrutsch.emergence.core.vcs.model.PullRequestNumber._
import com.fgrutsch.emergence.core.vcs.model.PullRequestTitle._
import com.fgrutsch.emergence.core.vcs.model._
import io.circe._

private[bitbucketcloud] object Encoding {

  implicit val pullRequestDecoder: Decoder[PullRequest] = Decoder.instance { c =>
    for {
      id               <- c.downField("id").as[PullRequestNumber]
      title            <- c.downField("title").as[PullRequestTitle]
      sourceBranchName <- c.downField("source").downField("branch").downField("name").as[BranchName]
      targetBranchName <- c.downField("destination").downField("branch").downField("name").as[BranchName]
      author           <- c.downField("author").downField("nickname").as[Author]
    } yield PullRequest(id, title, sourceBranchName, targetBranchName, author)
  }

  implicit val buildStatusDecoder: Decoder[BuildStatus] = Decoder.instance { c =>
    for {
      name  <- c.downField("name").as[BuildStatusName]
      state <- c.downField("state").as[BuildStatusState]
    } yield BuildStatus(name, state)
  }

  implicit val buildStatusStateDecoder: Decoder[BuildStatusState] = {
    Decoder[String].emap {
      case "SUCCESSFUL" => Right(BuildStatusState.Success)
      case "INPROGRESS" => Right(BuildStatusState.InProgress)
      case "FAILED"     => Right(BuildStatusState.Failed)
      case "STOPPED"    => Right(BuildStatusState.Stopped)
      case s            => Left(s"Unknown build status state: '$s'")
    }
  }

  implicit val mergeStrategyEncoder: Encoder[MergeStrategy] = Encoder.encodeString.contramap[MergeStrategy] {
    case MergeStrategy.MergeCommit => "merge_commit"
    case MergeStrategy.Squash      => "squash"
    case MergeStrategy.FastForward => "fast_forward"
  }

}
