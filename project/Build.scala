import sbt._
import Keys._
import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.routes.RoutesKeys.routesGenerator
import play.twirl.sbt.Import.TwirlKeys

object ApplicationBuild extends Build {

  val appName    = "play2-auth"

  val playVersion = "2.7.3"

  lazy val baseSettings = Seq(
    version            := "0.17.1-UKHO",
    scalaVersion       := "2.12.6",
    organization       := "jp.t2v",
    scalacOptions      ++= Seq("-language:_", "-deprecation")
  )

  lazy val core = Project("core", base = file("module"))
    .settings(
      baseSettings,
      libraryDependencies += "com.typesafe.play"  %%   "play"                   % playVersion        % "provided",
      libraryDependencies += "com.typesafe.play"  %%   "play-cache"             % playVersion        % "provided",
      libraryDependencies += "jp.t2v"             %%   "stackable-controller"   % "0.7.2-UKHO",
      name                    := appName,
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
      resolvers ++= Seq(
        "Artifactory Snapshot Realm" at "https://artifactory.digital.homeoffice.gov.uk/artifactory/libs-snapshot-local/",
        "Artifactory Release Realm" at "https://artifactory.digital.homeoffice.gov.uk/artifactory/libs-release-local/",
        "Artifactory External Release Local Realm" at "https://artifactory.digital.homeoffice.gov.uk/artifactory/ext-release-local/"
      ),
      publishTo := {
        val artifactory = "https://artifactory.digital.homeoffice.gov.uk/"
        Some("release"  at artifactory + "artifactory/libs-release-local")
      }
    )

  lazy val test = Project("test", base = file("test"))
    .settings(
      baseSettings,
      libraryDependencies += "com.typesafe.play"  %% "play-test"   % playVersion,
      name                    := appName + "-test"
    ).dependsOn(core)

  lazy val root = Project("root", base = file("."))
    .settings(baseSettings)
    .aggregate(core, test)

}
