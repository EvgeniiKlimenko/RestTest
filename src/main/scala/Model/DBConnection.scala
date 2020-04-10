package Model
import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.JdbcBackend._

/*
*   dataSourceClass = "slick.jdbc.DatabaseUrlDataSource"
      properties = {
        driver = "org.postgresql.Driver"
        url = "postgres://postgres:postgres@localhost/resttestdb"
      }

*
* */


object DBConnection {
  val jdbcUrl: String = "jdbc:postgresql://localhost:5432/resttestdb?user=postgres&password=postgres"
  val driver: String = "org.postgresql.Driver"
  var db = Database.forURL(jdbcUrl, driver)
  println("-----> Connection is done!")
  println(db.toString())

}
