package model.domain

import java.util.UUID

case class FileOrigin(
	id: UUID,
	name: String,
	note: Option[String]
)
