/*
 * Copyright © 2014 Teo Klestrup, Carl Dybdahl
 *
 * This file is part of Republix.
 *
 * Republix is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Republix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Republix.  If not, see <http://www.gnu.org/licenses/>.
 */

import sbt._
import Keys._

object RepublixBuild extends Build {
	override def settings: Seq[Def.Setting[_]] = super.settings ++ Seq(
		scalaVersion := "2.10.3"
	)

	lazy val republix = Project("republix", file("republix"))
	lazy val root = Project("root", file(".")).settings(
		mainClass in Compile <<= mainClass in(republix, Compile)
	).dependsOn(republix).aggregate(republix)

	scalacOptions += "-deprecation"
}
