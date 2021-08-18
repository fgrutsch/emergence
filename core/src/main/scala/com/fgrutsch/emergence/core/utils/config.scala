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

package com.fgrutsch.emergence.core.utils

import io.circe.Decoder

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object config {
  implicit val finiteDurationDecoder: Decoder[FiniteDuration] = Decoder.decodeString
    .map(s => Try(Duration(s)))
    .flatMap {
      case Success(v: FiniteDuration) => Decoder.const(v)
      case Success(v: Duration)       => Decoder.failedWithMessage(s"Expected a finite duration.")
      case Failure(t)                 => Decoder.failedWithMessage(s"Invalid finite duration: ${t.getMessage}")
    }
}
