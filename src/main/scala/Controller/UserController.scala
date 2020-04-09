package Controller
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives
import akka.actor.Actor
import Model.Users
import Model.User
import Service.UserService
import scala.util.{Failure, Success}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.slf4j.LoggerFactory
import spray.json._


object UserController extends Directives {
  val points = path("user") {
    get{
      println("GET all users")
      onComplete(UserService.getAll()){
        case Success(users) => complete(users)
        case Failure(ex) => complete(BadRequest, s"Houston, we have a problem: ${ex.getMessage}")
      }
    } ~
    post {
      println("POST a new user")
      entity(as[User]){ user =>
          onComplete(UserService.saveUser(user)){
            case Success(user) => complete(Created)
            case Failure(ex) => complete(BadRequest, s"Houston, we have a problem: ${ex.getMessage}")
          }
      }
    } ~
  }
  //work with user with specific ID
  path("user" / LongNumber){ userId =>
    get{
      println(s"GET user with ID = $userId")
      onComplete(UserService.getWithID(userId)){
        case Success(Some(user: User)) => complete(user)
        case Success(None) => complete(NotFound, "No such user!")
        case Failure(ex) => complete(BadRequest, s"Houston, we have a problem: ${ex.getMessage}")
      }
    } ~
      put{
        println(s"UPDATE user with ID = $userId")
        entity(as[User]){ user =>
          onComplete(UserService.updateUser(userId, user)){
            case Success(Some(updatedUser)) => complete(OK) // (OK, updatedUser)
            case Success(None) => complete(NotAcceptable, "Invalid Info")
            case Failure(ex) =>  complete(BadRequest, s"Houston, we have a problem: ${ex.getMessage}")
          }
        }
      } ~
    delete{
      println(s"DELETE user with ID = $userId")
      onComplete(UserService.deleteUser(userId)){
        case Success(ok) => complete(OK)
        case Failure(ex) => complete(BadRequest, s"Houston, we have a problem: ${ex.getMessage}")
      }
    } ~
  }

}
