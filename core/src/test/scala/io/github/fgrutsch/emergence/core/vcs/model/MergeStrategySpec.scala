package io.github.fgrutsch.emergence.core.vcs.model

import cats.syntax.all._
import io.circe.{DecodingFailure, Json}
import io.github.fgrutsch.emergence.core.vcs.model.MergeStrategy
import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

class MergeStrategySpec extends BaseSpec with TableDrivenPropertyChecks {

  test("decode MergeStrategy successfully") {
    val table = Table(
      "input"        -> "expected",
      "merge-commit" -> MergeStrategy.MergeCommit.asRight,
      "squash"       -> MergeStrategy.Squash.asRight,
      "fast-forward" -> MergeStrategy.FastForward.asRight,
      "invalid"      -> DecodingFailure("Invalid merge strategy: 'invalid'", Nil).asLeft
    )

    forAll(table) { case (input, expected) =>
      val jsonInput = Json.fromString(input)
      val result    = jsonInput.as[MergeStrategy]
      result mustBe { expected }
    }
  }

}
