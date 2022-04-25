/*
 * Copyright 2022 Emergence contributors
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

import cats.syntax.all.*
import com.fgrutsch.emergence.core.condition.Condition
import com.fgrutsch.emergence.core.utils.config.*
import com.typesafe.config.Config
import io.circe.{Decoder, Error}

final case class EmergenceConfig(
    conditions: List[Condition],
    merge: Option[MergeConfig]
)

object EmergenceConfig {

  val default: EmergenceConfig = EmergenceConfig(Nil, none)

  def from(config: Config): Either[Error, EmergenceConfig] = parse[EmergenceConfig](config)

  given Decoder[EmergenceConfig] = Decoder.instance { c =>
    for {
      conditions <- c.downField("conditions").as[Option[List[Condition]]]
      merge      <- c.downField(("merge")).as[Option[MergeConfig]]
    } yield EmergenceConfig(conditions.getOrElse(Nil), merge)

  }

}
