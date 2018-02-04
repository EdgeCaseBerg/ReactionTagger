package model.configuration

import java.nio.file.Path

case class DirectoryConfig(
	path: Path,
	shouldRecurse: Boolean
)