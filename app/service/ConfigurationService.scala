package service

import model.configuration._

import java.nio.file.Path

object ConfigurationService {
	sealed trait SaveResult
	case object SaveSuccess extends SaveResult
	sealed trait SaveFailure extends SaveResult {
		def message: String
	}
	case class GeneralFailure(throwable: Throwable) extends SaveFailure {
		def message = throwable.getMessage()
	}
	case class AccessFailure(throwable: Throwable) extends SaveFailure {
		def message = throwable.getMessage()
	}

}

class ConfigurationService(
		val configurationLocation: Path
) {
	import ConfigurationService._
	def loadConf(): AppConfig = ???

	def saveConf(appConfig: AppConfig): SaveResult = {
		GeneralFailure(new RuntimeException(":("))
	}
}