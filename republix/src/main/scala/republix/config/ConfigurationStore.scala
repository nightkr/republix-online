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
