package Model

import slick.jdbc.PostgresProfile.api._
//import java.util.Date
//import java.sql.Date
import java.util.UUID


final case class UserItem(name: String, id: Long)

final case class User( 
  id: Long,
  firstName: String,
  lastName: String,
  //dateOfBirth: Option[java.util.Date],
  address: String
)

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def firstName = column[String]("firstName")
  def lastName = column[String]("lastName")
  //def dateOfBirth  = column[java.util.Date]("dateOfBirth")
  def address = column[String]("address")
  def * = (id, firstName, lastName, address) <>(User.tupled, User.unapply)

/*
  implicit val dateTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Date](
    {
      ud => new java.sql.Date(ud.getTime)
    }, {
      sd => new java.util.Date(sd.getTime)
    }
  )
*/

}
