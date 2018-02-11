import com.typesafe.config._

val confFileName = System.getProperty("config.file", "conf/application.conf")

val conf = ConfigFactory.parseFile(new File(confFileName)).resolve()

flywayUrl :=  conf.getString("db.default.jdbcUrl")

flywayDriver := conf.getString("db.default.driverClassName")

flywayLocations := Seq("filesystem:conf/migrations")