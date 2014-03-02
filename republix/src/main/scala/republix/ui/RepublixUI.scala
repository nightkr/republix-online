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