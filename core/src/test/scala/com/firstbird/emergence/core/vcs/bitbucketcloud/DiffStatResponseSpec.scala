package com.fgrutsch.emergence.core.vcs.bitbucketcloud

import io.circe.parser.*
import testutil.BaseSpec

class DiffStatResponseSpec extends BaseSpec {

  test("isMergeable returns true if status contains 'modified'") {
    val response = DiffStatResponse("modified")
    response.isMergeable() mustBe { true }
  }

  test("isMergeable returns false if status does not contain 'modified'") {
    val response = DiffStatResponse("removed")
    response.isMergeable() mustBe { false }
  }

  test("decode json successfully") {
    val input = """{
        "status": "modified"
        }"""

    val result = parse(input).value.as[DiffStatResponse]
    result.value mustBe { DiffStatResponse("modified") }
  }

}
