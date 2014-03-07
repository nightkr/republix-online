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
import javax.swing._

class Lobby(player: (In[Update], Out[Command]), party: String) extends JPanel {

	add(new JLabel("Lobby"))

	player._2.send(Intro(party))

}
object Lobby {

	def join(address: String, port: Int, party: String): In[Lobby] = {
		val conn = Net.connect(address, port)
		val player = conn.map { case (in, out) =>
			(Net.read[Update](in), Net.write[Command](out))
		}
		player.map(p => new Lobby(p, party))
	}

}