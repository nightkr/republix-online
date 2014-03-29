package republix.config

import java.nio.file.{StandardOpenOption, Files, Path}
import org.json4s._
import org.json4s.jackson.Serialization
import java.nio.charset.Charset
import resource._

abstract class ConfigurationStore[T <: AnyRef : Manifest] {
  private var cache: Option[T] = None

  protected implicit val formats = DefaultFormats

  val empty: T
  val path: Path

  def get: T = {
    cache.getOrElse {
      val conf = {
        if (Files.exists(path)) {
          managed(Files.newBufferedReader(path, ConfigurationStore.CHARSET))
            .acquireAndGet(Serialization.read[T](_))
        } else {
          empty
        }
      }
      cache = Some(conf)
      conf
    }
  }

  def save(modified: T) {
    val parent = path.getParent
    if (Files.notExists(parent))
      Files.createDirectories(parent)

    managed(Files.newBufferedWriter(path, ConfigurationStore.CHARSET, StandardOpenOption.WRITE, StandardOpenOption.CREATE))
      .foreach(Serialization.writePretty(modified, _))

    cache = Some(modified)
  }
}

object ConfigurationStore {
  val CHARSET = Charset.forName("UTF-8")
}
