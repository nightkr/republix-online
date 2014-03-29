package republix.util

import java.nio.file.Path
import scala.language.implicitConversions

package object file {
  implicit def richpath(path: Path): RichPath = new RichPath(path)

  implicit def richoptpath(path: Option[Path]): RichOptPath = new RichOptPath(path)
}
