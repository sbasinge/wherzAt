import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play2Test"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
      "org.scalatest" %% "scalatest" % "1.9.1" % "test",
      "org.specs2" %% "specs2" % "1.12" % "test",
      "commons-codec" % "commons-codec" % "1.4",
      "org.squeryl" %% "squeryl" % "0.9.5-6",
    jdbc
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
