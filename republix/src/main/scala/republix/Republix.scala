package republix

import ui._
import javax.swing._

object Republix {

	def main(args: Array[String]) {
		val frame = new JFrame("Republix")
		val ui = new RepublixUI()
		frame.add(ui)
		frame.setVisible(true)
	}

}