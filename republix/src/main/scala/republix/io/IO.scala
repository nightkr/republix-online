package republix

import util.continuations._
import java.io._

package object io {

	trait In[+A] {

		// will build up backlog if not called
		def setReceive(f: A => Unit): Unit
		def close(): Unit

		def listen(f: A => Unit): Unit = setReceive { x =>
			f(x)
			listen(f)
		}
		def get(): A @cps[Unit] = shift(setReceive _)

	}

	trait Out[-A] {

		def backlog: Int

		//non-blocking
		def send(x: A): Unit
		def close(): Unit
	}

	def generate[A](toClose: () => Unit)(generator: (A => Unit) => Unit @cps[Unit]): In[A] = {
		val listeners = new java.util.concurrent.LinkedBlockingQueue[A => Unit]
		@volatile var open = true
		reset {
			generator { x =>
				while (open) {
					val listen = listeners.poll(1, java.util.concurrent.TimeUnit.SECONDS)
					if (listen ne null) {
						listen(x)
					}
				}
			}
		}
		new In[A] {
			def setReceive(f: A => Unit): Unit = { listeners.add(f) }
			def close(): Unit = { open = false; toClose() }
		}
	}
	def generateIO[A](toClose: () => Unit)(generator: (A => Unit) => Unit @cps[Unit]): In[A] = {
		generate(() =>
			try { toClose() }
				catch {
					case ex: IOException =>
						ex.printStackTrace
				}) { produce =>
			new Thread {
				override def run() = {
					try {
						generator(produce)
					}
					catch {
						case ex: IOException =>
							ex.printStackTrace
					}
				}
			}.start()
		}
	}
	def fromInputStream(is: InputStream): In[ByteString] = generateIO(is.close _) { produce =>
		while (true) {
			val bytes = new Array[Byte](1024)
			val len = is.read(bytes)
			val res = ByteString(bytes.take(len))
			if (res.length > 0) {
				produce(res)
			}
		}
	}
	def fromOutputStream(os: OutputStream): Out[ByteString] = {
		val queue = new java.util.concurrent.LinkedBlockingQueue[ByteString]
		@volatile var open = true
		new Thread {
			override def run() = {
				try {
					while (open) {
						val elem = queue.poll(1, java.util.concurrent.TimeUnit.SECONDS)
						if (elem ne null) {
							os.write(elem.toVector.toArray)
						}
					}
				}
				catch {
					case ex: IOException =>
						ex.printStackTrace
				}
			}
		}.start
		new Out[ByteString] {
			def backlog = queue.size
			def send(x: ByteString) = { queue.add(x) }
			def close() = {
				open = false
				try {
					os.close()
				}
				catch {
					case ex: IOException =>
						ex.printStackTrace
				}
			}
		}
	}

}