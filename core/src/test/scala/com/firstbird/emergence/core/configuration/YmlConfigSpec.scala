package com.fgrutsch.emergence.core.configuration

import com.typesafe.config.ConfigFactory
import testutil.BaseSpec

class YmlConfigSpec extends BaseSpec {

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
