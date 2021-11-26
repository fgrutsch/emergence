import sbt._
import sbtghactions.GenerativePlugin
import sbtghactions.GenerativePlugin.autoImport._
import sbtghactions.WorkflowStep._

object SetupGithubActionsPlugin extends AutoPlugin {

  override def requires: Plugins = GenerativePlugin
  override def trigger           = allRequirements
  override def buildSettings: Seq[Setting[_]] = Seq(
    githubWorkflowTargetTags ++= Seq("v*"),
    githubWorkflowJavaVersions := Seq("8", "11", "17"),
    githubWorkflowJobSetup := List(
      WorkflowStep.CheckoutFull,
      WorkflowStep.Use(
        UseRef.Public("actions", "setup-java", "v2"),
        name = Some("Setup Java and Scala"),
        params = Map("distribution" -> "temurin", "java-version" -> "${{ matrix.java }}")
      )
    ) ++ githubWorkflowGeneratedCacheSteps.value.toList,
    githubWorkflowBuild   := Seq(WorkflowStep.Sbt(List("codeVerify", "+test"))),
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
        UseRef.Public("docker", "login-action", "v1"),
        name = Some("Login to Docker Hub"),
        params = Map("username" -> "${{ secrets.DOCKER_USERNAME }}", "password" -> "${{ secrets.DOCKER_TOKEN }}")
      ),
      WorkflowStep.Run(List("sbt core/docker:publish"), name = Some("Publish docker image")),
      WorkflowStep.Run(
        List("sbt docs/makeSite"),
        name = Some("Generate documentation"),
        cond = Some("startsWith(github.ref, 'refs/tags/v')")
      ),
      WorkflowStep.Use(
        UseRef.Public("JamesIves", "github-pages-deploy-action", "4.1.6"),
        name = Some("Publish gh-pages"),
        cond = Some("startsWith(github.ref, 'refs/tags/v')"),
        params = Map("branch" -> "gh-pages", "folder" -> "docs/target/site")
      )
    )
  )

}
