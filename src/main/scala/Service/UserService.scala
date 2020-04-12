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
  val onSuccessJson: JsValue = """{ "Success": "true" }""".parseJson
  val onFailJson: JsValue = """{ "Success": "false" }""".parseJson
  //implicit val myFormat: RootJsonFormat[User] = jsonFormat5(Model.User)
  implicit val dateTypeMapper = MappedColumnType.base[java.util.Date, java.sql.Date](
    {
      ud => new java.sql.Date(ud.getTime)
    }, {
      sd => new java.util.Date(sd.getTime)
    }
  )

  implicit val UserJsonWriter = new RootJsonFormat[Model.User] {
    def write(user: Model.User): JsValue = {
      JsObject(
        "id" -> JsString(user.id),
        "firstname" -> JsString(user.firstName),
        "lastname" -> JsString(user.lastName),
        "born" -> JsString(user.born.toString),
        "address" -> JsString(user.address)
      )
    }
    def read(json: JsValue): User = {
      val sdf =  new SimpleDateFormat("dd-MM-yyyy")
      val fields = json.asJsObject("Expected user object").fields
      User(id = fields("id").convertTo[String],
           firstName = fields("firstName").convertTo[String],
           lastName = fields("lastName").convertTo[String],
           born = sdf.parse(fields("born").convertTo[String]),
           address = fields("address").convertTo[String]
           )
    }
  }

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

  def getAll(): Future[Seq[Model.User]] = {
    println("Get all user from DB")
    val q = for (c <- table) yield c
    val a = q.result
    val f: Future[Seq[Model.User]] = db.run(a)
    return f;
  }

  def saveUser(usr: User): Future[Int] = db.run {
    println("Saving a new user")
    val insertUser:User = usr.copy(id=UUID.randomUUID().toString)
    println("Preparing a new user")
    table.map(c => (c.id, c.firstName, c.lastName, c.born, c.address)) +=
             (insertUser.id, insertUser.firstName, insertUser.lastName, insertUser.born, insertUser.address)

  }

  def getWithID(usrId: String): Future[Option[User]] = {
    println(s"Get user with ID: ${usrId}")
    val q = table.filter(_.id === usrId)
    val action = q.result.headOption
    val userFromDB: Future[Option[User]] = db.run(action)
    return userFromDB
  }

  def deleteUser(userId: String): Future[Int] = {
    println(s"Delete user with ID: ${userId}")
    val q = table.filter(_.id === userId)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    return affectedRowsCount
  }

  def updateUser(usrId: String, ussr: User): Future[Int] =  {
    println(s"Update user with ID: ${usrId}")
    val q = table.filter(_.id === usrId).update(ussr.copy(id = usrId, ussr.firstName, ussr.lastName, ussr.born, ussr.address))
    val res:Future[Int]  = db.run(q)
    return res

  }

}





