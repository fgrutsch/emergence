package testutil

import cats.data.NonEmptyList
import cats.effect.unsafe.IORuntime
import cats.syntax.all._
import com.fgrutsch.emergence.core.app._
import com.fgrutsch.emergence.core.condition.{ConditionOperator, ConditionValue, _}
import com.fgrutsch.emergence.core.configuration.RunConfig.RepositoryConfig
import com.fgrutsch.emergence.core.configuration.{EmergenceConfig, MergeConfig, _}
import com.fgrutsch.emergence.core.merge.MergeAlg
import com.fgrutsch.emergence.core.model.{Settings, VcsType}
import com.fgrutsch.emergence.core.vcs.VcsSettings.VcsUser
import com.fgrutsch.emergence.core.vcs.model.{MergeStrategy, Repository}
import com.fgrutsch.emergence.core.vcs.{VcsSettings, _}
import sttp.model.Uri._

import scala.concurrent.duration._

private[testutil] trait Context {

  implicit val runtime = IORuntime.global

  implicit val settings = Settings(
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
    VcsSettings(
      uri"http://localhost",
      VcsUser("demo", "secret"),
      ".emergence.yml"
    )
  )

  implicit val vcsSettings = settings.vcs

  implicit val mockLogger          = new MockLogger
  implicit val mockVcsAlg          = new MockVcsAlg
  implicit val configResolver      = new EmergenceConfigResolverAlg[Eff](settings.config)
  implicit val conditionMatcherAlg = new ConditionMatcherAlg[Eff]
  implicit val mergeAlg            = new MergeAlg[Eff]
  implicit val emergenceAlg        = new EmergenceAlg[Eff]

}
