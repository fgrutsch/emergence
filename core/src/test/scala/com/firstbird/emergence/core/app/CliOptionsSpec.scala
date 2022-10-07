package com.fgrutsch.emergence.core.app

import cats.data.NonEmptyList
import cats.syntax.all.*
import com.fgrutsch.emergence.core.app.CliOptions.*
import com.fgrutsch.emergence.core.condition.*
import com.fgrutsch.emergence.core.configuration.RunConfig.RepositoryConfig
import com.fgrutsch.emergence.core.configuration.{EmergenceConfig, MergeConfig, RunConfig}
import com.fgrutsch.emergence.core.model.VcsType
import com.fgrutsch.emergence.core.vcs.model.{MergeStrategy, Repository}
import com.monovore.decline.Command
import sttp.model.Uri.*
import testutil.BaseSpec

import java.nio.file.Paths
import scala.concurrent.duration.*

class CliOptionsSpec extends BaseSpec {

  private val testCommand = Command("test-cmd", "test-header", false)(CliOptions.declineOpts)

  test("parse CliOptions") {
    val args = List(
      List("--config", "core/src/test/resources/test-runConfig.yml"),
      List("--vcs-type", "bitbucket-cloud"),
      List("--vcs-api-host", "http://localhost"),
      List("--vcs-login", "demo"),
      List("--git-ask-pass", "core/src/test/resources/test-gitAsk.sh")
    ).flatten

    val result = testCommand.parse(args).value

    result mustBe {
      CliOptions(
        RunConfig(
          NonEmptyList.one(
            RepositoryConfig(
              Repository("fgrutsch", "test"),
              EmergenceConfig.default.some
            )
          ),
          EmergenceConfig(
            Condition.BuildSuccessAll :: Nil,
            MergeConfig(MergeStrategy.Squash.some, true.some, 1.second.some).some
          ).some
        ),
        VcsType.BitbucketCloud,
        uri"http://localhost",
        "demo",
        Paths.get("core", "src", "test", "resources", "test-gitAsk.sh"),
        ".emergence.yml"
      )
    }
  }

  test("parse fails on invalid --config option") {
    val args = List(
      List("--config", "core/src/test/resources/non-existing.yml"),
      List("--vcs-type", "bitbucket-cloud"),
      List("--vcs-api-host", "http://localhost"),
      List("--vcs-login", "demo"),
      List("--git-ask-pass", "core/src/test/resources/test-gitAsk.sh")
    ).flatten

    val result = testCommand.parse(args).left.value

    result.errors mustBe {
      List("Unable to read run config: core/src/test/resources/non-existing.yml")
    }
  }

  test("parse fails on invalid --vcs-type option") {
    val args = List(
      List("--config", "core/src/test/resources/test-runConfig.yml"),
      List("--vcs-type", "bitbucket-clouda"),
      List("--vcs-api-host", "http://localhost"),
      List("--vcs-login", "demo"),
      List("--git-ask-pass", "core/src/test/resources/test-gitAsk.sh")
    ).flatten

    val result = testCommand.parse(args).left.value

    result.errors mustBe {
      List("Value must be one of: bitbucket-cloud or github")
    }
  }

  test("parse fails on invalid --git-ask-pass option") {
    val args = List(
      List("--config", "core/src/test/resources/test-runConfig.yml"),
      List("--vcs-type", "bitbucket-cloud"),
      List("--vcs-api-host", "://"),
      List("--vcs-login", "demo"),
      List("--git-ask-pass", "core/src/test/resources/non-existing.sh")
    ).flatten

    val result = testCommand.parse(args).left.value

    result.errors mustBe {
      List("Not a valid file.")
    }
  }

}
