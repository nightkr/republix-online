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
		in.listen { elem =>
			current = current ++ elem
			var done = false
			while (!done) {
				serial.deserialize(current) match {
					case Some((x, rest)) =>
						current = rest
						produce(x)
					case None =>
						done = true
				}
			}
		}
	}
	def write[A](out: Out[ByteString])(implicit serial: Serial[A]): Out[A] =
		out.comap(serial.serialize _)
	
}