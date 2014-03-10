package republix.ui

import republix.game._
import republix.io._
import republix.sim._

class Client(player: (In[Update], Out[Command]), partyName: String, nav: RepublixNav) {

	player._2.send(Intro(partyName))

	var us: Option[Party] = None
	var parties: Vector[Party] = Vector()

	var phaseUpdates: PhaseUpdate => Unit = x => {}

	val phaseMap: Map[GamePhase, UIPhase] = Map(LobbyPhase() -> Lobby)

	object model extends Model {
		type Node = String
		val links: Map[(Node, Node), Link] = Map()
		val serialized = IntroModel("YAY!")
	}

	def start(): Unit = {
		player._1.listen {
			case IntroModel(model) =>
				println(s"Model introduced: $model")
			case NewParty(party) =>
				println(s"$party joined.")
				parties :+= party
				phaseUpdates(NewParty(party))
			case YouAre(party) =>
				println(s"I am: $party")
				us = Some(party)
			case SwitchPhase(newPhase) =>
				println(s"Switching phase to $newPhase")
				val (updates, produce) = makeIn[PhaseUpdate](() => {})
				phaseUpdates = produce
				val comp = phaseMap(newPhase).open(model, (updates, player._2), us.get, parties)(Map())
				nav.switchTo(comp)
			case Chat(str) =>
				println(s"Chat: $str")
			case x: PhaseUpdate =>
				phaseUpdates(x)
		}
	}

}