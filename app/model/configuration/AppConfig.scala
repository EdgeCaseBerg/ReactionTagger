package model.configuration

import java.nio.file.Path

case class AppConfig(
	directoriesToScan: Vector[DirectoryConfig],
	directoriesToIgnore: Vector[Path]
)

import play.api.libs.json._

object AppConfig {
	implicit val appConfigFormat = Json.format[AppConfig]
}