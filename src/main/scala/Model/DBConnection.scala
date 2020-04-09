package Model
import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.JdbcBackend._
import slick.jdbc.PostgresProfile.api

object DBConnection {
  var db = Database.forConfig("dbConf")
  val conf: Config = ConfigFactory.load()
  val driver: String = conf.getString("db.driver")

  //val profile: JdbcProfile = driver "org.postgresql.Driver" => PostgresProfile
}
