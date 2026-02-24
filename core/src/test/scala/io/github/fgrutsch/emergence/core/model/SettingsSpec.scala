package io.github.fgrutsch.emergence.core.model

import cats.data.NonEmptyList
import cats.effect.IO
import cats.syntax.all._
import io.github.fgrutsch.emergence.core.app.CliOptions
import io.github.fgrutsch.emergence.core.condition.{Condition, ConditionOperator, ConditionValue, _}
import io.github.fgrutsch.emergence.core.configuration.RunConfig.RepositoryConfig
import io.github.fgrutsch.emergence.core.configuration.{EmergenceConfig, MergeConfig, RunConfig, _}
import io.github.fgrutsch.emergence.core.model.{Settings, VcsType}
import io.github.fgrutsch.emergence.core.vcs.model.{MergeStrategy, Repository, _}
import sttp.model.Uri._
import testutil.BaseSpec

import java.nio.file.Paths
import scala.concurrent.duration._

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
