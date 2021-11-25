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

package com.fgrutsch.emergence.core.vcs.model

sealed trait BuildStatusState {

  def isSuccess: Boolean = this match {
    case BuildStatusState.Success => true
    case _                        => false
  }

}

object BuildStatusState {
  case object Success    extends BuildStatusState
  case object InProgress extends BuildStatusState
  case object Failed     extends BuildStatusState
  case object Stopped    extends BuildStatusState
}
