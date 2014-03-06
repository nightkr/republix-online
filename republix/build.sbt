libraryDependencies ++= Seq(
	"akka-actor",
	"akka-remote"
).map("com.typesafe.akka" %% _ % "2.2.3")

libraryDependencies += "com.chuusai" %% "shapeless" % "2.0.0-M1" cross CrossVersion.full changing()