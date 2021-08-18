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

import io.circe.Decoder

final private[bitbucketcloud] case class DiffStatResponse(status: String) {

  def isMergeable(): Boolean = status match {
    case "modified" => true
    case _          => false
  }

}

private[bitbucketcloud] object DiffStatResponse {

  implicit val diffStatsResponseDecoder: Decoder[DiffStatResponse] = Decoder.instance { c =>
    c.downField("status").as[String].map(DiffStatResponse(_))
  }

}
