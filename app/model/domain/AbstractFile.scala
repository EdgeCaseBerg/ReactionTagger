package model.domain

import java.nio.file.Path
import java.time.Instant

sealed abstract trait AbstractFile {
	/** @note Is the hashcode of the File itself
	 */
	def id: Int
	def path: Path
	def maybeIndexedAt: Option[Instant]
	def isTagged: Boolean
}

case class TaggedFile(
		id: Int,
		path: Path,
		maybeIndexedAt: Option[Instant],
		tags: Vector[FileTag],
		maybeOrigin: Option[FileOrigin]
) extends AbstractFile {
	def isTagged = true
}

case class QueuedFile(
		id: Int,
		path: Path,
		maybeIndexedAt: Option[Instant]
) extends AbstractFile {
	def isTagged = false
}

object QueuedFile {
	def apply(path: Path): QueuedFile = {
		QueuedFile(path.hashCode, path, None)
	}
}