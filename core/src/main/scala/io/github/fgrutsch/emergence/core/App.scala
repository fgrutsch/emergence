/*
 * Copyright 2026 Emergence contributors
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

package io.github.fgrutsch.emergence.core

import cats.effect.{ExitCode, IO}
import cats.syntax.all._
import com.monovore.decline._
import com.monovore.decline.effect.CommandIOApp
import io.github.fgrutsch.BuildInfo
import io.github.fgrutsch.emergence.core.app.CliOptions._
import io.github.fgrutsch.emergence.core.app.{CliOptions, EmergenceContext, _}

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
