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

package com.fgrutsch.emergence.core.model

import cats.Functor
import cats.effect.Sync
import cats.syntax.all.*
import com.fgrutsch.emergence.core.app.CliOptions
import com.fgrutsch.emergence.core.configuration.RunConfig
import com.fgrutsch.emergence.core.model.VcsType
import com.fgrutsch.emergence.core.vcs.VcsSettings

import scala.sys.process.Process

final case class Settings(
    config: RunConfig,
    vcsType: VcsType,
    vcs: VcsSettings
)

object Settings {

  def from[F[_]: Functor](options: CliOptions)(using F: Sync[F]): F[Settings] = {
    val vcsSettings = F.delay {
      val secret = Process(options.gitAskPass.toString).!!.trim
      val user   = VcsSettings.VcsUser(options.vcsLogin, secret)
      VcsSettings(options.vcsApiHost, user, options.repoConfigName)
    }

    vcsSettings
      .map(settings => Settings(options.config, options.vcsType, settings))
  }

}
