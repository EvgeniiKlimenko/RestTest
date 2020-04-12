package Service
import java.util.UUID

import Model.{User, Users}
import Model.DBConnection
import slick.jdbc.PostgresProfile.api._
import slick.sql.{FixedSqlAction, SqlAction}
import slick.jdbc.PostgresProfile._
import slick._
import spray.json._
import DefaultJsonProtocol._
import slick.dbio.Effect
import slick.lifted.{Query, TableQuery}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration



object UserService extends TableQuery(new Users(_)) {
  val onSuccessJson: JsValue = """{ "Success": "true" }""".parseJson
  val onFailJson: JsValue = """{ "Success": "false" }""".parseJson
  implicit val myFormat: RootJsonFormat[User] = jsonFormat5(Model.User)
  /*
  implicit val UserJsonWriter = new JsonWriter[Model.User] {
    def write(user: Model.User): JsValue = {
      JsObject(
        "_id" -> JsString(user.id),
        "firstname" -> JsString(user.firstName),
        "lastname" -> JsString(user.lastName),
        "birth" -> JsString(user.birth),
        "address" -> JsString(user.address)
      )
    }
  }
*/
  var db = DBConnection.db
  val table = TableQuery[Model.Users]
  val pause : Duration = Duration.create(1000, "ms")

  def getAllJson(): Unit ={
    val res: Future[Seq[Model.User]] = getAll()
    val resSeq: Seq[User] = Await.result(res, pause)

  }

  def saveUserJson(userIn: User): JsValue = {
    //val usr: User = userJson.convertTo
    val res: Int = Await.result(saveUser(userIn), pause) // convert Future to Int
    if (res != 1) {
      return onFailJson
    } else {
      return onSuccessJson
    }
  }


  def getWithIdJson(id: String): JsValue = {
    //val userObj: Future[Option[User]] = getWithID(id)
    val prepUser: User  = Await.result(getWithID(id), pause).head // convert Future to User.obj
    val userJson: JsValue = prepUser.toJson
    return userJson
  }

  // restriction: this method takes only whole User object with changed and unchanged fields
  def updateUserJson(id: String, userIn: User): JsValue ={ // unused id:String
    //val usr: User = userJson.convertTo[User]
    println(userIn.toString)
    val res: Int  = Await.result(updateUser(userIn.id, userIn), pause) // Update DB
    //val userToResponse: User = updatedUser.copy(id=UUID.randomUUID().toString) // Prepare for response
    if (res != 1) {
      return onFailJson
    }else {
      return onSuccessJson
    }
  }

  def deleteUserJson(id: String): JsValue ={
    val res: Int = Await.result(deleteUser(id), pause)
    if (res != 1) {
      return onFailJson
    }else {
      return onSuccessJson
    }
  }

  // -----> PRIVATES!

  def getAll(): Future[Seq[Model.User]] = {
    val q = for (c <- table) yield c
    val a = q.result
    val f: Future[Seq[Model.User]] = db.run(a)
    return f;
  }

  def saveUser(usr: User): Future[Int] = db.run {
    val insertUser:User = usr.copy(id=UUID.randomUUID().toString)
    table.map(c => (c.id, c.firstName, c.lastName, c.born, c.address)) += (insertUser.id, insertUser.firstName, insertUser.lastName, insertUser.born, insertUser.address)
  }

  def getWithID(userId: String): Future[Option[User]] = {
    val q = table.filter(_.id === userId)
    val action = q.result.headOption
    val userFromDB: Future[Option[User]] = db.run(action)
    //val ussr: User = Await.result(userFromDB, wait).head
    return userFromDB
  }

  def deleteUser(userId: String): Future[Int] = {
    val q = table.filter(_.id === userId)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    //val sql = action.statements.head
    return affectedRowsCount
  }

  def updateUser(userId: String, ussr: User): Future[Int] =  { // unused userId:String
    val q = table.filter(_.id === userId).update(ussr.copy(id = ussr.id, ussr.firstName, ussr.lastName, ussr.born, ussr.address))
    val res:Future[Int]  = db.run(q)
    return res

  }

}





