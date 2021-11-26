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

import cats.data.*
import cats.syntax.all.*
import com.fgrutsch.emergence.core.configuration.*
import com.fgrutsch.emergence.core.model.VcsType
import com.fgrutsch.emergence.core.utils.config.configFromYaml
import com.monovore.decline.{Argument, Opts}
import sttp.model.Uri

import java.nio.file.{Files, Path, Paths}
import scala.util.Try

final case class CliOptions(
    config: RunConfig,
    vcsType: VcsType,
    vcsApiHost: Uri,
    vcsLogin: String,
    gitAskPass: Path,
    repoConfigName: String
)

object CliOptions {

  private given Argument[RunConfig] = new Argument[RunConfig] {
    override def read(string: String): ValidatedNel[String, RunConfig] = {
      Try(Paths.get(string))
        .flatMap(p => Try(new String(Files.readAllBytes(p))))
        .toEither
        .flatMap(configFromYaml(_))
        .flatMap(RunConfig.from(_))
        .leftMap(t => s"Unable to read run config: ${t.getMessage}")
        .toValidatedNel
    }
    override def defaultMetavar: String = "path"
  }

  private given Argument[VcsType] = new Argument[VcsType] {
    override def read(string: String): ValidatedNel[String, VcsType] = {
      VcsType.values
        .find(_.underlying.toLowerCase == string)
        .toValidNel(s"Value must be one of: ${VcsType.values.map(_.underlying).mkString(" or ")}")
    }
    override def defaultMetavar: String = VcsType.values.map(_.underlying).mkString(" or ")
  }

  private given Argument[Uri] = new Argument[Uri] {
    override def read(string: String): ValidatedNel[String, Uri] = {
      Uri.parse(string).toValidatedNel
    }
    override def defaultMetavar: String = "uri"
  }

  private given Argument[Path] = new Argument[Path] {
    override def read(string: String): ValidatedNel[String, Path] = {
      Try(Paths.get(string))
        .flatMap(path => Try(path.toFile.isFile).map(isFile => (path, isFile)))
        .toEither
        .leftMap(t => s"Unable to check if path is a file: ${t.getMessage}")
        .flatMap { case (path, isFile) => Either.cond(isFile, path, "Not a valid file.") }
        .toValidatedNel
    }

    override def defaultMetavar: String = "path"
  }

  private val declineOptsTuple = (
    Opts.option[RunConfig]("config", "The path to the eMERGEnce run config file."),
    Opts.option[VcsType]("vcs-type", "The type of VCS you want to run eMERGEnce."),
    Opts.option[Uri]("vcs-api-host", "The base URI for VCS api calls. e.g. https://api.bitbucket.org/2.0"),
    Opts.option[String]("vcs-login", "The username for authenticating VCS API calls."),
    Opts.option[Path](
      "git-ask-pass",
      "The path to the executable script file that returns your VCS secret for authenticating VCS API calls."
    ),
    Opts
      .option[String](
        "repo-config-name",
        "The name/path of the eMERGEnce config file inside the repository. Default: .emergence.yml"
      )
      .withDefault(".emergence.yml")
  )

  val declineOpts = declineOptsTuple.tupled.map(CliOptions.apply.tupled(_))

}
