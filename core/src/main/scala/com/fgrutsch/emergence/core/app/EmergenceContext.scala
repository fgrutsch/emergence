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

package com.fgrutsch.emergence.core.app

import cats.effect.{Resource, *}
import com.fgrutsch.emergence.core.condition.ConditionMatcherAlg
import com.fgrutsch.emergence.core.configuration.EmergenceConfigResolverAlg
import com.fgrutsch.emergence.core.merge.MergeAlg
import com.fgrutsch.emergence.core.model.Settings
import com.fgrutsch.emergence.core.vcs.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.*
import sttp.client3.*
import cats.syntax.all.*
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client3.logging.slf4j.Slf4jLoggingBackend


object EmergenceContext {

  
  def apply[F[_]: Async: Sync](options: CliOptions): Resource[F, EmergenceAlg[F]] = {
    val l = Slf4jFactory[F].getLogger

    for {
      given Logger[F]           <- Resource.liftK(Slf4jLogger.create[F])
      b: SttpBackend[F, Any] <- AsyncHttpClientCatsBackend.resource[F]()
      given SttpBackend[F, Any] <- Resource.liftK(Slf4jLoggingBackend(b, logResponseBody = true).pure[F])
      given Settings            <- Resource.liftK(Settings.from[F](options))
    } yield {
      val settings                        = summon[Settings]
      val vcsFactory                      = new VcsFactory
      given VcsAlg[F]                     = vcsFactory.getVcs(settings)
      given EmergenceConfigResolverAlg[F] = new EmergenceConfigResolverAlg[F](settings.config)
      given ConditionMatcherAlg[F]        = new ConditionMatcherAlg[F]
      given MergeAlg[F]                   = new MergeAlg[F]
      new EmergenceAlg
    }
  }

}
