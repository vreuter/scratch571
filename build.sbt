name := "flybrain571"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.9"
organization := "vreuter"

assemblyJarName in assembly := s"${name.value}_v${version.value}.jar"
publishTo := Some(Resolver.file(s"${name.value}",  new File(Path.userHome.absolutePath + "/.m2/repository")))

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.1"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

/* Core abstractions */
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"
libraryDependencies += "org.typelevel" % "mouse_2.12" % "0.23"
libraryDependencies += "eu.timepit" %% "refined" % "0.9.10"

/*
libraryDependencies += "org.typelevel" %% "kittens" % "1.2.0"
*/

/* Breeze, mainly for linear algebra */
/*
libraryDependencies  ++= Seq(
  // Last stable release
  "org.scalanlp" %% "breeze" % "1.0",
  // Native libraries are not included by default. add this if you want them
  // Native libraries greatly improve performance, but increase jar sizes. 
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  "org.scalanlp" %% "breeze-natives" % "1.0",
  // The visualization library is distributed separately as well.
  // It depends on LGPL code
  "org.scalanlp" %% "breeze-viz" % "1.0"
)
*/

/* My own */
//libraryDependencies += "vreuter" %% "uewf" % "0.0.1-SNAPSHOT"
//libraryDependencies += "vreuter" %% "flour" % "0.0.1-SNAPSHOT"

/* Java and compiler options */
scalacOptions ++= Seq("-deprecation", "-feature", "-language:higherKinds", "-Ypartial-unification")
//javaOptions += "-Xmx4G"

/* Testing tools, or at least only used in tests */
/*
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
libraryDependencies += "org.scalamock" %% "scalamock" % "4.1.0" % "test"
*/

/* Include circe from Travis Brown. */
/*
val circeVersion = "0.12.2"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic", 
  "io.circe" %% "circe-parser", 
  "io.circe" %% "circe-generic-extras"
).map(_ % circeVersion)
addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)
*/

/* ScalaTest options */
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oS")    // o for stdout, S for "short" stack trace; "F" for full
parallelExecution in Test := false                                        // Run tests serially for more intelligible exec output.

// Autogenerate source code as package episteminfo, with object BuildInfo, to access version; useful for GFF provenance.
lazy val root  = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoKeys := Seq[BuildInfoKey](name, version), buildInfoPackage := "flybrain571info")

// Enable quitting a run without quitting sbt.
cancelable in Global := true

// Ignore certain file patterns for the build.
excludeFilter in unmanagedSources := HiddenFileFilter || "Interactive*.scala" || ( new FileFilter { def accept(f: File) = Set("reserved")(f.getParentFile.getName) } )
