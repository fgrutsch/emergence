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

package com.fgrutsch.emergence.core

import cats.effect.{ExitCode, IO}
import cats.syntax.all.*
import com.fgrutsch.emergence.BuildInfo
import com.fgrutsch.emergence.core.app.CliOptions.*
import com.fgrutsch.emergence.core.app.{CliOptions, *}
import com.monovore.decline.*
import com.monovore.decline.effect.CommandIOApp

object App
    extends CommandIOApp(
      name = "emergence",
      header = s"eMERGEnce ${BuildInfo.Version}",
      version = BuildInfo.Version
    ) {

  override def main: Opts[IO[ExitCode]] = {
    CliOptions.declineOpts.map { options =>
      EmergenceContext[IO](options).use(_.run)
    }
  }
}
