package republix.ui

import javax.swing._

class GameSelector(parent: RepublixNav) extends JPanel {

	object IP extends JTextField {
		setMinimumSize(getPreferredSize)
	}
	object Port extends JTextField {
		setMinimumSize(getPreferredSize)
	}
	object Ok extends JButton("Ok") {
		
	}
	object Cancel extends JButton("Cancel") {
		addActionListener(on {
			parent.menu()
		})
	}

	layoutify(this)
	place(this, new JLabel("Ip Address:"), 0, 0)
	place(this, IP,                        1, 0)
	place(this, new JLabel("Port:"),       0, 1)
	place(this, Port,                      1, 1)
	place(this, Cancel,                    0, 2)
	place(this, Ok,                        1, 2)
}