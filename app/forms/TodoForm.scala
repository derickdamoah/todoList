package forms

import models.TodoModel
import org.mongodb.scala.bson.BsonObjectId
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formats._
import utils.BsonFormatter.bsonFormatter

object TodoForm {
  private val bsonObjectIDFormat: Mapping[BsonObjectId] = of[BsonObjectId]

  val todoForm: Form[TodoModel] = Form(
    mapping(
      "_id" -> optional(bsonObjectIDFormat),
      "title" -> text.verifying("The title cannot be empty", value => value.trim.nonEmpty),
      "description" -> text.verifying("The description cannot be empty", value => value.trim.nonEmpty)
    )(TodoModel.apply)(TodoModel.unapply)
  )
}