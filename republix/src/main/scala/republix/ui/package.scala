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
	def groupLayout(owner: JComponent): GroupLayout = {
		val layout = new GroupLayout(owner)
		layout.setAutoCreateGaps(true)
		layout.setAutoCreateContainerGaps(true)
		layout
	}

}