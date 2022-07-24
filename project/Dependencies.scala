import sbt._

object Dependencies {

  private val circeVersion      = "0.14.1"
  private val fs2Version        = "3.2.10"
  private val sttpClientVersion = "3.7.0"

  val core: Seq[ModuleID] = Seq(
    "ch.qos.logback"                   % "logback-classic"                % "1.2.11",
    "co.fs2"                          %% "fs2-io"                         % fs2Version,
    "co.fs2"                          %% "fs2-core"                       % fs2Version,
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml"        % "2.13.3",
    "com.monovore"                    %% "decline-effect"                 % "2.3.0",
    "com.softwaremill.sttp.client3"   %% "core"                           % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "circe"                          % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "async-http-client-backend-cats" % sttpClientVersion,
    "com.typesafe"                     % "config"                         % "1.4.2",
    "io.circe"                        %% "circe-core"                     % circeVersion,
    "io.circe"                        %% "circe-parser"                   % circeVersion,
    "io.circe"                        %% "circe-yaml"                     % circeVersion,
    "org.scalatest"                   %% "scalatest"                      % "3.2.12" % Test,
    "org.typelevel"                   %% "cats-effect"                    % "3.3.14",
    "org.typelevel"                   %% "log4cats-slf4j"                 % "2.4.0"
  )

  val organizeimports: ModuleID = "com.github.liancheng" %% "organize-imports" % "0.6.0"

}
