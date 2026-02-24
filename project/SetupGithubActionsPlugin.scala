import sbt.*
import sbt.Keys.crossScalaVersions
import sbtghactions.GenerativePlugin
import sbtghactions.GenerativePlugin.autoImport.*
import sbtghactions.WorkflowStep.*

object SetupGithubActionsPlugin extends AutoPlugin {

  override def requires: Plugins              = GenerativePlugin
  override def trigger                        = allRequirements
  override def buildSettings: Seq[Setting[_]] = Seq(
    githubWorkflowPermissions := Some(
      Permissions.Specify(
        Map(PermissionScope.IdToken -> PermissionValue.Write, PermissionScope.Contents -> PermissionValue.Write)
      )
    ),
    githubWorkflowTargetTags ++= Seq("v*"),
    githubWorkflowJavaVersions := Seq(JavaSpec.temurin("21")),
    githubWorkflowBuild        := Seq(
      WorkflowStep.Sbt(List("codeVerify", "coverage", "test", "coverageReport", "coverageAggregate"))
    ),
    githubWorkflowBuildPostamble += WorkflowStep.Use(
      UseRef.Public("codecov", "codecov-action", "v5"),
      name = Some("Upload coverage to Codecov"),
      params = Map("fail_ci_if_error" -> "true", "use_oidc" -> "true")
    ),
    githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release"))),
    githubWorkflowPublishTargetBranches += RefPredicate.StartsWith(Ref.Tag("v")),
    githubWorkflowPublish := Seq(
      WorkflowStep.Sbt(
        List("ci-release"),
        env = Map(
          "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
          "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
          "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
          "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
        )
      )
    ),
    githubWorkflowPublishPostamble ++= List(
      WorkflowStep.Use(
        UseRef.Public("docker", "login-action", "v2"),
        name = Some("Login to Docker Hub"),
        params = Map("username" -> "${{ secrets.DOCKER_USERNAME }}", "password" -> "${{ secrets.DOCKER_TOKEN }}")
      ),
      WorkflowStep.Run(List("sbt core/docker:publish"), name = Some("Publish docker image")),
      WorkflowStep.Run(
        List("sbt docs/paradox"),
        name = Some("Generate documentation"),
        cond = Some("startsWith(github.ref, 'refs/tags/v')")
      ),
      WorkflowStep.Use(
        UseRef.Public("JamesIves", "github-pages-deploy-action", "v4"),
        name = Some("Publish gh-pages"),
        cond = Some("startsWith(github.ref, 'refs/tags/v')"),
        params = Map("folder" -> "docs/target/paradox/site/main")
      )
    )
  )

}
