import sbt._

object Dependencies {

  private val circeVersion      = "0.14.1"
  private val fs2Version        = "3.1.5"
  private val sttpClientVersion = "3.3.15"

  val core: Seq[ModuleID] = Seq(
    "ch.qos.logback"                 % "logback-classic"                % "1.2.6",
    "com.github.alexarchambault"    %% "case-app-cats"                  % "2.1.0-M8",
    "com.softwaremill.sttp.client3" %% "core"                           % sttpClientVersion,
    "com.softwaremill.sttp.client3" %% "circe"                          % sttpClientVersion,
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % sttpClientVersion,
    "org.typelevel"                 %% "cats-effect"                    % "3.2.9",
    "co.fs2"                        %% "fs2-core"                       % fs2Version,
    "co.fs2"                        %% "fs2-io"                         % fs2Version,
    "io.circe"                      %% "circe-config"                   % "0.8.0",
    "io.circe"                      %% "circe-yaml"                     % "0.14.1",
    "io.circe"                      %% "circe-literal"                  % circeVersion,
    "io.circe"                      %% "circe-generic-extras"           % circeVersion,
    "org.typelevel"                 %% "log4cats-slf4j"                 % "2.1.1",
    "org.scalatest"                 %% "scalatest"                      % "3.2.10" % Test
  )

  val betterMonadicFor: ModuleID = "com.olegpy"           %% "better-monadic-for" % "0.3.1"
  val organizeimports: ModuleID  = "com.github.liancheng" %% "organize-imports"   % "0.5.0"

}
