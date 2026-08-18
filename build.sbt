scalaVersion := "3.8.4"

lazy val root = rootProject
  .settings(
    name := "marmot",
    idePackagePrefix := Some("org.tk.marmot"),
    libraryDependencies ++= Seq(
      //You can add library dependencies here, for example,
      //"org.scalatest" %% "scalatest" % "3.2.19" % Test,
      //"org.scalameta" %% "munit" % "1.2.3" % Test

      "com.lihaoyi" %% "upickle" % "4.4.3",
      "org.typelevel" %% "spire" % "0.18.0",

      "org.openjdk.jmh" % "jmh-core" % "1.37",
      "org.openjdk.jmh" % "jmh-generator-annprocess" % "1.37",
    )
  )

enablePlugins(JmhPlugin)

lazy val benchmarks = project
  .in(file("benchmarks"))
  .dependsOn(root)
  .enablePlugins(JmhPlugin)

