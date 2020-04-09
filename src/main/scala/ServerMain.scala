import Controller.UserController
import Model.DBConnection
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

// extends App ?
object ServerMain {
  def main(args: Array[String]) {
    val db = DBConnection.db
    val conf = ConfigFactory.load()
    val host: String = "0.0.0.0"
    val port: Int = 8090
    implicit val system: ActorSystem = ActorSystem("resttest")
    implicit val executor: ExecutionContext = system.dispatcher
    implicit val materializer: Materializer = Materializer.matFromSystem(system)

    val endPoints = UserController.points
    Http().bindAndHandle(endPoints, host, port)
    println("Server online at http://localhost:8090/")
  }
}
