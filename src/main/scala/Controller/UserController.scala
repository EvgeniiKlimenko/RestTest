package Controller
import java.util.UUID

import Model.User

import scala.language.postfixOps
import scala.util.{Failure, Success}
import akka.http.scaladsl.server.{Directives, Route}
import spray.json._
import akka.http.scaladsl.model.StatusCodes._
import Service.UserService
import akka.actor.ActorSystem
import spray.json.DefaultJsonProtocol.jsonFormat5

import scala.concurrent.{ExecutionContext, Future}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.stream.{ActorMaterializer, Materializer}
import spray.json.DefaultJsonProtocol._


object UserController extends Directives  {
  val errorMsg: String = s"internal error"
  val unexpectedMsg: String = s"unexpected result"
  implicit val myFormat: RootJsonFormat[Model.User] = jsonFormat5(Model.User)

  implicit val system: ActorSystem = ActorSystem("resttest")
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val materializer: Materializer = Materializer.matFromSystem(system)
  implicit val executionContext = system.dispatcher

  val points: Route = concat(
      get {
        println()
        path("users") {
        val usersFromDb: Future[Seq[Model.User]] = UserService.getAll()
        onComplete(usersFromDb) {
          case Success(users) => complete(users)
          case Failure(ex) => complete(BadRequest, s"Houston, we have a problem: ${ex.getMessage}")
          }
        }
      },
      post {
        println("POST a new user")
        path("create-user")
        entity(as[User]){ user =>
          val resp: Future[Int] = UserService.saveUser(user)
            onComplete(resp){
              case Success(resp) => complete(StatusCodes.Created)
              case Failure(ex) => complete(BadRequest, s"Houston, we have a problem: ${ex.getMessage}")
            }
        }
      },
  //work with user with specific ID
    get {
      pathPrefix("user" / JavaUUID) { userId =>
        println(s"GET user with ID = $userId")
        val userFromDb: Future[Option[Model.User]] = UserService.getWithID(userId.toString)
        onComplete(userFromDb) {
          case Success(Some(user: Model.User)) => complete(user)
          case Failure(ex) => complete(StatusCodes.NotFound, "No such user! ${ex.getMessage}")
        }
      }
    },
      put{
        println("UPDATE user with ID")
        pathPrefix("update-user" / JavaUUID) { userId =>
          entity(as[Model.User]) { user =>
            println(userId.toString)
            val req: Future[Int] = UserService.updateUser(userId.toString, user)
            onComplete(req) {
              case Success(number) => complete(StatusCodes.OK)
              case Failure(ex) => complete(BadRequest, s"Houston, we have a problem: ${ex.getMessage}")
            }
          }
        }
      },
    delete {
      println("DELETE user with ID")
      pathPrefix("delete-user" / JavaUUID) { userId =>
        onComplete(UserService.deleteUser(userId.toString)) {
          case Success(ok) => complete(StatusCodes.OK)
          case Failure(ex) => complete(BadRequest, s"Houston, we have a problem: ${ex.getMessage}")
        }
      }
    }
  )
}
