import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import scala.concurrent.ExecutionContext
import com.typesafe.config.ConfigFactory
import Controller.UserController
import Model.DBConnection

object ServerMain{
  implicit val system: ActorSystem = ActorSystem("resttest")
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val materializer: Materializer = Materializer.matFromSystem(system)

  def main(args: Array[String]) {
    val db = DBConnection.db
    val conf = ConfigFactory.load()
    val endPoints = UserController.points
    val host: String = "localhost"
    val port: Int = 8090
    Http().bindAndHandle(endPoints, host, port)
    println(s"Server online at http://localhost:8090/")
  }
}
