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

object Lobby extends UIPhase {

	def open(model: Model, player: (In[PhaseUpdate], Out[Command]),
	         party: Party, startParties: Vector[Party])
	        (state: Map[model.Node, Intensity]): JComponent = new JPanel {

		var parties = party +: startParties
		var listeners = Vector[ListDataListener]()
		val partyModel = new ListModel[Party] {
			def addListDataListener(l: ListDataListener): Unit = {
				listeners :+= l
			}
			def removeListDataListener(l: ListDataListener): Unit = {
				listeners = listeners.filterNot(_ == l)
			}
			def getElementAt(i: Int) = parties(i)
			def getSize = parties.size
		}

		val partyList = new JList[Party](partyModel)

		player._1.listen {
			case NewParty(party) =>
				parties :+= party
				for (l <- listeners) {
					l.intervalAdded(new ListDataEvent(partyModel, ListDataEvent.INTERVAL_ADDED, parties.size, parties.size))
				}
		}

		add(new JLabel("Lobby"))
		add(partyList)

	}
	// todo: place this somewhere sane
	def join(address: String, port: Int): In[(In[Update], Out[Command])] = {
		val conn = Net.connect(address, port)
		conn.map { case (in, out) =>
			(Net.read[Update](in), Net.write[Command](out))
		}
	}
}