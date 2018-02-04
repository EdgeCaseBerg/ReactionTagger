package service

import model.configuration._

import java.nio.file.{ Files, Path, StandardOpenOption }
import java.nio.charset.Charset
import java.io.IOException

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

class ConfigurationService(
		val configurationLocation: Path
) {
	import ConfigurationService._
	def loadConf(): AppConfig = {
		val byteArray = {
			try {
				Files.readAllBytes(configurationLocation)
			} catch {
				case e: IOException =>
					val fileData = Json.prettyPrint(Json.toJson(defaultConfiguration))
					val bytes = fileData.getBytes(Charset.forName("UTF-8"))
					Files.write(configurationLocation, bytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
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
		GeneralFailure(new RuntimeException(":("))
	}
}