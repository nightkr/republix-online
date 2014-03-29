package republix.util.file

import java.nio.file.Path

class RichPath(val path: Path) extends AnyVal {
  def /(other: String): Path = path.resolve(other)
}

class RichOptPath(val path: Option[Path]) extends AnyVal {
  def /(other: String): Option[Path] = path.map(_ / other)
}
