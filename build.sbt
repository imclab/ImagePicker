scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "commons-lang" % "commons-lang" % "2.6",
  "org.jsoup" % "jsoup" % "1.7.2",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
)

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)
