package republix.ui

import javax.swing._

trait RepublixNav {

	def menu(): Unit
	def quit(): Unit
	def switchTo(comp: JComponent): Unit

}
class RepublixUI extends JPanel { outer =>

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
			parent.switchTo(new Lobby())
		}
	}
	object Join extends RepublixScreen {
		def name = "Join"
		def choose(parent: RepublixNav) = {
			parent.switchTo(new Lobby())
		}
	}
	object Exit extends RepublixScreen {
		def name = "Exit"
		def choose(parent: RepublixNav) = {
			parent.quit()
		}
	}
}