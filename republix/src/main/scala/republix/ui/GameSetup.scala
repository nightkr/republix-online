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

import javax.swing._

class GameSetup(parent: RepublixNav) extends JPanel {

	object Port extends JTextField {

	}

	object Ok extends JButton("Ok") {

	}

	object Cancel extends JButton("Cancel") {
		addActionListener(on {
			parent.menu()
		})
	}

	layoutify(this)
	place(this, new JLabel("Port:"), 0, 0)
	place(this, Port, 1, 0)
	place(this, Cancel, 0, 1)
	place(this, Ok, 1, 1)

}