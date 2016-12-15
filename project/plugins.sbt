scalacOptions ++= Seq("-unchecked", "-feature", /*"-deprecation",*/
  "-Xlint" /*, "-Xfatal-warnings"*/)

resolvers ++= DefaultOptions.resolvers(snapshot = true)
resolvers += Resolver.typesafeRepo("releases")

libraryDependencies += "org.pantsbuild" % "jarjar" % "1.6.3"
