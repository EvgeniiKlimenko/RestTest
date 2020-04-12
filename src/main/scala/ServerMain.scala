import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer

import scala.concurrent.{Await, CanAwait, ExecutionContext, Future}
import com.typesafe.config.ConfigFactory
import Controller.UserController

import scala.util.{Failure, Success}
import Model.{DBConnection, User}
import Service.UserService

import scala.concurrent.duration.Duration
//import Model.DBConnection


object ServerMain {
  implicit val system: ActorSystem = ActorSystem("resttest")
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val materializer: Materializer = Materializer.matFromSystem(system)

  def main(args: Array[String]) {
    val db = DBConnection.db
    val conf = ConfigFactory.load()
    //testMain()
    val endPoints = UserController.points
    val host: String = "localhost"
    val port: Int = 8090
    Http().bindAndHandle(endPoints, host, port)
    println(s"Server online at http://localhost:8090/")

  }

  def testMain(): Unit ={
    //testDeleteUser()
    //testGetWithId(2)
    //testSaveUser()
    //testGetAll()
    testUpdateUser()
    testGetWithId("c18e751d-8ba8-477f-9de2-4ec6a9809005")

    //val wait : Duration = Duration.create(100, "ms")
    //val par1 = f.result(wait)
    //val par1: Seq[User] = f.result(wait)

  }

  def testGetAll(): Unit = {
    val res: Future[Seq[Model.User]] = UserService.getAll()
    res.onComplete{
      case Success(s)=> res.foreach(println)
      case Failure(t) => t.printStackTrace()
    }
    //val par1: Seq[User] = res.result(wait)
  }

  def testSaveUser(): Unit ={
    val testUser: User = new User(UUID.randomUUID().toString, "Evgenii", "Klimenko", "31.07.1989", "SaintPetersburg")
    val res: Future[Int] = UserService.saveUser(testUser)
    res.onComplete{
      case Success(s)=> res.foreach(println)
      case Failure(t) => t.printStackTrace()
    }
  }

  def testGetWithId(id: String): Unit ={
    val res: Future[Option[User]] = UserService.getWithID(id)
    res.onComplete{
      case Success(s)=> res.foreach(println)
      case Failure(t) => t.printStackTrace()
    }
  }

  def testDeleteUser(): Unit ={
    val wait : Duration = Duration.create(1000, "ms")
    val res: Future[Int] = UserService.deleteUser("2")
    val resInt = Await.result(res, wait)
    println(resInt)
    /*
    res.onComplete{
      case Success(s)=> res.foreach(println)
      case Failure(t) => t.printStackTrace()
    }
    */
  }

  def testUpdateUser(): Unit = {
    val wait : Duration = Duration.create(1000, "ms")
    //implicit val permit: CanAwait = Await
    val testUser: User = new User("c18e751d-8ba8-477f-9de2-4ec6a9809005", "Evgenii", "Lelyuk","31.07.1989", "Earth")
    val res: Future[Int] = UserService.updateUser("c18e751d-8ba8-477f-9de2-4ec6a9809005", testUser)
    val resInt = Await.result(res, wait)
    println(resInt)
    /*
    res.onComplete{
      case Success(s)=> res.foreach(println)
      case Failure(t) => t.printStackTrace()
    }
    */
  }


}
