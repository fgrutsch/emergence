import java.time.LocalDate

ThisBuild / scalafixDependencies += Dependencies.organizeimports
ThisBuild / dynverSeparator := "-" // Default uses '+' which is not valid for docker tags
ThisBuild / scalaVersion    := "3.1.2"

addCommandAlias("codeFmt", ";headerCreate;scalafmtAll;scalafmtSbt;scalafixAll")
addCommandAlias("codeVerify", ";scalafmtCheckAll;scalafmtSbtCheck;scalafixAll --check;headerCheck")

lazy val commonSettings = Seq(
  organization           := "com.fgrutsch.emergence",
  sonatypeCredentialHost := "s01.oss.sonatype.org",
  sonatypeRepository     := "https://s01.oss.sonatype.org/service/local",
  sonatypeProfileName    := "com.fgrutsch",
  startYear              := Some(2020),
  homepage               := Some(url("https://github.com/fgrutsch/emergence")),
  licenses               := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  scmInfo := Some(
    ScmInfo(homepage.value.get, "scm:git:https://github.com/fgrutsch/emergence.git")
  ),
  developers += Developer(
    "contributors",
    "Contributors",
    "",
    url("https://github.com/fgrutsch/emergence/graphs/contributors")
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "utf-8",
    "-explain-types",
    "-feature",
    "-language:higherKinds",
    "-unchecked",
    "-Ysafe-init",
    "-Xfatal-warnings"
  ),
  headerLicense     := Some(HeaderLicense.ALv2(LocalDate.now.getYear.toString, "Emergence contributors")),
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(publish / skip := true)
  .aggregate(core)

lazy val core = project
  .in(file("core"))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(commonSettings)
  .settings(
    name := "core",
    libraryDependencies ++= Dependencies.core
  )
  .settings(
    Compile / sourceGenerators += Def.task {
      val directory = organization.value.split('.').mkString("/")
      val pkg       = organization.value
      val file      = (Compile / sourceManaged).value / directory / "BuildInfo.scala"

      IO.write(
        file,
        s"""
        |package $pkg
        |
        |object BuildInfo {
        |  val Version: String = "${version.value}"
        |}""".stripMargin
      )
      Seq(file)
    }.taskValue
  )
  .settings(
    dockerBaseImage      := "eclipse-temurin:11",
    Docker / packageName := "fgrutsch/emergence",
    dockerUpdateLatest   := true
  )

lazy val docs = project
  .in(file("docs"))
  .settings(commonSettings)
  .settings(
    name                         := "docs",
    githubWorkflowArtifactUpload := false
  )
  .dependsOn(core)
  .enablePlugins(ParadoxSitePlugin)
