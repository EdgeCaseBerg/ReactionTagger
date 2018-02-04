package global

import play.api.{ GlobalSettings, Application, Play }
import com.google.inject.Guice

import service._

object Global extends GlobalSettings {

	lazy val injector = Guice.createInjector(new AppModule(Play.current.configuration))

	override def getControllerInstance[A](clazz: Class[A]) = getInstance(clazz)

	def getInstance[A](clazz: Class[A]) = injector.getInstance(clazz)

	override def beforeStart(app: Application) {
		val cs = getInstance(classOf[ConfigurationService])
		cs.loadConf()
	}
}