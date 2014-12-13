enablePlugins(JavaAppPackaging, LinuxPlugin, UniversalPlugin)
name := "acleague"
version := "1.0"
scalaVersion := "2.11.4"
resolvers += "BaseX Maven Repository" at "http://files.basex.org/maven"
libraryDependencies ++= Seq(
  "org.syslog4j" % "syslog4j" % "0.9.30",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
  "org.basex" % "basex" % "7.9",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.6",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.6" % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
)
mainClass in Compile := Option("us.woop.EverythingIntegrated")
publishArtifact in (Compile, packageBin) := false
publishArtifact in (Universal, packageZipTarball) := true
publishArtifact in (Compile, packageDoc) := false

