package controllers

import dao.EntityDAO
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

class Application @Inject() (entityDao: EntityDAO) extends Controller {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import utils.Converters._

  def index = Action.async {
    entityDao.list.map(x => Ok(Json.toJson(x)))
  }

  def listNames = index

  def insertName(name: String) = Action.async {
    entityDao.insert(name)
      .map(x => Ok(Json.toJson(x)))
      .recover({case e:Throwable => BadRequest(e.getMessage)})
  }

  def updateName(id: Integer, name: String) = Action.async {
    entityDao.update(id, name)
      .map(x => Ok(Json.toJson(x)))
      .recover({case e:Throwable => BadRequest(e.getMessage)})
  }

  def deleteName(id: Integer) = Action.async {
    entityDao.delete(id)
      .map(x => Ok(Json.toJson(x)))
      .recover({case e:Throwable => BadRequest(e.getMessage)})
  }

}
