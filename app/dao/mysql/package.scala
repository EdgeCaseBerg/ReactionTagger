package dao

import play.api.Application
import play.api.db.DB
import java.time.Instant
import java.nio.file.Paths
import java.sql.Connection
import java.util.UUID

import anorm._
import model.domain._

package object mysql {

	private[mysql] def withReadOnlyConnection[R](
		block: Connection => R
	)(implicit app: Application): R = {
		DB.withConnection { implicit connection =>
			try {
				connection.setReadOnly(true)
				connection.setAutoCommit(false)
				block(connection)
			} finally {
				connection.setReadOnly(false)
				connection.setAutoCommit(true)
			}
		}
	}

	private[mysql] val queueFileParser: RowParser[QueuedFile] = {
		import SqlParser._
		get[Int]("queued_file.id") ~
			get[String]("queued_file.path") ~
			get[Option[Long]]("queued_file.maybeIndexedAt") map {
				case id ~ strPath ~ maybeInt => {
					QueuedFile(
						id,
						Paths.get(strPath),
						maybeInt.map(Instant.ofEpochSecond)
					)
				}
			}
	}

	private[mysql] val taggedFileParser: RowParser[TaggedFile] = {
		import SqlParser._
		get[Int]("taggable_file.id") ~
			get[String]("taggable_file.path") ~
			get[Option[Long]]("taggable_file.maybeIndexedAt") ~
			get[Option[String]]("file_origin.id") ~
			get[Option[String]]("file_origin.name") ~
			get[Option[String]]("file_origin.note") map {
				case fileId ~ strPath ~ maybeInt ~ maybeOriginId ~ maybeOriginName ~ maybeOriginNote => {
					val maybeFileOrigin = for {
						id <- maybeOriginId
						name <- maybeOriginName
					} yield FileOrigin(UUID.fromString(id), name, maybeOriginNote)
					TaggedFile(
						fileId,
						Paths.get(strPath),
						maybeInt.map(Instant.ofEpochSecond),
						Vector.empty[FileTag],
						maybeFileOrigin
					)
				}
			}
	}

	private[mysql] val fileTagParser: RowParser[FileTag] = {
		import SqlParser._
		get[String]("file_tags.id") ~
			get[String]("file_tags.name") ~
			get[Option[String]]("file_tags.note") map {
				case id ~ name ~ note => {
					FileTag(UUID.fromString(id), name, note)
				}
			}
	}
}