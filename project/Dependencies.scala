import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.0"

  val nettyVersion = "4.0.42.Final"
  val netty = Seq(
    "io.netty" % "netty-codec-http" % nettyVersion,
    "io.netty" % "netty-codec" % nettyVersion,
    "io.netty" % "netty-handler" % nettyVersion,
    "io.netty" % "netty-transport" % nettyVersion,
    "io.netty" % "netty-buffer" % nettyVersion,
    "io.netty" % "netty-common" % nettyVersion
  )

}
