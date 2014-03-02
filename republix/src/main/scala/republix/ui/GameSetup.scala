package republix.ui

import javax.swing._

class GameSetup(parent: RepublixNav) extends JPanel {

	object Port extends JTextField {

	}
	object Ok extends JButton("Ok") {
		
	}
	object Cancel extends JButton("Cancel") {
		addActionListener(on {
			parent.menu()
		})
	}

	layoutify(this)
	place(this, new JLabel("Port:"),       0, 0)
	place(this, Port,                      1, 0)
	place(this, Cancel,                    0, 1)
	place(this, Ok,                        1, 1)

}