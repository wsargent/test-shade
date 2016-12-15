import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.0"

  val nettyVersion = "4.0.42.Final"
  val netty = "io.netty" % "netty-codec-http" % nettyVersion

}
