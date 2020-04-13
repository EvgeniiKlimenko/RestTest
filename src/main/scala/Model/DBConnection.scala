package Model
import slick.jdbc.JdbcBackend._

/**
 * DataBase connection through jdbc url.
 * Using postgres DB.
 */
object DBConnection {
  // DataBase connection through jdbc url:
  val jdbcUrl: String = "jdbc:postgresql://localhost:5432/resttestdb?user=postgres&password=postgres"
  val driver: String = "org.postgresql.Driver"
  var db = Database.forURL(jdbcUrl, driver)
  println("-----> Connection is done!")

}
