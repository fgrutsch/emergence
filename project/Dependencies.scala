import sbt._

object Dependencies {

  private val circeVersion      = "0.14.5"
  private val fs2Version        = "3.7.0"
  private val sttpClientVersion = "3.8.15"

  val core: Seq[ModuleID] = Seq(
    "ch.qos.logback"                   % "logback-classic"                % "1.4.8",
    "co.fs2"                          %% "fs2-io"                         % fs2Version,
    "co.fs2"                          %% "fs2-core"                       % fs2Version,
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml"        % "2.14.3",
    "com.monovore"                    %% "decline-effect"                 % "2.4.1",
    "com.softwaremill.sttp.client3"   %% "core"                           % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "circe"                          % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "async-http-client-backend-cats" % sttpClientVersion,
    "com.typesafe"                     % "config"                         % "1.4.2",
    "io.circe"                        %% "circe-core"                     % circeVersion,
    "io.circe"                        %% "circe-parser"                   % circeVersion,
    "io.circe"                        %% "circe-yaml"                     % "0.14.2",
    "org.scalatest"                   %% "scalatest"                      % "3.2.16" % Test,
    "org.typelevel"                   %% "cats-effect"                    % "3.5.1",
    "org.typelevel"                   %% "log4cats-slf4j"                 % "2.6.0"
  )

  val organizeimports: ModuleID = "com.github.liancheng" %% "organize-imports" % "0.6.0"

}
