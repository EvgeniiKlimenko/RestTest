package Service
import java.text.SimpleDateFormat
import java.util.{Date, UUID}
import slick.sql.{FixedSqlAction, SqlAction}
//import slick.jdbc.PostgresProfile._
import slick._
import spray.json.{RootJsonFormat, _}
import DefaultJsonProtocol._
import slick.dbio.Effect
import slick.lifted.{Query, TableQuery}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import Model.{User, Users}
import Model.DBConnection

object UserService extends TableQuery(new Users(_)) {
  implicit val dateTypeMapper = MappedColumnType.base[java.util.Date, java.sql.Date](
    {
      ud => new java.sql.Date(ud.getTime)
    }, {
      sd => new java.util.Date(sd.getTime)
    }
  )
  var db = DBConnection.db
  val table = TableQuery[Model.Users]
  val pause : Duration = Duration.create(1000, "ms")

  /**
   * Retrieves all users from database.
   * @return Seq[Model.User] containing all users from database.
   */
  def getAll(): Future[Seq[Model.User]] = {
    println("Get all user from DB")
    val q = for (c <- table) yield c
    val a = q.result
    val f: Future[Seq[Model.User]] = db.run(a)
    f
  }

  /**
   * Save new user into database
   * @param usr Takes Model.User object from HTTP request.
   * @param newId New generated Id for new user.
   * @return number of affected rows. Must be 1 if success.
   */
  def saveUser(newId: String, usr: User): Future[Int] = db.run {
    println("Saving a new user")
    val insertUser:User = usr.copy(id=newId)
    table.map(c => (c.id, c.firstName, c.lastName, c.born, c.address)) +=
             (insertUser.id, insertUser.firstName, insertUser.lastName, insertUser.born, insertUser.address)
  }

  /**
   * Retrieves user with specified id from database.
   * @param usrId Id of user we want to retrieve. Received from Http request.
   * @return user with specified id.
   */
  def getWithID(usrId: String): Future[Option[User]] = {
    println(s"Get user with ID: ${usrId}")
    val q = table.filter(_.id === usrId)
    val action = q.result.headOption
    val userFromDB: Future[Option[User]] = db.run(action)
    userFromDB
  }

  /**
   * Deletes user with specified id in database.
   * @param userId Id of user we want to delete. Received from Http request.
   * @return number of affected rows. Must be 1 if success.
   */
  def deleteUser(userId: String): Future[Int] = {
    println(s"Delete user with ID: ${userId}")
    val q = table.filter(_.id === userId)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    affectedRowsCount
  }

  /**
   * Updates existing user in database.
   * @param usrId Id of user we want to update. Received from Http request.
   * @param ussr new Model.User object with changed and unchanged fields. Received from Http request.
   * @return number of affected rows. Must be 1 if success.
   */
  def updateUser(usrId: String, ussr: User): Future[Int] =  {
    println(s"Update user with ID: ${usrId}")
    val q = table.filter(_.id === usrId).update(ussr.copy(id = usrId, ussr.firstName, ussr.lastName, ussr.born, ussr.address))
    val res:Future[Int]  = db.run(q)
    res
  }
}





