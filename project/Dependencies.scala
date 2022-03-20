import sbt._

object Dependencies {

  private val circeVersion      = "0.14.1"
  private val fs2Version        = "3.2.5"
  private val sttpClientVersion = "3.5.1"

  val core: Seq[ModuleID] = Seq(
    "ch.qos.logback"                   % "logback-classic"                % "1.2.11",
    "co.fs2"                          %% "fs2-io"                         % fs2Version,
    "co.fs2"                          %% "fs2-core"                       % fs2Version,
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml"        % "2.13.2",
    "com.monovore"                    %% "decline-effect"                 % "2.2.0",
    "com.softwaremill.sttp.client3"   %% "core"                           % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "circe"                          % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "async-http-client-backend-cats" % sttpClientVersion,
    "com.typesafe"                     % "config"                         % "1.4.1",
    "io.circe"                        %% "circe-yaml"                     % "0.14.1",
    "org.scalatest"                   %% "scalatest"                      % "3.2.11" % Test,
    "org.typelevel"                   %% "cats-effect"                    % "3.3.8",
    "org.typelevel"                   %% "log4cats-slf4j"                 % "2.2.0"
  )

  val organizeimports: ModuleID = "com.github.liancheng" %% "organize-imports" % "0.6.0"

}
