package com.fgrutsch.emergence.core.vcs.model

import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

class BuildStatusStateSpec extends BaseSpec with TableDrivenPropertyChecks {

  test("isSuccess returns true on Success") {
    val table = Table(
      "input"                     -> "expected",
      BuildStatusState.Success    -> true,
      BuildStatusState.InProgress -> false,
      BuildStatusState.Failed     -> false,
      BuildStatusState.Stopped    -> false
    )

    forAll(table) { case (input, expected) =>
      input.isSuccess mustBe { expected }
    }
  }

  test("isFailure returns true if no Success") {
    val table = Table(
      "input"                     -> "expected",
      BuildStatusState.Success    -> true,
      BuildStatusState.InProgress -> false,
      BuildStatusState.Failed     -> false,
      BuildStatusState.Stopped    -> false
    )

    forAll(table) { case (input, expected) =>
      input.isSuccess mustBe { expected }
    }
  }

}
