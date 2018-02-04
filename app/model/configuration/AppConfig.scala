package model.configuration

import java.nio.file.Path

case class AppConfig(
	directoriesToScan: Vector[DirectoryConfig],
	directoriesToIgnore: Vector[Path]
)
