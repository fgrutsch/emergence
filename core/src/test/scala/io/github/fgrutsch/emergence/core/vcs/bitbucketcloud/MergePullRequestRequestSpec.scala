package io.github.fgrutsch.emergence.core.vcs.bitbucketcloud

import io.circe.parser._
import io.circe.syntax._
import io.github.fgrutsch.emergence.core.vcs.model.MergeStrategy
import testutil.BaseSpec

class MergePullRequestRequestSpec extends BaseSpec {

  test("encode json successfully") {
    val input = MergePullRequestRequest(true, MergeStrategy.Squash)

    val result = input.asJson

    result mustBe {
      parse("""{
        "close_source_branch": true,
        "merge_strategy": "squash"
    }""").value
    }
  }

}
