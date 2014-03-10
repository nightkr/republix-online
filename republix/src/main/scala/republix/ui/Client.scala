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

package republix.ui

import republix.game._
import republix.io._
import republix.sim._

class Client(player: (In[Update], Out[Command]), partyName: String, nav: RepublixNav) {

	player._2.send(Intro(partyName))

	var us: Party = _ // on one hand, ew, on the other, don't want to bother with options
	var model: GameModel = _
	var parties: Vector[Party] = Vector()

	var phaseUpdates: PhaseUpdate => Unit = x => {}

	val phaseMap: Map[GamePhase, UIPhase] = Map(LobbyPhase() -> Lobby)

	def start(): Unit = {
		player._1.listen {
			case IntroModel(gameModel) =>
				println(s"Model introduced: $gameModel")
				model = gameModel
			case NewParty(party) =>
				println(s"$party joined.")
				parties :+= party
				phaseUpdates(NewParty(party))
			case YouAre(party) =>
				println(s"I am: $party")
				us = party
			case SwitchPhase(newPhase, state) =>
				println(s"Switching phase to $newPhase")
				val (updates, produce) = makeIn[PhaseUpdate](() => {})
				phaseUpdates = produce
				val comp = phaseMap(newPhase).open(model, (updates, player._2), us, parties, state)
				nav.switchTo(comp)
			case Chat(str) =>
				println(s"Chat: $str")
			case x: PhaseUpdate =>
				phaseUpdates(x)
		}
	}

}