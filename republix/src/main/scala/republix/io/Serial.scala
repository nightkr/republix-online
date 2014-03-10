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

import shapeless._

trait Serial[T] {

	def serialize(x: T): ByteString
	def deserialize(bs: ByteString): Option[(T, ByteString)]

}
case object Serial extends TypeClassCompanion[Serial] {
	implicit def serialInstance: TypeClass[Serial] = new TypeClass[Serial] {
		def emptyProduct = new Serial[HNil] {
			def serialize(x: HNil) = ByteString()
			def deserialize(bs: ByteString) = Some((HNil, bs))
		}
		def product[L, R <: HList](pHead: Serial[L], pTail: Serial[R]) = new Serial[L :: R] {
			def serialize(x: L :: R) = pHead.serialize(x.head) ++ pTail.serialize(x.tail)
			def deserialize(bs1: ByteString) = for {
				(l, bs2) <- pHead.deserialize(bs1)
				(r, bs) <- pTail.deserialize(bs2)
			} yield (l :: r, bs)
		}
		def project[F, G](serial: => Serial[G], to: F => G, from: G => F) = new Serial[F] {
			def serialize(x: F) = serial.serialize(to(x))
			def deserialize(bs: ByteString) = for {
				(res, bsres) <- serial.deserialize(bs)
			} yield (from(res), bsres)
		}
		def coproduct[L, R <: Coproduct](cHead: => Serial[L], cTail: => Serial[R]) = new Serial[L :+: R] {
			def serialize(x: L :+: R) = x match {
				case Inl(l) => ByteString(0.toByte) ++ cHead.serialize(l)
				case Inr(r) => ByteString(1.toByte) ++ cTail.serialize(r)
			}
			def deserialize(bs: ByteString) = for {
				(head, tail) <- bs.extract
				(x, rest) <- head match {
					case 0 => cHead.deserialize(tail).map { case (l, rest) => (Inl(l), rest) }
					case 1 => cTail.deserialize(tail).map { case (r, rest) => (Inr(r), rest) }
					case _ => None
				}
			} yield (x, rest)
		}
	}
	implicit val intSerial: Serial[Int] = new Serial[Int] {
		def serialize(i: Int) = ByteString(i)
		def deserialize(bs: ByteString) = bs.splitAt(4) match {
			case (l, r) if l.length == 4 =>
				val i = l.toVector.reverse.zipWithIndex.map { case (b, ix) => b.toInt << ix*8 }.sum
				Some((i, r))
			case _ => None
		}
	}
	implicit val longSerial: Serial[Long] = new Serial[Long] {
		def serialize(l: Long) = ByteString(l)
		def deserialize(bs: ByteString) = bs.splitAt(8) match {
			case (l, r) if l.length == 8 =>
				val i = l.toVector.reverse.zipWithIndex.map { case (b, ix) => b.toLong << ix*8 }.sum
				Some((i, r))
			case _ => None
		}
	}
	implicit val stringSerial: Serial[String] = new Serial[String] {
		def serialize(str: String) = {
			val serial = ByteString(str)
			intSerial.serialize(serial.length) ++ serial
		}
		def deserialize(bs1: ByteString) = for {
			(len, bs2) <- intSerial.deserialize(bs1)
			val (string, rest) = bs2.splitAt(len)
			if (string.length == len)
		} yield (string.toString, rest)
	}
	implicit val doubleSerial: Serial[Double] =
		serialInstance.project(longSerial,
			java.lang.Double.doubleToRawLongBits _,
			java.lang.Double.longBitsToDouble _)
}