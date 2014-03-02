package republix

import java.awt._
import java.awt.event._
import javax.swing._

package object ui {

	def on(body: => Unit): ActionListener = new ActionListener() {
		def actionPerformed(ev: ActionEvent) = { body }
	}
	def debug(comp: JComponent): Unit = {
		comp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3))
	}

}