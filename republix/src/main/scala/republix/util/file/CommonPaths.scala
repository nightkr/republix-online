package republix.util.file

import java.nio.file.{Path, Paths}


object CommonPaths {
  def configDir: Path = Seq(
    getFileEnv("APPDATA") / "Republix", // Windows
    getFileEnv("XDG_CONFIG_HOME") / "republix", // Modern linux
    getFileEnv("HOME") / ".republix" // Fallback *NIX
  ).flatMap(_.toSeq).head

  def configFile = configDir / "config.json"

  private def getFileEnv(name: String): Option[Path] = {
    Option(System.getenv(name)).map(Paths.get(_))
  }
}
