import Dependencies._

val disableDocs = Seq[Setting[_]](
  sources in (Compile, doc) := Seq.empty,
  publishArtifact in (Compile, packageDoc) := false
)

val disablePublishing = Seq[Setting[_]](
  publishArtifact := false,
  // The above is enough for Maven repos but it doesn't prevent publishing of ivy.xml files
  publish := {},
  publishLocal := {}
)

lazy val root = (project in file(".")).aggregate(`play-ws-netty-depends`)

lazy val `play-ws-netty` = project.in(file("play-ws-netty"))
  .settings(disableDocs)
  .settings(disablePublishing)
  .settings(
    scalaVersion := "2.12.1",
    libraryDependencies += netty,
    name := "play-ws-netty"
  )

lazy val `play-ws-netty-embedded` = project.in(file("play-ws-netty-embedded"))
  .settings(disablePublishing)
  .settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.1"
    )),
    name := "play-ws-netty-embedded",
    compile in Compile := (compile in Compile).dependsOn(Def.task {
      import java.util.jar._
      import collection.JavaConverters._
      val inputs: Iterator[JarJar.Entry] = {
        val validClasses = (products in Compile in `play-ws-netty`).value.flatMap(base => Path.allSubpaths(base).map(x => (base, x._1)))
        val nettyJar = findJar((dependencyClasspath in Compile).value, netty).get.data
        val jarFile = new JarFile(nettyJar)
        val jarEntries = jarFile.entries.asScala.filterNot(_.isDirectory).map(entry => JarJar.JarEntryInput(jarFile, entry))
        def compiledClasses = validClasses.iterator.map { case (base, file) => JarJar.FileInput(base, file) }
        (jarEntries ++ compiledClasses).filter(x =>
          x.name.endsWith(".class") || x.name.endsWith(".properties") || x.name.startsWith("META-INF/native") || x.name.startsWith("META-INF/maven")
        )
      }
      import JarJar.JarJarConfig._
      val config: Seq[JarJar.JarJarConfig] = Seq(
        Rule("io.netty.**", "play.api.libs.ws.ahc.netty.@1")
      )
      val outdir = (classDirectory in Compile).value
      JarJar(inputs, outdir, config)
    }).value
  ).dependsOn(`play-ws-netty`)

lazy val `play-ws-netty-depends` = project.in(file("play-ws-netty-depends")).dependsOn(`play-ws-netty-embedded`)

/** Find a specific module's JAR in a classpath, comparing only organization and name */
def findJar(files: Seq[Attributed[File]], dep: ModuleID): Option[Attributed[File]] = {
  def extract(m: ModuleID) = (m.organization, m.name)
  files.find(_.get(moduleID.key).map(extract _) == Some(extract(dep)))
}
