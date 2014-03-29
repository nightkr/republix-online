package republix.config

import java.nio.file.Path
import republix.util.file.CommonPaths

case class Configuration(lastUse: LastUseConfig = LastUseConfig())

case class LastUseConfig(server: String = "", port: String = "", party: String = "")

object Configuration extends ConfigurationStore[Configuration] {
  override val path: Path = CommonPaths.configFile
  override val empty: Configuration = Configuration()
}
