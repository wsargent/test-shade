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

lazy val `example-netty` = project.in(file("example-netty"))
  .settings(disableDocs)
  .settings(disablePublishing)
  .settings(
    libraryDependencies ++= netty,
    name := "example-netty"
  )

def excludeNetty(module: ModuleID): ModuleID =
  module.excludeAll(ExclusionRule(organization = "io.netty"))

lazy val `example-netty-embedded` = Project("example-netty-embedded", file(".") / "target" / "example-netty-embedded-src-dummy")
  .settings(disablePublishing)
  .settings(
    name := "example-netty-embedded",
    // There is nothing to compile for this project. Instead we use the compile task to create
    // shaded versions of repl-jline and jline.jar. dist/mkBin puts all of quick/repl,
    // quick/repl-jline and quick/repl-jline-shaded on the classpath for quick/bin scripts.
    // This is different from the Ant build where all parts are combined into quick/repl, but
    // it is cleaner because it avoids circular dependencies.
    compile in Compile := (compile in Compile).dependsOn(Def.task {
      val log = streams.value.log

      import java.util.jar._
      import collection.JavaConverters._
      val inputs: Iterator[JarJar.Entry] = {
        val validClasses = (products in Compile in `example-netty`).value.flatMap(base => Path.allSubpaths(base).map(x => (base, x._1)))
        netty.flatMap { moduleId =>
          val nettyJar = findJar((dependencyClasspath in Compile).value, moduleId).get.data
          val jarFile = new JarFile(nettyJar)
          val jarEntries = jarFile.entries.asScala.filterNot(_.isDirectory).map(entry => JarJar.JarEntryInput(jarFile, entry))
          def compiledClasses = validClasses.iterator.map { case (base, file) => JarJar.FileInput(base, file) }
          (jarEntries ++ compiledClasses).filter(x =>
            x.name.endsWith(".class") || x.name.endsWith(".properties") || x.name.startsWith("META-INF/native") || x.name.startsWith("META-INF/maven")
          )
        }.iterator
      }
      import JarJar.JarJarConfig._
      val config: Seq[JarJar.JarJarConfig] = Seq(
        Rule("io.netty.**", "example.netty.@1")
      )
      val outdir = (classDirectory in Compile).value
      JarJar(inputs, outdir, config, verbose = false, log)
    }).value
  ).dependsOn(`example-netty`)

lazy val `example-netty-depends` = project.in(file("example-netty-depends"))
  .settings(
    mainClass in (Compile, run) := Some("example.Main")
  ).settings(
    allDependencies ~= (_.map(excludeNetty))
  ).dependsOn(`example-netty-embedded`)

lazy val root = (project in file("."))
  .aggregate(`example-netty-depends`)

/** Find a specific module's JAR in a classpath, comparing only organization and name */
def findJar(files: Seq[Attributed[File]], dep: ModuleID): Option[Attributed[File]] = {
  def extract(m: ModuleID) = (m.organization, m.name)
  files.find(_.get(moduleID.key).map(extract _) == Some(extract(dep)))
}
