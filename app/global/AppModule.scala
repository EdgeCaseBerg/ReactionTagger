package global

import com.google.inject.{ AbstractModule, Provides }
import javax.inject.Named
import java.nio.file.Paths

import play.api.Configuration

class AppModule(configuration: Configuration) extends AbstractModule {
	override def configure() {
		bind(classOf[Configuration]).toInstance(configuration)
	}

	@Provides
	@Named("configurationLocation")
	def providesConfigurationLocation(appConf: Configuration) = {
		appConf.getString("app.config.location") match {
			case Some(strPath) => Paths.get(strPath)
			case None => Paths.get("reaction-tagger.conf.json")
		}
	}
}