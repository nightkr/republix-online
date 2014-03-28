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

package republix.game

import republix.io._
import republix.sim._

class Game(clients: In[(In[Command], Out[Update])]) {

	val model = GameModel(Map())

	// todo: somehow freeze once games starts
	var players: Map[Party, Out[Update]] = Map()
	var phaseCommands: (Party, PhaseCommand) => Unit = (x, y) => {}
	var phase: GamePhase = _
	switchPhase(LobbyPhase())
	var state = GameState(Map())

	val phaseMap: Map[GamePhase, SimPhase] = Map(LobbyPhase() -> SimLobby)

	import java.util.concurrent.{Executors, ExecutorService}
	val pool: ExecutorService = Executors.newFixedThreadPool(1)
	def atomically(body: => Unit): Unit = {
		pool.execute(new Runnable {
			def run() = body
		})
	}

	clients.listen { newPlayer =>
		println("Player attempting to connect.")
		newPlayer._1.setReceive {
			case Intro(partyName) =>
				println(s"Intro received. $partyName connecting.")
				atomically {
					val party = Party(partyName)
					players.foreach { other =>
						other._2.send(NewParty(party))
						newPlayer._2.send(NewParty(other._1))
					}
					players += party -> newPlayer._2
					newPlayer._2.send(IntroModel(model))
					newPlayer._2.send(YouAre(party))
					newPlayer._2.send(SwitchPhase(LobbyPhase(), state))
					newPlayer._1.listen(playerListener(party))
				}
			case _ =>
				println("Player is breaking protocol. Kicking.")
				newPlayer._2.close
		}
	}

	def switchPhase(newPhase: GamePhase): Unit = {
		phase = newPhase
		val (updates, produce) = makeIn[(Party, PhaseCommand)](() => {})
		phaseCommands = (party, command) => produce((party, command))
		players.foreach { player =>
			player._2.send(SwitchPhase(phase, state))
		}
		phaseMap(phase).sim(model, players.keys.toVector, updates, state, feedback _)
	}
	def feedback(effect: SimEffect) = effect match {
		case SwitchSimPhase(newPhase) =>
			switchPhase(newPhase)
		case Kick(party) =>
			players(party).close
			players -= party
		case LockGame =>
			clients.close
	}

	def playerListener(party: Party): Command => Unit = (message: Command) => message match {
		case SendChat(chat: String) =>
			players.foreach { player =>
				player._2.send(Chat(party.name + ": " + chat))
			}
		case x: PhaseCommand =>
			phaseCommands(party, x)
		case _ =>
			players(party).close
	}

}