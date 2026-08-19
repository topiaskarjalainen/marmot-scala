ThisBuild / organization := "org.tk.marmot"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.8.4"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-encoding", "utf8",
  ),
  Test / fork := true,
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "org.openjdk.jmh" % "jmh-core" % "1.37",
    "org.openjdk.jmh" % "jmh-generator-annprocess" % "1.37",

    "com.lihaoyi" %% "upickle" % "4.4.3",
    "org.typelevel" %% "spire" % "0.18.0",

  )
)

enablePlugins(JmhPlugin)

lazy val coreModule = project
  .in(file("modules/core"))
  .settings(commonSettings)
  .settings(
    name := "marmot-core",
  )


lazy val mathModule = project
  .in(file("modules/math"))
  .settings(commonSettings)
  .settings(
    name := "marmot-math",
  )


lazy val benchmarks = project
  .in(file("benchmarks"))
  .dependsOn(root)
  .enablePlugins(JmhPlugin)

lazy val root = rootProject
  .aggregate(coreModule, mathModule)
  .dependsOn(coreModule, mathModule)
  .settings(
    name := "marmot",
    // idePackagePrefix := Some("org.tk.marmot"),
    publish / skip := true,
  )

