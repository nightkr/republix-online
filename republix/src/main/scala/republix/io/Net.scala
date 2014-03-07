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

	// todo: find better place for this
	def read[A](in: In[ByteString])(implicit serial: Serial[A]): In[A] = generate(in.close _) { produce =>
		var current = ByteString()
		while (true) {
			current = current ++ in.get()
			serial.deserialize(current) match {
				case Some(x, rest) =>
					current = rest
					produce(x)
				case None =>
					// todo: needs a way to check if deserialization encountered error or just needs more info
			}
		}
	}
	def write[A](out: Out[ByteString])(implicit serial: Serial[A]): Out[A] =
		out.comap(serial.serialize _)
	
}