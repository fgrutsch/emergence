package com.fgrutsch.emergence.core.model

import cats.data.NonEmptyList
import cats.effect.IO
import cats.syntax.all.*
import com.fgrutsch.emergence.core.app.CliOptions
import com.fgrutsch.emergence.core.condition.*
import com.fgrutsch.emergence.core.configuration.RunConfig.RepositoryConfig
import com.fgrutsch.emergence.core.configuration.{RunConfig, *}
import com.fgrutsch.emergence.core.model.{Settings, VcsType}
import com.fgrutsch.emergence.core.vcs.model.*
import sttp.model.Uri.*
import testutil.BaseSpec

import java.nio.file.Paths
import scala.concurrent.duration.*

class SettingsSpec extends BaseSpec {

  test("from CliOptions to Settings") {
    val cliOptions = CliOptions(
      RunConfig(
        NonEmptyList.one(
          RepositoryConfig(Repository("fgrutsch", "test"), none)
        ),
        EmergenceConfig(
          List(
            Condition.BuildSuccessAll,
            Condition.Author(ConditionOperator.Equal, ConditionValue("fgrutsch"))
          ),
          MergeConfig(
            MergeStrategy.MergeCommit.some,
            false.some,
            1.second.some
          ).some
        ).some
      ),
      VcsType.BitbucketCloud,
      uri"http://localhost",
      "demo",
      Paths.get("core", "src", "test", "resources", "test-gitAsk.sh"),
      ".emergence.yml"
    )

    Settings.from[IO](cliOptions).unsafeRunSync() mustBe { settings }
  }

}
