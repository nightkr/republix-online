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
	         party: Party, parties: Vector[Party], state: GameState,
	         nav: UINav): JComponent = new JPanel {

		def proposeChange(node: GameNode)(update: Option[Intensity]): Unit = {
			player._2.send(ProposeAmendment(node, update))
		}

		add(new JLabel("Laws"))

		for (node <- state.intensities) {
			add(new NodeVisualizer(node, proposeChange(node._1) _, model, state, nav))
		}

	}
	class NodeVisualizer(node: (GameNode, Intensity), proposeChange: Option[Intensity] => Unit, gameModel: GameModel, context: GameState, nav: UINav) extends JPanel {

		object DetailsButton extends JButton("Details") {
			addActionListener(on {
				nav.showDialog(new NodeSettings(node, proposeChange, gameModel, context))
			})
		}

		add(new JLabel(s"${node._1.name} (${node._2.intensity})"))
		add(DetailsButton)

	}
	class NodeSettings(node: (GameNode, Intensity), proposeChange: Option[Intensity] => Unit, gameModel: GameModel, context: GameState) extends JPanel {

		object RepealButton extends JButton("Repeal") {
			addActionListener(on {
				proposeChange(None)
			})
		}
		object IntensitySlider extends JSlider(SwingConstants.HORIZONTAL, 1, 99, (node._2.intensity*100).toInt min 99 max 1) {

		}
		object ProposeButton extends JButton("Propose") {
			addActionListener(on {
				proposeChange(Some(Intensity((IntensitySlider.getValue min 99 max 1) / 100.0)))
			})
		}

		add(new JLabel(s"${node._1.name}"))
		add(new JLabel(s"Intensity: ${node._2.intensity}"))
		add(IntensitySlider)
		add(RepealButton)
		add(ProposeButton)

		IntensitySlider.setEnabled(node._1.isLaw)
		RepealButton.setEnabled(node._1.isLaw)
		ProposeButton.setEnabled(node._1.isLaw)

	}
	
}