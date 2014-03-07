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

import java.io._

package object io {

	trait In[+A] { self =>

		// will build up backlog if not called
		def setReceive(f: A => Unit): Unit
		def close(): Unit

		def listen(f: A => Unit): Unit = setReceive { x =>
			f(x)
			listen(f)
		}

		def map[B](f: A => B): In[B] = new In[B] {
			def setReceive(g: B => Unit) = self.setReceive(f andThen g)
			def close() = self.close()
		}

	}

	trait Out[-A] { self =>

		def backlog: Int

		//non-blocking
		def send(x: A): Unit
		def close(): Unit

		def comap[B](f: B => A): Out[B] = new Out[B] {
			def backlog = self.backlog
			def send(x: B) = self.send(f(x))
			def close() = self.close()
		}

	}

	def generate[A](toClose: () => Unit)(generator: (A => Unit) => Unit): In[A] = {
		val listeners = new java.util.concurrent.LinkedBlockingQueue[A => Unit]
		@volatile var open = true
		generator { x =>
			var done = false
			while (!done && open) {
				val listen = listeners.poll(1, java.util.concurrent.TimeUnit.SECONDS)
				if (listen ne null) {
					listen(x)
					done = true
				}
			}
		}
		new In[A] {
			def setReceive(f: A => Unit): Unit = { listeners.add(f) }
			def close(): Unit = { open = false; toClose() }
		}
	}
	def generateIO[A](toClose: () => Unit)(generator: (A => Unit) => Unit): In[A] = {
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