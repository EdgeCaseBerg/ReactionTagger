package dao

import model.domain._

import java.nio.file.Path

trait FileDAO {
	def addFileToQueue(queueFile: QueuedFile): Boolean

	/** Add a tagged file to the database
	 *  @note Does not link tags/origins to file
	 *       please call appropriate methods in
	 *       relevant DAOs to do so
	 */
	def addTaggedFile(taggedFile: TaggedFile): Boolean

	def removeFromQueue(id: Int): Boolean

	def removeTaggedFile(id: Int): Boolean

	/** Checks if an ID already exists (queued or tagged)
	 */
	def existsById(id: Int): FileExists

	def updateTaggedFilePath(id: Int, path: Path): Boolean

	def listQueued(page: Int = 0, perPage: Int = 24): Seq[QueuedFile]

	def listTagged(page: Int = 0, perPage: Int = 24): Seq[TaggedFile]
}