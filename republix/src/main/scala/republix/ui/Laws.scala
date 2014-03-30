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

	def open(gameModel: GameModel, player: (In[PhaseUpdate], Out[Command]),
	         party: Party, parties: Vector[Party], state: GameState,
	         nav: UINav): JComponent = new JPanel {

		var proposals = Map[GameNode, Option[Intensity]]()

		def proposeChange(node: GameNode, update: Option[Intensity]): Unit = {
			player._2.send(ProposeAmendment(node, update))
		}

		object ProposalsButton extends JButton("Proposals") {
			addActionListener(on {
				nav.showDialog(Proposals)
			})
		}

		add(new JLabel("Laws"))
		add(ProposalsButton)

		for (node <- state.intensities) {
			add(new NodeVisualizer(node))
		}

		player._1.listen {
			case Proposing(p, gameNode, update) if p == party =>
				proposals += ((gameNode, update))
				Proposals.ProposalList.setListData(proposals.toArray) // todo: sane way of updating this
			case CancelProposing(p, gameNode) if p == party =>
				proposals -= gameNode
				Proposals.ProposalList.setListData(proposals.toArray) // todo: sane way of updating this
			case _ =>
		}

		object Proposals extends JPanel {
			
			object ProposalList extends JList[(GameNode, Option[Intensity])] {

			}

			add(new JLabel("Proposals"))
			add(ProposalList)

		}
		class NodeVisualizer(node: (GameNode, Intensity)) extends JPanel {

			object DetailsButton extends JButton("Details") {
				addActionListener(on {
					nav.showDialog(NodeSettings)
				})
		}
			object RepealButton extends JButton("Repeal") {
				addActionListener(on {
					proposeChange(node._1, None)
				})
			}
			object IntensitySlider extends JSlider(SwingConstants.HORIZONTAL, 1, 99, (node._2.intensity*100).toInt min 99 max 1) {
				def intensity = Intensity((getValue min 99 max 1) / 100.0)
			}
			object ProposeButton extends JButton("Propose") {
				addActionListener(on {
					proposeChange(node._1, Some(IntensitySlider.intensity))
				})
			}
			object NodeSettings extends JPanel {

				add(new JLabel(s"${node._1.name}"))
				add(new JLabel(s"Intensity: ${node._2.intensity}"))
				add(IntensitySlider)
				add(RepealButton)
				add(ProposeButton)

				IntensitySlider.setEnabled(node._1.isLaw)
				RepealButton.setEnabled(node._1.isLaw)
				ProposeButton.setEnabled(node._1.isLaw)

			}

			add(new JLabel(s"${node._1.name} (${node._2.intensity})"))
			add(DetailsButton)

		}
	}
	
}