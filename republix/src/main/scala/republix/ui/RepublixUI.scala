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

trait UINav {

	def showDialog(comp: JComponent): Unit

}
trait RepublixNav extends UINav {

	def menu(): Unit
	def quit(): Unit
	def switchTo(comp: JComponent): Unit

}
class RepublixUI(frame: JFrame) extends JPanel { outer =>

	import RepublixScreen._

	val screens = Seq(Host, Join, Exit)
	var currentScreen: Option[JComponent] = Some(TitleScreen)

	object TitleScreen extends JPanel {
		for (screen <- screens) {
			add(new OptionButton(screen))
		}
	}
	object Nav extends RepublixNav {
		def menu() = {
			switchTo(TitleScreen)
		}
		def quit() = {
			sys.exit(0) // todo
		}
		def switchTo(comp: JComponent) = {
			for (screen <- currentScreen) {
				outer.remove(screen)
				outer.add(comp)
				currentScreen = Some(comp)
			}
			outer.validate()
			outer.repaint()
		}
		def showDialog(comp: JComponent) = {
			val dialog = new JDialog(frame)
			dialog.add(comp)
			dialog.pack()
			dialog.setVisible(true)
		}
	}
	class OptionButton(opt: RepublixScreen) extends JButton {
		setText(opt.name)
		addActionListener(on {
			opt.choose(Nav)
		})
	}

	add(TitleScreen)

}

trait RepublixScreen {

	def name: String
	def icon: Unit = () // todo
	def choose(parent: RepublixNav): Unit

}

object RepublixScreen {
	object Host extends RepublixScreen {
		def name = "Host"
		def choose(parent: RepublixNav) = {
			parent.switchTo(new GameSetup(parent))
		}
	}
	object Join extends RepublixScreen {
		def name = "Join"
		def choose(parent: RepublixNav) = {
			parent.switchTo(new GameSelector(parent))
		}
	}
	object Exit extends RepublixScreen {
		def name = "Exit"
		def choose(parent: RepublixNav) = {
			parent.quit()
		}
	}
}