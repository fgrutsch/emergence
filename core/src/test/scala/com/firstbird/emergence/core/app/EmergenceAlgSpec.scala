package com.fgrutsch.emergence.core.app

import com.fgrutsch.emergence.core.vcs.model.*
import testutil.*

class EmergenceAlgSpec extends BaseSpec {

  test("run should merge correct PRs") {
    val initial = TestState()

    val result = emergenceAlg.run
      .runS(initial)
      .unsafeRunSync()

    // PR #1 matches conditions and mergeChecks, #2 and #3 not
    result.mergedPrs mustBe {
      List(
        TestState.MergedPr(PullRequestNumber(1), MergeStrategy.MergeCommit, false)
      )
    }
  }

}
