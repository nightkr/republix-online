package republix.sim

import Node.Intensity

trait Model {
	val nodes: Seq[Node]
}

sealed trait Effect

sealed trait Node {
	val model: Model
	val name: String
	val causes: Map[Node, Effect]

	lazy val effects: Map[Node, Effect] = (for {
		node <- model.nodes
		(otherNode, effect) <- node.causes
		if otherNode == this
	} yield (node, effect)).toMap
}

object Node {
	type Intensity = Int
}

case class Status(model: Model, name: String, causes: Map[Node, Effect]) extends Node

case class Law(model: Model, name: String) extends Node {
	override val causes: Map[Node, Effect] = Map()
}

case class Event(model: Model, name: String, causes: Map[Node, Effect], startTrigger: Intensity, stopTrigger: Intensity) extends Node
