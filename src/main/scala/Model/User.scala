package Model

import slick.jdbc.PostgresProfile.api._
import java.util.Date
import java.sql.Date

/**
 * Our user entity
 * @param id unique user id. It is stored in database as string.
 * @param firstName user's first name
 * @param lastName user's last name
 * @param born  user's date of birth. Format: dd-MM-yyyy
 * @param address user's address
 */
final case class User(
  id: String,
  firstName: String,
  lastName: String,
  born: java.util.Date,
  address: String
)
/**
 * Mapped user table class.
 */
class Users(tag: Tag) extends Table[User](tag, "users") {
  // Mapping dates formats.
  implicit val dateTypeMapper = MappedColumnType.base[java.util.Date, java.sql.Date](
    {
      ud => new java.sql.Date(ud.getTime)
    }, {
      sd => new java.util.Date(sd.getTime)
    }
  )
  def id = column[String]("id", O.PrimaryKey)
  def firstName = column[String]("firstname")
  def lastName = column[String]("lastname")
  def born  = column[java.util.Date]("born")
  def address = column[String]("address")
  def * = (id, firstName, lastName, born, address) <>(User.tupled, User.unapply)





}
