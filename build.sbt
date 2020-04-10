name := "RestTest"

version := "0.1"

scalaVersion := "2.13.1"


libraryDependencies ++= {
  Seq(
    "postgresql"          % "postgresql"  % "9.1-901.jdbc4",
    "org.slf4j" % "slf4j-api" % "1.7.25",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.typesafe.akka" %% "akka-http" % "10.1.11",
    "com.typesafe.akka" %% "akka-actor" % "2.6.4",
    "com.typesafe.akka" %% "akka-stream" % "2.6.4",
    "com.typesafe.akka" %% "akka-actor-typed" % "2.6.4",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",
    "com.typesafe.slick"  %%  "slick"     % "3.3.2",

  )
}

/*resolvers ++= Seq(
  "Spray repository" at "https://repo.spray.io",
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)
*/