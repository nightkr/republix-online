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

object Laws extends UIPhase {

	def open(model: GameModel, player: (In[PhaseUpdate], Out[Command]),
	         party: Party, parties: Vector[Party], state: GameState): JComponent = new JPanel {

		def proposeChange(node: GameNode)(update: Option[Intensity]): Unit = {
			player._2.send(ProposeAmendment(node, update))
		}

		add(new JLabel("Laws"))

		for (node <- state.intensities) {
			add(new NodeVisualizer(node, proposeChange(node._1) _, state))
		}

	}
	class NodeVisualizer(node: (GameNode, Intensity), proposeChange: Option[Intensity] => Unit, context: GameState) extends JPanel {

		object RepealButton extends JButton("Repeal") {
			addActionListener(on {
				proposeChange(None)
			})
		}

		add(new JLabel(s"${node._1.name} (${node._2.intensity})"))
		add(RepealButton)

	}
	
}