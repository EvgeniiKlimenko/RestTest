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

/* to delete
  def getAllJson(): Future[Seq[User]] ={
    val res: Future[Seq[Model.User]] = getAll()
    val resSeq: Seq[User] = Await.result(res, pause)
    val toResp: JsValue = resSeq.toJson
    return  res
  }
*/

  def saveUserJson(userIn: User): JsValue = {
    //val usr: User = userJson.convertTo
    val res: Int = Await.result(saveUser(userIn), pause) // convert Future to Int
    if (res != 1) {
      return onFailJson
    } else {
      return onSuccessJson
    }
  }
/* to delete
  def getWithIdJson(id: String): JsValue = {
    //val userObj: Future[Option[User]] = getWithID(id)
    val prepUser: User  = Await.result(getWithID(id), pause).head // convert Future to User.obj
    val userJson: JsValue = prepUser.toJson
    return userJson
  }
*/
  // restriction: this method takes only whole User object with changed and unchanged fields
  def updateUserJson(usrId: String, userIn: User): JsValue = {
    println(userIn.toString)
    val res: Int  = Await.result(updateUser(usrId, userIn), pause) // Update DB
    if (res != 1) {
      return onFailJson
    }else {
      return onSuccessJson
    }
  }

  def deleteUserJson(usrId: String): JsValue ={
    val res: Int = Await.result(deleteUser(usrId), pause)
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

  def getWithID(usrId: String): Future[Option[User]] = {
    val q = table.filter(_.id === usrId)
    val action = q.result.headOption
    val userFromDB: Future[Option[User]] = db.run(action)
    return userFromDB
  }

  def deleteUser(userId: String): Future[Int] = {
    val q = table.filter(_.id === userId)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    return affectedRowsCount
  }

  def updateUser(usrId: String, ussr: User): Future[Int] =  {
    val q = table.filter(_.id === usrId).update(ussr.copy(id = usrId, ussr.firstName, ussr.lastName, ussr.born, ussr.address))
    val res:Future[Int]  = db.run(q)
    return res

  }

}





