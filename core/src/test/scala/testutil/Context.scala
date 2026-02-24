package testutil

import cats.data.NonEmptyList
import cats.effect.unsafe.IORuntime
import cats.syntax.all._
import io.github.fgrutsch.emergence.core.app.EmergenceAlg
import io.github.fgrutsch.emergence.core.condition.{Condition, ConditionMatcherAlg, ConditionOperator, ConditionValue}
import io.github.fgrutsch.emergence.core.configuration.RunConfig.RepositoryConfig
import io.github.fgrutsch.emergence.core.configuration.{
  EmergenceConfig,
  EmergenceConfigResolverAlg,
  MergeConfig,
  RunConfig
}
import io.github.fgrutsch.emergence.core.merge.MergeAlg
import io.github.fgrutsch.emergence.core.model.{Settings, VcsType}
import io.github.fgrutsch.emergence.core.vcs.VcsSettings.VcsUser
import io.github.fgrutsch.emergence.core.vcs.model.{MergeStrategy, Repository}
import io.github.fgrutsch.emergence.core.vcs.{MockVcsAlg, VcsAlg, VcsSettings}
import org.typelevel.log4cats.Logger
import sttp.model.Uri._

import scala.concurrent.duration._

private[testutil] trait Context {

  given IORuntime = IORuntime.global

  given settings: Settings = Settings(
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

  given vcsSettings: VcsSettings = settings.vcs

  given mockLogger: Logger[Eff]                         = new MockLogger
  given mockVcsAlg: VcsAlg[Eff]                         = new MockVcsAlg
  given configResolver: EmergenceConfigResolverAlg[Eff] = new EmergenceConfigResolverAlg[Eff](settings.config)
  given conditionMatcherAlg: ConditionMatcherAlg[Eff]   = new ConditionMatcherAlg[Eff]
  given mergeAlg: MergeAlg[Eff]                         = new MergeAlg[Eff]
  given emergenceAlg: EmergenceAlg[Eff]                 = new EmergenceAlg[Eff]

}
