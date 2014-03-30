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
import javax.swing._
import javax.swing.event._

class Vote(proposals: Map[(Party, GameNode), Option[Intensity]]) extends UIPhase {

	def open(model: GameModel, player: (In[PhaseUpdate], Out[Command]),
	         party: Party, parties: Vector[Party], state: GameState,
	         nav: UINav): JComponent = new JPanel {
		add(new JLabel("Vote"))

		for (p <- parties :+ party) {
			add(new Proposal(p, proposals.collect {case ((party, law), i) if party == p => (law, i)}))
		}

		class Proposal(party: Party, proposal: Map[GameNode, Option[Intensity]]) extends JPanel {

			object ProposalList extends JList[(GameNode, Option[Intensity])](proposal.toArray) {
			}
			object VoteButton extends JButton("Vote") {
				addActionListener(on {
					player._2.send(VoteFor(party))
				})
			}

			add(new JLabel(party.name))
			add(ProposalList)
			add(VoteButton)

		}
	}
}