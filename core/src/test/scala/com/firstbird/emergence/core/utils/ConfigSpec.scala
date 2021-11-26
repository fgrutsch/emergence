package com.fgrutsch.emergence.core.utils

import cats.syntax.all.*
import com.fgrutsch.emergence.core.utils.config.configFromYaml
import com.fgrutsch.emergence.core.utils.config.given
import com.typesafe.config.ConfigFactory
import io.circe.{DecodingFailure, Json}
import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

import scala.concurrent.duration.*

class ConfigSpec extends BaseSpec with TableDrivenPropertyChecks {

  test("decode FiniteDuration successfully") {
    val table = Table(
      "input"     -> "expected",
      "2 seconds" -> 2.seconds.asRight,
      "1 minute"  -> 1.minute.asRight,
      "Inf"       -> DecodingFailure("Expected a finite duration.", Nil).asLeft,
      "-Inf"      -> DecodingFailure("Expected a finite duration.", Nil).asLeft,
      "invalid"   -> DecodingFailure("Invalid finite duration: format error invalid", Nil).asLeft
    )

    forAll(table) { case (input, expected) =>
      val jsonInput = Json.fromString(input)
      val result    = jsonInput.as[FiniteDuration]
      result mustBe { expected }
    }
  }

  test("configFromYml converts yml to typesafe config") {
    val yml = """
    |repositories:
    |   - name: owner/name
    |     conditions:
    |       - "target-branch == master"
    |     merge:
    |       strategy: merge-commit
    |       close_source_branch: false
    |
    |defaults:
    |   conditions:
    |     - "build-success-all"
    |     - "author == test"
    |   merge:
    |     strategy: squash
    |     close_source_branch: true
    """.stripMargin

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
    |   }
    |}
    """.stripMargin)

    val result = configFromYaml(yml)
    result.value mustBe { config }
  }

}
