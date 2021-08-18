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

import com.fgrutsch.emergence.core.vcs.bitbucketcloud.Encoding._
import com.fgrutsch.emergence.core.vcs.model._
import io.circe.{Encoder, Json}

final private[bitbucketcloud] case class MergePullRequestRequest(
    closeSourceBranch: Boolean,
    mergeStrategy: MergeStrategy
)

private[bitbucketcloud] object MergePullRequestRequest {

  implicit val mergePullRequestEncoder: Encoder[MergePullRequestRequest] = {
    Encoder.instance { m =>
      Json.obj(
        "close_source_branch" -> Json.fromBoolean(m.closeSourceBranch),
        "merge_strategy"      -> mergeStrategyEncoder(m.mergeStrategy)
      )
    }
  }

}
