package com.fgrutsch.emergence.core.vcs.bitbucketcloud

import com.fgrutsch.emergence.core.vcs.bitbucketcloud.MergePullRequestRequest.*
import com.fgrutsch.emergence.core.vcs.model.MergeStrategy
import io.circe.parser.*
import io.circe.syntax.*
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
