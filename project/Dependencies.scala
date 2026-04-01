import sbt._

object Dependencies {

  private val circeVersion      = "0.14.15"
  private val fs2Version        = "3.13.0"
  private val sttpClientVersion = "3.11.0"

  val core: Seq[ModuleID] = Seq(
    "ch.qos.logback"                   % "logback-classic"                % "1.5.32",
    "co.fs2"                          %% "fs2-io"                         % fs2Version,
    "co.fs2"                          %% "fs2-core"                       % fs2Version,
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml"        % "2.21.2",
    "com.monovore"                    %% "decline-effect"                 % "2.6.1",
    "com.softwaremill.sttp.client3"   %% "core"                           % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "circe"                          % sttpClientVersion,
    "com.softwaremill.sttp.client3"   %% "async-http-client-backend-cats" % sttpClientVersion,
    "com.typesafe"                     % "config"                         % "1.4.6",
    "io.circe"                        %% "circe-core"                     % circeVersion,
    "io.circe"                        %% "circe-parser"                   % circeVersion,
    "io.circe"                        %% "circe-yaml"                     % "1.15.0",
    "org.scalatest"                   %% "scalatest"                      % "3.2.20" % Test,
    "org.typelevel"                   %% "cats-effect"                    % "3.7.0",
    "org.typelevel"                   %% "log4cats-slf4j"                 % "2.8.0"
  )

}
