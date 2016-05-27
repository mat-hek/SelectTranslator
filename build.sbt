import AssemblyKeys._

name := "selectTranslator"

version := "0.2"

scalaVersion := "2.11.8"

unmanagedBase <<= baseDirectory { base => base / "lib" }

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
libraryDependencies += "org.spire-math" %% "jawn-ast" % "0.8.4"
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.6.4"

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "1.0.1"

libraryDependencies += "com.github.tulskiy" % "jkeymaster" % "1.2"

libraryDependencies += "com.typesafe" % "config" % "1.3.0"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.4"

mainClass in Compile := Some("SelectTranslator.SelectTranslator")
mainClass in assembly := Some("SelectTranslator.SelectTranslator")
/*
mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => old match {
  case x if x endsWith ".conf" => MergeStrategy.concat
  case x => old(x)
   }
}
*/