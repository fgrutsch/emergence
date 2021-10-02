ThisBuild / scalafixDependencies += Dependencies.organizeimports
ThisBuild / dynverSeparator := "-" // Default uses '+' which is not valid for docker tags

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
  scalaVersion := "2.13.6",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "utf-8",
    "-explaintypes",
    "-feature",
    "-language:higherKinds",
    "-unchecked",
    "-Xcheckinit",
    "-Xfatal-warnings",
    "-Wdead-code",
    "-Wunused:imports"
  ),
  headerLicense     := Some(HeaderLicense.ALv2("2021", "Emergence contributors")),
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
  .enablePlugins(BuildInfoPlugin, JavaAppPackaging, DockerPlugin)
  .settings(commonSettings)
  .settings(
    name := "core",
    libraryDependencies ++= Dependencies.core,
    addCompilerPlugin(Dependencies.betterMonadicFor)
  )
  .settings(
    buildInfoPackage := organization.value,
    buildInfoKeys := Seq[BuildInfoKey](
      version,
      "appName" -> "eMERGEnce",
      "cliName" -> "emergence"
    )
  )
  .settings(
    dockerBaseImage      := "adoptopenjdk:11",
    Docker / packageName := "fgrutsch/emergence",
    dockerUpdateLatest   := true
  )

lazy val docs = project
  .in(file("docs"))
  .settings(commonSettings)
  .settings(
    name := "docs"
  )
  .dependsOn(core)
  .enablePlugins(ParadoxSitePlugin)
