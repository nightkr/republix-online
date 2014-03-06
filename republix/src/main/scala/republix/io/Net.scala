package republix.io

import java.net._

object Net {

	def host(port: Int): In[(In[ByteString], Out[ByteString])] = {
		val server = new ServerSocket(port)
		generateIO[(In[ByteString], Out[ByteString])](() => {server.close}) { produce =>
			while (true) {
				val socket = server.accept
				produce((fromInputStream(socket.getInputStream), fromOutputStream(socket.getOutputStream)))
			}
		}
	}
	def connect(address: String, port: Int): In[(In[ByteString], Out[ByteString])] = {
		generateIO(() => { /* todo; minor problem */ }) { produce =>
			val socket = new Socket(address, port)
			produce((fromInputStream(socket.getInputStream), fromOutputStream(socket.getOutputStream)))
		}
	}
	
}