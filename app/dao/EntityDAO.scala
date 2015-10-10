package dao

import javax.inject.Inject

import models.Entity
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import scala.concurrent.Future

class EntityDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._
  
  private class EntitiesTable(tag: Tag) extends Table[Entity](tag, "ENTITY") {
    def id = column[Int]("ID", O.AutoInc, O.PrimaryKey)
    def name = column[String]("NAME")

    def * = (id, name) <> (Entity.tupled, Entity.unapply _)
  }

  private val Entities = TableQuery[EntitiesTable]

  def list: Future[Seq[Entity]] = db.run(Entities.result)

  def insert(name: String): Future[Int] = {
    val id = (Entities returning Entities.map(_.id)) += Entity(0, name)
//    db.run(Entities += Entity(0, name))
    db.run(id)
  }

  def update(id: Int, name: String): Future[Int] = {
    val e = Entities.filter(_.id === id).map(_.name)
    db.run(e.update(name))
  }

  def delete(id: Int): Future[Int] = {
    val e = Entities.filter(_.id === id)
    db.run(e.delete)
  }
}
