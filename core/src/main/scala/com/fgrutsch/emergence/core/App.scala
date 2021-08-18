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

import caseapp.cats.IOCaseApp
import caseapp.core.RemainingArgs
import cats.effect.{ExitCode, IO}
import com.fgrutsch.emergence.core.app.CliOptions._
import com.fgrutsch.emergence.core.app.{CliOptions, _}

object App extends IOCaseApp[CliOptions] {

  def run(options: CliOptions, args: RemainingArgs): IO[ExitCode] = {
    EmergenceContext[IO](options).use(_.run)
  }

}
