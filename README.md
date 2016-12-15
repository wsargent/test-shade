# test-shade

This is a very simple SBT project that shows how to shade Netty so that it uses a different root package.

This example uses the pantsbuild version of jarjar, which is updated for JDK 1.8.

Much of this work is taken from the Scala build implementation of Jarjar:

* https://github.com/scala/scala/pull/4874
* https://github.com/scala/scala/blob/2.12.x/project/JarJar.scala

However, in the Scala build, jline is provided embedded as a fallback mechanism, and so the original classes are still available.  Here, we only want the shaded classes to be used.
