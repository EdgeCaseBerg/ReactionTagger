package model.domain

import java.util.UUID

/** A meta tag indicating a file has been indexed as pertinent to the name of this tag
 */
case class FileTag(
	id: UUID,
	name: String,
	note: Option[String]
)