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
	def apply(i: Int): ByteString = ByteString(Vector(3, 2, 1, 0).map(x => ((i >> x) & 0xFF).toByte))
	def apply(l: Long): ByteString = ByteString(Vector(7, 6, 5, 4, 3, 2, 1, 0).map(x => ((l >> x) & 0xFF).toByte))
	def apply(bytes: Array[Byte]): ByteString = ByteString(bytes.toVector)
	def apply(): ByteString = ByteString(Vector())

}