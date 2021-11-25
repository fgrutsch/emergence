package com.fgrutsch.emergence.core.vcs.model

import cats.syntax.all.*
import com.fgrutsch.emergence.core.vcs.model.Repository
import io.circe.{DecodingFailure, Json}
import org.scalatest.prop.TableDrivenPropertyChecks
import testutil.BaseSpec

class RepositorySpec extends BaseSpec with TableDrivenPropertyChecks {

  test("decode Repository successfully") {
    val table = Table(
      "input"                -> "expected",
      "repo-owner/repo-name" -> Repository("repo-owner", "repo-name").asRight,
      "invalid"              -> DecodingFailure("Invalid repository format: 'invalid'", Nil).asLeft
    )

    forAll(table) { case (input, expected) =>
      val jsonInput = Json.fromString(input)
      val result    = jsonInput.as[Repository]
      result mustBe { expected }
    }
  }

  test("toString formats correctly") {
    val repo = Repository("repo-owner", "repo-name")
    repo.toString mustBe { "repo-owner/repo-name" }
  }

}
