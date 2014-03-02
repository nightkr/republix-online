package republix

import ui._
import java.awt.Frame.MAXIMIZED_BOTH
import javax.swing._
import JFrame.EXIT_ON_CLOSE

object Republix {

	def main(args: Array[String]) {
		val frame = new JFrame("Republix")
		val ui = new RepublixUI()
		frame.add(ui)
		frame.setExtendedState(MAXIMIZED_BOTH)
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE)
		frame.setVisible(true)
	}

}