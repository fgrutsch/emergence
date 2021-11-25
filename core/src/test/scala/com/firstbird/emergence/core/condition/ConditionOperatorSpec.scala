package com.fgrutsch.emergence.core.condition

import cats.syntax.all.*
import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

class ConditionOperatorSpec extends BaseSpec with TableDrivenPropertyChecks {

  test("unapply") {
    val table = Table(
      "input" -> "expected",
      "=="    -> ConditionOperator.Equal.some,
      "^$"    -> ConditionOperator.RegEx.some,
      "xxx"   -> none
    )

    forAll(table) { case (input, expected) =>
      ConditionOperator.unapply(input) mustBe { expected }
    }
  }

  test("matches") {

    val table = Table(
      ("condition", "input", "expected"),
      (
        Condition.Author(ConditionOperator.Equal, ConditionValue("abc")),
        "abc",
        ().validNel
      ),
      (
        Condition.Author(ConditionOperator.Equal, ConditionValue("abc")),
        "a",
        "Input 'a' doesn't match condition 'abc' with operator '=='".invalidNel
      ),
      (
        Condition.Author(ConditionOperator.RegEx, ConditionValue("^abc.*$")),
        "abc-xxx",
        ().validNel
      ),
      (
        Condition.Author(ConditionOperator.RegEx, ConditionValue("^abc.*$")),
        "abx",
        "Input 'abx' doesn't match condition '^abc.*$' with operator '^$'".invalidNel
      )
    )

    forAll(table) { case (condition, input, expected) =>
      ConditionOperator.matches(condition, input) mustBe { expected }
    }
  }

}
