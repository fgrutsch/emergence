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

package com.fgrutsch.emergence.core.configuration

import com.fgrutsch.emergence.core.utils.config._
import com.fgrutsch.emergence.core.vcs.model.MergeStrategy
import io.circe.Decoder

import scala.concurrent.duration._

final case class MergeConfig(
    strategy: Option[MergeStrategy],
    closeSourceBranch: Option[Boolean],
    throttle: Option[FiniteDuration]
)

object MergeConfig {

  object Default {
    val strategy: MergeStrategy    = MergeStrategy.Squash
    val closeSourceBranch: Boolean = true
    val throttle: FiniteDuration   = 0.seconds
  }

  implicit val mergeConfigDecoder: Decoder[MergeConfig] = Decoder.instance { c =>
    for {
      strategy          <- c.downField("strategy").as[Option[MergeStrategy]]
      closeSourceBranch <- c.downField("close_source_branch").as[Option[Boolean]]
      throttle          <- c.downField("throttle").as[Option[FiniteDuration]]
    } yield MergeConfig(strategy, closeSourceBranch, throttle)
  }

}
