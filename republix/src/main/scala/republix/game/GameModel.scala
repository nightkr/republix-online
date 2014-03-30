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

import republix.sim._
import republix.io._

case class GameModel(nodes: Set[GameNode], links: Map[(GameNode, GameNode), Link]) extends Model {
	type Node = GameNode
}
case class GameNode(name: String, isLaw: Boolean)
case class GameState(intensities: Map[GameNode, Intensity])

trait SimPhase {

	def sim(model: GameModel, players: => Vector[Party], updates: In[(Party, PhaseCommand)],
			state: GameState, feedback: SimEffect => Unit): Unit

}

sealed trait SimEffect
case class SwitchSimPhase(phase: GamePhase) extends SimEffect
case class Kick(party: Party) extends SimEffect
case class Broadcast(msg: PhaseUpdate) extends SimEffect
case object LockGame extends SimEffect // prevents people from joining