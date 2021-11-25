package com.fgrutsch.emergence.core.condition

import cats.Id
import cats.syntax.all.*
import com.fgrutsch.emergence.core.condition.Condition.BuildSuccessAll
import com.fgrutsch.emergence.core.vcs.model.*
import testutil.BaseSpec

class ConditionMatcherAlgSpec extends BaseSpec {

  private val matcherAlg = new ConditionMatcherAlg[Id]

  test("checkConditions fails if empty list provided") {
    val conditions = Nil
    val input = Input(
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      ),
      BuildStatus(BuildStatusName("Testing"), BuildStatusState.Success) :: Nil
    )

    val result = matcherAlg.checkConditions(conditions, input)
    result mustBe { "No conditions provided. At least one condition required in order to execute a merge.".invalidNel }
  }

  test("checkConditions fails BuildSuccessAll condition with empty build statuses") {
    val conditions = BuildSuccessAll :: Nil
    val input = Input(
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      ),
      Nil
    )

    val result = matcherAlg.checkConditions(conditions, input)
    result mustBe { "No build statuses. At least one required for this condition.".invalidNel }
  }

  test("checkConditions fails BuildSuccessAll condition with at least one failing build") {
    val conditions = BuildSuccessAll :: Nil
    val input = Input(
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      ),
      List(
        BuildStatus(BuildStatusName("Building"), BuildStatusState.Success),
        BuildStatus(BuildStatusName("Testing"), BuildStatusState.Failed)
      )
    )

    val result = matcherAlg.checkConditions(conditions, input)
    result mustBe { "Build is not succesful: 'Testing'".invalidNel }
  }

  test("checkConditions succeeds BuildSuccessAll condition") {
    val conditions = BuildSuccessAll :: Nil
    val input = Input(
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      ),
      List(
        BuildStatus(BuildStatusName("Building"), BuildStatusState.Success),
        BuildStatus(BuildStatusName("Testing"), BuildStatusState.Success)
      )
    )

    val result = matcherAlg.checkConditions(conditions, input)
    result mustBe { ().validNel }
  }

  test("checkConditions fails Author condition if there is no match") {
    val conditions = Condition.Author(ConditionOperator.Equal, ConditionValue("abc")) :: Nil
    val input = Input(
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      ),
      Nil
    )

    val result = matcherAlg.checkConditions(conditions, input)
    result mustBe { "Input 'fgrutsch' doesn't match condition 'abc' with operator '=='".invalidNel }
  }

  test("checkConditions succeeds Author condition") {
    val conditions = Condition.Author(ConditionOperator.Equal, ConditionValue("fgrutsch")) :: Nil
    val input = Input(
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      ),
      Nil
    )

    val result = matcherAlg.checkConditions(conditions, input)
    result mustBe { ().validNel }
  }

  test("checkConditions fails SourceBranch condition if there is no match") {
    val conditions = Condition.SourceBranch(ConditionOperator.Equal, ConditionValue("abc")) :: Nil
    val input = Input(
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      ),
      Nil
    )

    val result = matcherAlg.checkConditions(conditions, input)
    result mustBe { "Input 'update/abc' doesn't match condition 'abc' with operator '=='".invalidNel }
  }

  test("checkConditions succeeds SourceBranch condition") {
    val conditions = Condition.SourceBranch(ConditionOperator.Equal, ConditionValue("update/abc")) :: Nil
    val input = Input(
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      ),
      Nil
    )

    val result = matcherAlg.checkConditions(conditions, input)
    result mustBe { ().validNel }
  }

  test("checkConditions fails TargetBranch§ condition if there is no match") {
    val conditions = Condition.TargetBranch(ConditionOperator.Equal, ConditionValue("abc")) :: Nil
    val input = Input(
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      ),
      Nil
    )

    val result = matcherAlg.checkConditions(conditions, input)
    result mustBe { "Input 'master' doesn't match condition 'abc' with operator '=='".invalidNel }
  }

  test("checkConditions succeeds TargetBranch condition") {
    val conditions = Condition.TargetBranch(ConditionOperator.Equal, ConditionValue("master")) :: Nil
    val input = Input(
      PullRequest(
        PullRequestNumber(1),
        PullRequestTitle("Test"),
        BranchName("update/abc"),
        BranchName("master"),
        Author("fgrutsch")
      ),
      Nil
    )

    val result = matcherAlg.checkConditions(conditions, input)
    result mustBe { ().validNel }
  }

}
