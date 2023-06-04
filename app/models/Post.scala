package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json._

case class Post(
    _id: Option[BSONObjectID],
    title: String,
    description: String
)

object Post {
    implicit val format: OFormat[Post] = Json.format[Post]
}