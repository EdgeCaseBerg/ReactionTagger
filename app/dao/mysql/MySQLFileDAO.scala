package dao.mysql

import dao._
import model.domain._

import java.time.Instant
import java.nio.file.{ Path, Paths }
import javax.inject.Inject

import play.api.db.DB
import play.api.Application

import anorm._

class MySQLFileDAO @Inject() (implicit app: Application) extends FileDAO {
	def addFileToQueue(queueFile: QueuedFile): Boolean = {
		DB.withTransaction { implicit connection =>
			SQL(
				"""
				INSERT INTO queued_file (id, path, maybeIndexedAt) VALUES ({id},{path},{maybeIndexedAt})
				"""
			).on(
					"id" -> queueFile.id,
					"path" -> queueFile.path.toAbsolutePath.toString,
					"maybeIndexedAt" -> queueFile.maybeIndexedAt.map(_.getEpochSecond())
				).execute()
		}
	}

	def addTaggedFile(taggedFile: TaggedFile): Boolean = {
		DB.withTransaction { implicit connection =>
			SQL(
				"""
				INSERT INTO taggable_file (id, path, maybeIndexedAt, fileOriginId) VALUES ({id},{path},{maybeIndexedAt}, {maybeOrigin})
				"""
			).on(
					"id" -> taggedFile.id,
					"path" -> taggedFile.path.toAbsolutePath.toString,
					"maybeIndexedAt" -> taggedFile.maybeIndexedAt.map(_.getEpochSecond()),
					"maybeOrigin" -> taggedFile.maybeOrigin.map(_.id.toString)
				).execute()
		}
	}

	def removeFromQueue(id: Int): Boolean = {
		DB.withTransaction { implicit connection =>
			SQL(
				"""
				DELETE FROM queued_file WHERE id = {id}
				"""
			).on("id" -> id).execute()
		}
	}

	def removeTaggedFile(id: Int): Boolean = {
		DB.withTransaction { implicit connection =>
			SQL(
				"""
				DELETE FROM taggable_file WHERE id = {id}
				"""
			).on("id" -> id).execute()
		}
	}

	def updateTaggedFilePath(id: Int, path: Path): Boolean = {
		DB.withTransaction { implicit connection =>
			SQL(
				"""
				UPDATE taggable_file SET path = {path} WHERE id = {id}
				"""
			).on("path" -> path.toAbsolutePath.toString, "id" -> id)
				.execute()
		}
	}

	/** Checks if an ID already exists (queued or tagged)
	 */
	def existsById(id: Int): FileExists = {
		withReadOnlyConnection { implicit connection =>
			val foundInQueued = SQL(
				"""SELECT COUNT(*) AS c FROM queued_file WHERE id = {id}"""
			).as(SqlParser.int("c").single)

			val foundInTagged = SQL(
				"""SELECT COUNT(*) AS c FROM taggable_file WHERE id = {id}"""
			).as(SqlParser.int("c").single)

			(foundInTagged, foundInQueued) match {
				case (0, 0) => DoesNotExist
				case (1, 0) => ExistsInTagged
				case (0, 1) => ExistsInQueue
				case _ => ExistsInBoth
			}

		}
	}

	def listQueued(page: Int = 0, perPage: Int = 24): Seq[QueuedFile] = {
		val limit = perPage
		val offset = page * perPage
		withReadOnlyConnection { implicit connection =>
			SQL(
				"""
				SELECT 
					queued_file.id, 
					queued_file.path, 
					queued_file.maybeIndexedAt 
				FROM queued_file
				ORDER BY id
				LIMIT {limit}, {offset}
				"""
			).on(
					"limit" -> limit,
					"offset" -> offset
				).as(queueFileParser.*)
		}
	}

	def listTagged(page: Int = 0, perPage: Int = 24): Seq[TaggedFile] = {
		val limit = perPage
		val offset = page * perPage
		withReadOnlyConnection { implicit connection =>
			val taggedFilesWithoutTags = SQL(
				"""
				SELECT 
					taggable_file.id, 
					taggable_file.path, 
					taggable_file.maybeIndexedAt 
					file_origin.id,
					file_origin.name,
					file_origin.note
				FROM taggable_file
				LEFT JOIN file_origin
					ON taggable_file.fileOriginId = file_origin.id
				ORDER BY taggable_file.id
				LIMIT {limit}, {offset}
				"""
			).on(
					"limit" -> limit,
					"offset" -> offset
				).as(taggedFileParser.*)

			/* Avoid passing no ids to SQL to avoid SQL statement exception */
			if (taggedFilesWithoutTags.isEmpty) {
				return Seq.empty[TaggedFile]
			}

			// TODO: look into anorm WHERE IN later
			val fileIds = taggedFilesWithoutTags.map(_.id).mkString(",")
			val fileTags = SQL(
				"""
				SELECT 
					file_tags.id, 
					file_tags.name, 
					file_tags.note,
					taggable_file_to_file_tag.taggable_file_id
				FROM 
					file_tags JOIN
					taggable_file_to_file_tag ON
					file_tags.id = file_tag_id
				WHERE file_tags.id IN ({fileIds})
				"""
			).on("fileIds" -> fileIds)
				.as((fileTagParser ~ SqlParser.int("taggable_file_to_file_tag.taggable_file_id")).*)
				.map {
					case fileTag ~ fileId => (fileTag, fileId)
				}

			val tagsByFileId = fileTags.groupBy(_._2)
			taggedFilesWithoutTags.map { taggedFile =>
				val fileId = taggedFile.id
				val tagsForFile =
					tagsByFileId.get(fileId)
						.fold(Vector.empty[FileTag])(_.map(_._1).toVector)
				taggedFile.copy(tags = tagsForFile)
			}
		}
	}
}