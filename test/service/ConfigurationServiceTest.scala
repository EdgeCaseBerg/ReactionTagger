package service

import java.nio.file.{ Files, Paths, StandardOpenOption, Path }
import java.nio.charset.Charset

import scala.util.control.NonFatal

import play.api.libs.json._
import model.configuration._

import org.scalatest.{ FlatSpec, Matchers }

class ConfigurationServiceTest() extends FlatSpec with Matchers {

	import ConfigurationService._

	def withConf[T](appConfig: AppConfig)(f: (ConfigurationService, Path) => T) = {
		val path = Files.createTempFile("foo", ".json")
		val fileData = Json.prettyPrint(Json.toJson(appConfig))

		try {
			Files.write(path, fileData.getBytes(Charset.forName("UTF-8")), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
		} catch {
			case NonFatal(e) =>
				fail(s"Could not run test because: ${e.getMessage()}")
		}

		val cs = new ConfigurationService(path)
		try {
			f(cs, path)
		} finally {
			Files.deleteIfExists(path)
		}

	}

	val testConf = AppConfig(
		directoriesToScan = Vector(
			DirectoryConfig(
				Paths.get(".").toAbsolutePath(),
				false
			)
		),
		directoriesToIgnore = Vector(
			Paths.get("target").toAbsolutePath()
		)
	)

	"ConfigurationService#loadConf" should "load a configuration that exists" in {
		withConf(testConf) {
			case (configurationService, _) =>
				val appConfig = configurationService.loadConf()
				assertResult(testConf)(appConfig)
		}
	}

	it should "create a default configuration if one does not exist" in {
		val tmpDirPath = Files.createTempDirectory("foo")
		try {
			val confPath = tmpDirPath.resolve("dne.json")
			try {
				val cs = new ConfigurationService(confPath)
				val conf = cs.loadConf()
				assertResult(defaultConfiguration)(conf)
			} finally {
				Files.deleteIfExists(confPath)
			}
		} finally {
			Files.deleteIfExists(tmpDirPath)
		}

	}

	it should "throw an exception if a configuration cannot be created when one needs to be" in {
		withConf(testConf) {
			case (configurationService, path) =>
				Files.deleteIfExists(path)
				val sm = System.getSecurityManager()
				try {
					/* Disallow writing to a file so that loadConf cannot create the default configuration */
					System.setSecurityManager(
						new SecurityManager() {
							override def checkWrite(file: String) {
								if (file.equals(path.toAbsolutePath().toString())) {
									super.checkWrite(file)
									throw new SecurityException("!")
								}
							}
							override def checkDelete(file: String) {}

							override def checkPermission(perm: java.security.Permission) = {}
						}
					)
					intercept[SecurityException] {
						configurationService.loadConf()
					}
					/* Set the SM back so withConf clean up can run properly */
					System.setSecurityManager(sm)
				} finally {
					System.setSecurityManager(sm)
				}

		}
	}

	it should "throw a corruption exception if the configuration file is not well formed" in {
		withConf(testConf) {
			case (configurationService, path) =>
				Files.write(path, "some junk".getBytes(), StandardOpenOption.APPEND)
				intercept[ConfigurationCorruptException] {
					configurationService.loadConf()
				}
		}
	}

	"ConfigurationService#saveConf" should "return a SaveSuccess if a configuration is saved correctly" in {
		pending
	}

	it should "return an AccessFailure if the configuration cannot be saved due to security reasons" in {
		pending
	}

	it should "return a GeneralFailure for any other exception during save" in {
		pending
	}
}