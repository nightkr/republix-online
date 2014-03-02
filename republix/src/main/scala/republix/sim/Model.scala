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

package republix.sim

import Node.Intensity

trait Model {
	val nodes: Seq[Node]
}

sealed trait Effect {
	def apply(cause: Intensity): Intensity
}

case class LinearEffect(coefficient: Int) extends Effect {
	override def apply(cause: Intensity): Intensity = cause * coefficient
}

sealed trait Node {
	val model: Model
	val name: String
	val causes: Map[Node, Effect]

	val baseState: NodeState = NodeState(0, active = true)

	lazy val effects: Map[Node, Effect] = (for {
		node <- model.nodes
		(otherNode, effect) <- node.causes
		if otherNode == this
	} yield (node, effect)).toMap

	def intensity(previous: NodeState, causeIntensities: Map[Node, Intensity]): Intensity = {
		causeIntensities.map {
			case (node, intensity) => causes(node)(intensity)
		}.sum
	}

	def active(previous: NodeState, intensity: Intensity): Boolean = true

	def tick(previous: NodeState, causeStates: Map[Node, NodeState]): NodeState = {
		val intensity = this.intensity(previous, causeStates.filter(_._2.active).mapValues(_.intensity))
		val active = this.active(previous, intensity)
		NodeState(intensity, active)
	}
}

case class NodeState(intensity: Intensity, active: Boolean)

object Node {
	type Intensity = Double
}

case class Status(model: Model, name: String, causes: Map[Node, Effect]) extends Node

case class Law(model: Model, name: String) extends Node {
	override val causes: Map[Node, Effect] = Map()
}

case class Event(model: Model, name: String, causes: Map[Node, Effect], startTrigger: Intensity, stopTrigger: Intensity) extends Node {
	override val baseState: NodeState = NodeState(0, active = false)

	override def active(previous: NodeState, intensity: Intensity): Boolean = {
		intensity match {
			case x if x > startTrigger => true
			case x if x < stopTrigger => false
			case _ => previous.active
		}
	}
}
