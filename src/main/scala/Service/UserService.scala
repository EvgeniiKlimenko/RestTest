package Service
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlAction
import slick.jdbc.PostgresProfile._
import slick._
import scala.concurrent.Future
import slick.lifted.{Query, TableQuery}
import Model.{User, Users}
import Model.DBConnection
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global

import slick.basic.{BasicStreamingAction, BasicAction}
import slick.compiler.QueryCompiler
import slick.relational.{RelationalActionComponent, RelationalTableComponent, RelationalProfile}

import slick.dbio._
import slick.ast.{TableNode, Symbol, SymbolNamer, ColumnOption}
import slick.util.DumpInfo


object UserService extends TableQuery(new Users(_)) {
  var db = DBConnection.db
  val table = TableQuery[User]


  def getWithID(userId: Long): Future[Option[User]] = {
    val q = table.filter(_.id == userId)
    val action = q.result.headOption
    val ussr: Future[Option[User]] = db.run(action)
    return ussr
  }

  def deleteUser(userId: Long): Future[Int] = {
    //db.run(table.filter(_.id == userId).delete)
    val q = table.filter(_.id == userId)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    val sql = action.statements.head
    return affectedRowsCount
  }

  def getAll(limit: Int = 10, from: Int = 0): Future[Seq[User]] = {
    val q = for (c <- table) yield c
    val a = q.result
    val f: Future[Seq[User]] = db.run(a)
    return f;
  }


  def saveUser(usr: User) = db.run {
    table.map(c => (c.firstName, c.lastName, c.address)) += (usr.firstName, usr.lastName, usr.address)

  }

  def updateUser(id: Long, usr: User) = db.run {
    for {
      _ <- table.filter(_.id == id).update(usr.copy(id = usr.id, usr.firstName, usr.lastName, usr.address))
      res <- table.filter(_.id == id).result.headOption
    } yield res

  }

}



















/*
def getWithID(id: Long): Future[Option[User]] = db.run {
table.filter(_.id == id).result.headOption
}

def updateUser(usrId: Long, usr: User): Future[Option[User]] = db.run{
  table.filter(_.id == usrId).update(usr).map {
  case 0 => None
  case _ => Some(usr)
}
/*
val q = for { c <- table if c.id == usrId } yield c
val updateAction = q.update(usr)

for {
  //_ <- table.filter(_.id == usrId).update(usr.copy(id = usrId))
  //res <- table.filter(_.id == usrId).result.headOption
} yield res
*/
}

def saveUser(usr: User)= db.run{
//val insertUser: User = usr.copy()
table.map(c => (c.firstName, c.lastName, c.address)) += (usr.firstName, usr.lastName, usr.address)
/*
db.run {
  for {
    //_ <- table += insertUser
    _ <- table ++= usr
    res <- table.filter(_.id == insertUser.id).result.head
  } yield res
}
*/
}

def deleteUser(userId: Long) = {
//table.filter(_.id == id).result.delete
val q = table.filter(_.id == userId)
val action = q.
val affectedRowsCount: Future[Int] = db.run(action)
//val sql = action.statements.head
}


}
*/