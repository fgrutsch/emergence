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

package com.fgrutsch.emergence.core.vcs

import cats.MonadThrow
import com.fgrutsch.emergence.core.model.{Settings, VcsType}
import com.fgrutsch.emergence.core.vcs.bitbucketcloud.*
import com.fgrutsch.emergence.core.vcs.github.GithubVcs
import sttp.client3.SttpBackend

final class VcsFactory[F[_]: MonadThrow](using SttpBackend[F, Any]) {

  def getVcs(settings: Settings): VcsAlg[F] = settings.vcsType match {
    case VcsType.BitbucketCloud => bitbucketCloud(using settings.vcs)
    case VcsType.Github         => github(using settings.vcs)
  }

  private def bitbucketCloud(using vcsSettings: VcsSettings): VcsAlg[F] = {
    new BitbucketCloudVcs()
  }

  private def github(using vcsSettings: VcsSettings): VcsAlg[F] = {
    new GithubVcs()
  }

}
