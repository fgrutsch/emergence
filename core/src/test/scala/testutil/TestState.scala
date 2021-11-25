package testutil

import com.fgrutsch.emergence.core.vcs.model.*

object TestState {

  final case class MergedPr(
      number: PullRequestNumber,
      strategy: MergeStrategy,
      closeSourceBranch: Boolean
  )

}

final case class TestState(
    repoEmergenceConfigFile: Option[RepoFile] = None,
    mergedPrs: List[TestState.MergedPr] = Nil,
    logs: List[(Option[Throwable], String)] = List.empty
) {

  def addMergedPr(pr: TestState.MergedPr): TestState =
    copy(mergedPrs = mergedPrs :+ pr)

  def addLog(maybeThrowable: Option[Throwable], msg: String): TestState =
    copy(logs = logs :+ ((maybeThrowable, msg)))

}
