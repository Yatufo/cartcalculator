name := "cart"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies ++=
  Seq("org.scalactic" %% "scalactic" % "3.0.0",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test",
    "org.mockito" % "mockito-all" % "1.9.5" % "test",
    "com.typesafe.akka" %% "akka-actor" % "2.4.11",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.11" % "test")