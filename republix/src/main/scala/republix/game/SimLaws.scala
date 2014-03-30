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

object SimLaws extends SimPhase {

	def sim(model: GameModel, players: => Vector[Party], updates: In[(Party, PhaseCommand)],
			state: GameState, feedback: SimEffect => Unit): Unit = {
		var proposals = Map[(Party, GameNode), Option[Intensity]]()

		updates.listen {
			case (p, ProposeAmendment(law, intensity)) =>
				if (model.nodes.contains(law) && law.isLaw) {
					proposals += (p, law) -> intensity
					feedback(Broadcast(Proposing(p, law, intensity)))
				}
				else {
					feedback(Kick(p))
				}
			case (p, CancelChanges(law)) =>
				if (proposals.contains((p, law))) {
					proposals -= ((p, law))
					feedback(Broadcast(CancelProposing(p, law)))
				}
				else {
					feedback(Kick(p))
				}
			case (p, _) =>
				feedback(Kick(p))
		}
	}

}