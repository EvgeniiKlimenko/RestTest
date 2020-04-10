import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer

import scala.concurrent.{ExecutionContext, Future}
import com.typesafe.config.ConfigFactory
import Controller.UserController

import scala.util.{Failure, Success}
import Model.{DBConnection, User}
import Service.UserService

import scala.concurrent.duration.Duration
//import Model.DBConnection


object ServerMain {
  def main(args: Array[String]) {
    val wait : Duration = Duration.create(100, "ms")
    val db = DBConnection.db
    val conf = ConfigFactory.load()
    
    implicit val system: ActorSystem = ActorSystem("resttest")
    implicit val executor: ExecutionContext = system.dispatcher
    implicit val materializer: Materializer = Materializer.matFromSystem(system)
    val testUser: User = new User(2, "Evgenii", "Klimenko", "SaintPetersburg")
    /*
    val f1 = UserService.saveUser(testUser)
    f1.onComplete{
      case Success(s)=> f1.foreach(println)
      case Failure(t) => t.printStackTrace()
    }
    val f2: Future[Seq[Model.User]] = UserService.getAll()
    f2.onComplete{
      case Success(s)=> f2.foreach(println)
      case Failure(t) => t.printStackTrace()
    }
    */
    val f3 = UserService.getWithID(2)
      f3.onComplete{
        case Success(s)=> f3.foreach(println)
        case Failure(t) => t.printStackTrace()
      }


    //val par1 = f.result(wait)
    //val par1: Seq[User] = f.result(wait)


    /*
    val endPoints = UserController.points
    val host: String = "0.0.0.0"
    val port: Int = 8090
    Http().bindAndHandle(endPoints, host, port)

    println(s"Server online at http://localhost:8090/")
*/
  }
}
