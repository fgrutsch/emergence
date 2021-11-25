package com.fgrutsch.emergence.core.vcs.bitbucketcloud

import cats.data.NonEmptyList
import cats.syntax.all.*
import com.fgrutsch.emergence.core.condition.*
import com.fgrutsch.emergence.core.configuration.RunConfig.RepositoryConfig
import com.fgrutsch.emergence.core.configuration.{EmergenceConfig, MergeConfig, RunConfig}
import com.fgrutsch.emergence.core.vcs.model.{MergeStrategy, Repository}
import com.typesafe.config.ConfigFactory
import testutil.BaseSpec

import scala.concurrent.duration.*

class RunConfigSpec extends BaseSpec {

  test("from reads settings from typesafe Config") {
    val config = ConfigFactory.parseString("""
    |repositories = [
    |   {
    |       "name" = "owner/name"
    |       "conditions" = [
    |           "target-branch == master"
    |       ]
    |       "merge" {
    |           "strategy" = "merge-commit"
    |           "close_source_branch" = false
    |           "throttle" = "5 seconds"
    |       }
    |   }
    |]
    |
    |defaults = {
    |   conditions = [
    |       "build-success-all",
    |       "author == test"
    |   ]
    |   merge {
    |       "strategy" = "squash"
    |       "close_source_branch" = true
    |       "throttle" = "1 second"
    |   }
    |}
    """.stripMargin)

    val result = RunConfig.from(config)
    result mustBe {
      RunConfig(
        NonEmptyList.of(
          RepositoryConfig(
            Repository("owner", "name"),
            EmergenceConfig(
              Condition.TargetBranch(ConditionOperator.Equal, ConditionValue("master")) :: Nil,
              MergeConfig(MergeStrategy.MergeCommit.some, false.some, 5.seconds.some).some
            ).some
          )
        ),
        EmergenceConfig(
          Condition.BuildSuccessAll :: Condition.Author(ConditionOperator.Equal, ConditionValue("test")) :: Nil,
          MergeConfig(MergeStrategy.Squash.some, true.some, 1.second.some).some
        ).some
      ).asRight
    }
  }

}
