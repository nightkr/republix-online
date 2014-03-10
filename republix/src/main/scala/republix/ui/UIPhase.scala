package republix.ui

import republix.io._
import republix.sim._
import republix.game._
import javax.swing._

trait UIPhase {

	def open(model: Model, player: (In[PhaseUpdate], Out[Command]),
	         party: Party, parties: Vector[Party])
	        (state: Map[model.Node, Intensity]): JComponent

}