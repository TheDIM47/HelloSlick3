package utils

import models.Entity
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath, Reads, Writes}

object Converters {

  implicit val entityWrites: Writes[Entity] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "name").write[String]
  ) (unlift(Entity.unapply))

  implicit val entityReads: Reads[Entity] = (
    (JsPath \ "id").read[Int] and
    (JsPath \ "name").read[String]
  )(Entity.apply _)

  implicit val entityFormat: Format[Entity] = Format(entityReads, entityWrites)
}
