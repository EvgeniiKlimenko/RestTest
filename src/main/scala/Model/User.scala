package Model

import slick.jdbc.PostgresProfile.api._
import java.util.Date
import java.sql.Date
import java.util.UUID

final case class User(
  id: String,
  firstName: String,
  lastName: String,
  born: java.util.Date,
  address: String
)

//(id, firstName, lastName, born, address)
class Users(tag: Tag) extends Table[User](tag, "users") {
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
