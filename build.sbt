name := "MongoScalaFirst"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.mongodb" %% "casbah" % "2.8.2"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.3"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3"

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "1.0.1"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.6.4"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.12"

resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases"