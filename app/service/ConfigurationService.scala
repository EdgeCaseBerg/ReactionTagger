package service

import model.configuration._

import java.nio.file.{ Files, Path, StandardOpenOption }
import java.nio.charset.Charset
import java.io.IOException
import javax.inject.{ Inject, Named }

import scala.util.control.NonFatal

object ConfigurationService {
	sealed trait SaveResult
	case object SaveSuccess extends SaveResult
	sealed trait SaveFailure extends SaveResult {
		def message: String
		def throwable: Throwable
	}
	case class GeneralFailure(throwable: Throwable) extends SaveFailure {
		def message = throwable.getMessage()
	}
	case class AccessFailure(throwable: Throwable) extends SaveFailure {
		def message = throwable.getMessage()
	}

	final val defaultConfiguration = {
		AppConfig(Vector.empty, Vector.empty)
	}

	class ConfigurationCorruptException(message: String) extends IllegalStateException(message)

}

import play.api.libs.json._

class ConfigurationService @Inject() (
		@Named("configurationLocation") val configurationLocation: Path
) {
	import ConfigurationService._
	def loadConf(): AppConfig = {
		val byteArray = {
			try {
				Files.readAllBytes(configurationLocation)
			} catch {
				case e: IOException =>
					/* Save the configuration */
					saveConf(defaultConfiguration) match {
						case e: SaveFailure => throw e.throwable
						case _ =>
					}

					/*  Return the bytes we know we just saved*/
					val fileData = Json.prettyPrint(Json.toJson(defaultConfiguration))
					val bytes = fileData.getBytes(Charset.forName("UTF-8"))
					bytes
			}
		}
		try {
			Json.parse(byteArray).validate[AppConfig] match {
				case JsSuccess(appConfig, _) => appConfig
				case JsError(errors) =>
					val message = Json.prettyPrint(JsError.toFlatJson(errors))
					throw new ConfigurationCorruptException(message)
			}
		} catch {
			case NonFatal(e) =>
				throw new ConfigurationCorruptException(e.getMessage())
		}

	}

	def saveConf(appConfig: AppConfig): SaveResult = {
		try {
			val fileData = Json.prettyPrint(Json.toJson(defaultConfiguration))
			val bytes = fileData.getBytes(Charset.forName("UTF-8"))
			Files.write(configurationLocation, bytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
			SaveSuccess
		} catch {
			case e: SecurityException => AccessFailure(e)
			case NonFatal(e) => GeneralFailure(e)
		}
	}
}