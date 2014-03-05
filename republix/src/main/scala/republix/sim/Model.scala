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
	type Node
	val links: Map[(Node, Node), Link]

	def causes(node: Node): Map[Node, Link] =
		links.collect { case ((cause, effect), link) if node == effect => (cause, link) }
	def effects(node: Node): Map[Node, Link] =
		links.collect { case ((cause, effect), link) if node == cause => (effect, link) }

	def compute(node: Node, state: Map[Node, NodeState]): NodeState =
		NodeState(Value(causes(node).map { case (cause, link) => link(state(cause).intensity).value}.sum))
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

sealed trait Link {
	def apply(cause: Intensity): Intensity
}

case class LogisticLink(coefficient: Double, offset: Double) extends Link {
	override def apply(cause: Intensity): Intensity = Value(cause.intensity * coefficient + offset)
}
case class LinearLink(coefficient: Double, offset: Double) extends Link {
	override def apply(cause: Intensity): Intensity = Value(cause.value * coefficient + offset)
}
case class DiscontinuousLink(trigger: Intensity, neutral: Intensity, active: Intensity) extends Link {
	override def apply(cause: Intensity): Intensity =
		if (trigger.value > cause.value) neutral
		else active
}

case class NodeState(intensity: Intensity)