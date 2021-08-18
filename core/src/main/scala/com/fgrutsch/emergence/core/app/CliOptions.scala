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

package com.fgrutsch.emergence.core.app

import caseapp.core.Error.MalformedValue
import caseapp.core.argparser.{ArgParser, SimpleArgParser}
import caseapp.{AppName, AppVersion, HelpMessage, ProgName, ValueDescription}
import cats.syntax.all._
import com.fgrutsch.emergence.BuildInfo
import com.fgrutsch.emergence.core.configuration._
import com.fgrutsch.emergence.core.model.VcsType
import sttp.model.Uri

import java.nio.file.{Files, Path, Paths}
import scala.util.Try

@AppName(BuildInfo.appName)
@AppVersion(BuildInfo.version)
@ProgName(BuildInfo.cliName)
final case class CliOptions(
    @HelpMessage("The path to the eMERGEnce run config file.")
    config: RunConfig,
    @HelpMessage(s"The type of VCS you want to run eMERGEnce.")
    @ValueDescription(s"${VcsType.values.mkString(" or ")}")
    vcsType: VcsType,
    @HelpMessage("The base URI for VCS api calls. e.g. https://api.bitbucket.org/2.0")
    vcsApiHost: Uri,
    @HelpMessage("The username for authenticating VCS API calls.")
    vcsLogin: String,
    @HelpMessage(
      "The path to the executable script file that returns your VCS secret for authenticating VCS API calls.")
    gitAskPass: Path,
    @HelpMessage("The name/path of the eMERGEnce config file inside the repository. Default: .emergence.yml")
    repoConfigName: String = ".emergence.yml"
)

object CliOptions {

  implicit val pathParser: ArgParser[Path] = SimpleArgParser.from[Path]("path") { s =>
    Try(Paths.get(s))
      .flatMap(path => Try(path.toFile.isFile).map(isFile => (path, isFile)))
      .toEither
      .leftMap(t => MalformedValue("Path", s"Unable to check if path is a file: ${t.getMessage}"))
      .flatMap { case (path, isFile) =>
        Either.cond(isFile, path, MalformedValue("Path", "Not a valid file."))
      }
  }

  implicit val vcsTypeParser: ArgParser[VcsType] = SimpleArgParser.from[VcsType]("vcs-type") { s =>
    VcsType.values
      .find(_.underlying.toLowerCase == s)
      .toRight(MalformedValue("VcsType", s"Value must be one of: ${VcsType.values.map(_.underlying).mkString(" or ")}"))
  }

  implicit val uriParser: ArgParser[Uri] = SimpleArgParser.from[Uri]("uri") { s =>
    Uri
      .parse(s)
      .leftMap(MalformedValue("Uri", _))
  }

  implicit val runConfigParser: ArgParser[RunConfig] = SimpleArgParser.from[RunConfig]("path") { s =>
    Try(Paths.get(s))
      .flatMap(p => Try(new String(Files.readAllBytes(p))))
      .toEither
      .flatMap(configFromYaml(_))
      .flatMap(RunConfig.from(_))
      .leftMap(t => MalformedValue("RunConfig", s"Unable to read run config: ${t.getMessage}"))
  }

}
