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

	def layoutify(comp: JComponent): Unit = {
		comp.setLayout(new GridBagLayout())
	}
	def place(parent: JComponent, child: JComponent, gx: Int, gy: Int, gw: Int = 1, gh: Int = 1): Unit = {
		val gbc = new GridBagConstraints()
		gbc.gridx = gx
		gbc.gridy = gy
		gbc.gridwidth = gw
		gbc.gridheight = gh
		gbc.weightx = 1.0
		parent.add(child, gbc)
	}

}