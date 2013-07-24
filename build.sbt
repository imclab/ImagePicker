scalaVersion := "2.10.2"

resolvers ++= Seq(
  "spray" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "commons-lang" % "commons-lang" % "2.6",
  "io.spray" % "spray-json_2.10" % "1.2.5",
  "org.jsoup" % "jsoup" % "1.7.2",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
)

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)
