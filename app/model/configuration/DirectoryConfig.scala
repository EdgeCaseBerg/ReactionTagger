package model.configuration

import java.nio.file.Path

case class DirectoryConfig(
	path: Path,
	shouldRecurse: Boolean
)

import play.api.libs.json._

object DirectoryConfig {
	implicit val directoryConfigFormat = Json.format[DirectoryConfig]
}