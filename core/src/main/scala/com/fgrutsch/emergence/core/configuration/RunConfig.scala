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

import cats.data.NonEmptyList
import com.fgrutsch.emergence.core.configuration.EmergenceConfig.*
import com.fgrutsch.emergence.core.utils.config.*
import com.fgrutsch.emergence.core.vcs.model.Repository
import com.fgrutsch.emergence.core.vcs.model.Repository.*
import com.typesafe.config.Config
import io.circe.{Decoder, Error}

final case class RunConfig(
    repositories: NonEmptyList[RunConfig.RepositoryConfig],
    defaults: Option[EmergenceConfig]
)

object RunConfig {

  final case class RepositoryConfig(
      name: Repository,
      emergenceConfig: Option[EmergenceConfig]
  )

  object RepositoryConfig {
    given Decoder[RepositoryConfig] = Decoder.instance { c =>
      for {
        name            <- c.downField("name").as[Repository]
        emergenceConfig <- c.as[Option[EmergenceConfig]]
      } yield RepositoryConfig(name, emergenceConfig)
    }
  }

  def from(config: Config): Either[Error, RunConfig] = parse[RunConfig](config)

  given Decoder[RunConfig] = Decoder.instance { c =>
    for {
      repositories <- c.downField("repositories").as[NonEmptyList[RepositoryConfig]]
      defaults     <- c.downField("defaults").as[Option[EmergenceConfig]]
    } yield RunConfig(repositories, defaults)
  }

}
