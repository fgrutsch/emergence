package com.fgrutsch.emergence.core.vcs.bitbucketcloud

import io.circe.parser.*
import testutil.BaseSpec

class PageSpec extends BaseSpec {

  test("decode json successfully") {
    val input = """{
        "values": ["test"]
    }"""

    val result = parse(input).value.as[Page[String]]
    result.value mustBe { Page("test" :: Nil) }
  }

}
