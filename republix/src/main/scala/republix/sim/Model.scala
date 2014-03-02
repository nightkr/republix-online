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

trait Model {
	val nodes: Seq[Node]
}
object Model {

	def valueToIntensity(intensity: Double) = 1/(1+math.exp(-intensity))
	def intensityToValue(value: Double) = math.log(value/(1-value))

}

class Intensity(val intensity: Double) extends AnyVal {
	def value = Model.intensityToValue(intensity) // value = logit(intensity)
}
object Intensity {
	def apply(intensity: Double): Intensity = new Intensity(intensity)
	def unapply(intensity: Intensity): Option[Double] = Some(intensity.intensity)
}
object Value {
	def apply(value: Double): Intensity = new Intensity(Model.valueToIntensity(value))
	def unapply(intensity: Intensity): Option[Double] = Some(intensity.value)
}

sealed trait Effect {
	def apply(cause: Intensity): Intensity
}

case class LinearEffect(coefficient: Double) extends Effect {
	override def apply(cause: Intensity): Intensity = Value(cause.intensity * coefficient)
}

sealed trait Node {
	val model: Model
	val name: String
	val causes: Map[Node, Effect]

	lazy val effects: Map[Node, Effect] = (for {
		node <- model.nodes
		(otherNode, effect) <- node.causes
		if otherNode == this
	} yield (node, effect)).toMap

	private def intensity(previous: NodeState, causeIntensities: Map[Node, Intensity]): Intensity =
		Value(causeIntensities.map {
			case (node, intensity) => causes(node)(intensity).value
		}.sum)

	def active(previous: NodeState, intensity: Intensity): Boolean = previous.active

	def tick(previous: NodeState, causeStates: Map[Node, NodeState]): NodeState = {
		val intensity = this.intensity(previous, causeStates.filter(_._2.active).mapValues(_.intensity))
		val active = this.active(previous, intensity)
		NodeState(intensity, active)
	}
}

case class NodeState(intensity: Intensity, active: Boolean)

case class Status(model: Model, name: String, causes: Map[Node, Effect]) extends Node

case class Law(model: Model, name: String) extends Node {
	override val causes: Map[Node, Effect] = Map()
}

case class Event(model: Model, name: String, causes: Map[Node, Effect], startTrigger: Intensity, stopTrigger: Intensity) extends Node {
	override def active(previous: NodeState, intensity: Intensity): Boolean = {
		intensity match {
			case x if x.intensity > startTrigger.intensity => true
			case x if x.intensity < stopTrigger.intensity => false
			case _ => previous.active
		}
	}
}
