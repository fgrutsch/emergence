package com.fgrutsch.emergence.core.merge

import cats.syntax.all.*
import com.fgrutsch.emergence.core.condition.*
import com.fgrutsch.emergence.core.configuration.*
import com.fgrutsch.emergence.core.vcs.model.*
import testutil.*

import scala.concurrent.duration.*

class MergeAlgSpec extends BaseSpec {

  test("mergePullRequests") {
    val initial = TestState()

    val emergenceConfig = EmergenceConfig(
      List(
        Condition.BuildSuccessAll,
        Condition.Author(ConditionOperator.Equal, ConditionValue("fgrutsch"))
      ),
      MergeConfig(
        MergeStrategy.MergeCommit.some,
        false.some,
        2.seconds.some
      ).some
    )

    val result = mergeAlg
      .mergePullRequests(Repository("fgrutsch", "test"), emergenceConfig)
      .runS(initial)
      .unsafeRunSync()

    // PR #1 matches conditions and mergeChecks, #2 and #3 not
    result.mergedPrs mustBe {
      List(
        TestState.MergedPr(PullRequestNumber(1), MergeStrategy.MergeCommit, false)
      )
    }

    result.logs must {
      contain.allOf(
        none -> "******************** Processing pull request #1 ********************",
        none -> "Pull request matches all configured conditions.",
        none -> "******************** Processing pull request #2 ********************",
        none -> "Ignoring pull request as not all conditions match: \n                 - Build is not succesful: 'Build and Test'",
        none -> "******************** Processing pull request #3 ********************",
        none -> "Ignoring as merge check for PR failed. Reason: failed"
      )
    }
  }

}
