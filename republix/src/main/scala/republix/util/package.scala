package republix

package object util {


	class Init[A] {
		private var value: Option[A] = None

		def init(x: A) = {
			for (y <- value) throw new Exception(s"Cannot initialize to $x; already initialized to $y!")
			value = Some(x)
		}
		def apply() = value.getOrElse(throw new Exception("Not initialized"))
	}
	def uninitialized[A]: Init[A] = new Init[A]

}