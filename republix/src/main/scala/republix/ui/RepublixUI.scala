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

trait RepublixNav {

	def closeOption(): Unit

}
class RepublixUI extends JPanel { outer =>

	val screens = Seq(Exit)
	var currentScreen: Option[JComponent] = None

	object TitleScreen extends JPanel {
		for (screen <- screens) {
			add(new OptionButton(screen))
		}
	}
	object Nav extends RepublixNav {
		def closeOption() = {
			for (screen <- currentScreen) {
				outer.remove(screen)
				outer.add(TitleScreen)
				currentScreen = None
			}
		}
	}
	class OptionButton(opt: RepublixScreen) extends JButton {
		setText(opt.name)
		addActionListener(on {
			outer.remove(TitleScreen)
			val comp = opt.choose(Nav)
			outer.add(comp)
			outer.currentScreen = Some(comp)
		})

	}

	add(TitleScreen)

}

trait RepublixScreen {

	def name: String
	def icon: Unit = () // todo
	def choose(parent: RepublixNav): JComponent

}

object Exit extends RepublixScreen {

	def name = "Exit"
	def choose(parent: RepublixNav) = {
		sys.exit(0) // todo
	}

}