package com.fgrutsch.emergence.core.condition

import cats.syntax.all.*
import com.fgrutsch.emergence.core.condition.Condition.BuildSuccessAll
import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

class ConditionSpec extends BaseSpec with TableDrivenPropertyChecks {

  test("parse") {

    val table = Table(
      "input"                  -> "expected",
      "build-success-all"      -> BuildSuccessAll.asRight,
      "abc"                    -> "Not a valid condition: 'abc'".asLeft,
      "author == fgrutsch"     -> Condition.Author(ConditionOperator.Equal, ConditionValue("fgrutsch")).asRight,
      "author ^$ .*fg.*"       -> Condition.Author(ConditionOperator.RegEx, ConditionValue(".*fg.*")).asRight,
      "source-branch == x"     -> Condition.SourceBranch(ConditionOperator.Equal, ConditionValue("x")).asRight,
      "source-branch ^$ .*x.*" -> Condition.SourceBranch(ConditionOperator.RegEx, ConditionValue(".*x.*")).asRight,
      "target-branch == x"     -> Condition.TargetBranch(ConditionOperator.Equal, ConditionValue("x")).asRight,
      "target-branch ^$ .*x.*" -> Condition.TargetBranch(ConditionOperator.RegEx, ConditionValue(".*x.*")).asRight
    )

    forAll(table) { case (input, expected) =>
      Condition.parse(input) mustBe { expected }
    }
  }

}
