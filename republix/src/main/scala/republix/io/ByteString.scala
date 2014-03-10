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

case class ByteString(toVector: Vector[Byte]) { // efficiency schmeficiency; this game is turn-based

	import ByteString._

	def splitAt(n: Int): (ByteString, ByteString) = {
		val (l, r) = toVector.splitAt(n)
		(ByteString(l), ByteString(r))
	}
	override def toString = new String(toVector.toArray, UTF8)
	def extract: Option[(Byte, ByteString)] = for {
		head <- toVector.headOption
	} yield (head, ByteString(toVector.tail))

	def ++(that: ByteString) = ByteString(toVector ++ that.toVector)
	def length = toVector.length

}
object ByteString {

	val UTF8 = java.nio.charset.Charset.forName("UTF-8")

	def apply(str: String): ByteString = ByteString(str.getBytes(UTF8).toVector)
	def apply(i: Int): ByteString = ByteString(Vector(3, 2, 1, 0).map(x => ((i >> x*8) & 0xFF).toByte))
	def apply(l: Long): ByteString = ByteString(Vector(7, 6, 5, 4, 3, 2, 1, 0).map(x => ((l >> x*8) & 0xFF).toByte))
	def apply(bytes: Array[Byte]): ByteString = ByteString(bytes.toVector)
	def apply(b: Byte): ByteString = ByteString(Vector(b))
	def apply(): ByteString = ByteString(Vector())

}