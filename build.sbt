name := """toDo list"""
organization := "derickdamoah.com"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.11"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.10.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.8"
libraryDependencies += "org.mockito" %  "mockito-core"  % "5.4.0" % Test
libraryDependencies += "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % Test
libraryDependencies += "com.typesafe.play" %% "play" % "2.8.20"
libraryDependencies += "org.jsoup" % "jsoup" % "1.16.1"
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
enablePlugins(ScoverageSbtPlugin)
coverageEnabled := true


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "derickdamoah.com.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "derickdamoah.com.binders._"
