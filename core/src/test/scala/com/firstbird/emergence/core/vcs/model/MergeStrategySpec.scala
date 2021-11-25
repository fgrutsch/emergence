package com.fgrutsch.emergence.core.vcs.bitbucketcloud

import cats.syntax.all.*
import com.fgrutsch.emergence.core.vcs.model.MergeStrategy
import io.circe.{DecodingFailure, Json}
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
