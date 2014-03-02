import sbt._
import Keys._

object RepublixBuild extends Build {
	override def settings: Seq[Def.Setting[_]] = super.settings ++ Seq(
		scalaVersion := "2.10.3"
	)

	lazy val republix = Project("republix", file("republix"))
	lazy val root = Project("root", file(".")).aggregate(republix)
}
