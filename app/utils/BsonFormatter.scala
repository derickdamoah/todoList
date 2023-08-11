package utils

import org.mongodb.scala.bson.BsonObjectId
import play.api.data.FormError
import play.api.data.format.Formatter

object BsonFormatter {
  implicit def bsonFormatter: Formatter[BsonObjectId] = new Formatter[BsonObjectId] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BsonObjectId] = {
      data.get(key) match {
        case Some(value) =>
          try {
            Right(new BsonObjectId(new org.bson.types.ObjectId(value)))
          } catch {
            case _: IllegalArgumentException =>
              Left(Seq(FormError(key, "error.invalid", Nil)))
          }

        case None => Left(Seq(FormError(key, "error.required", Nil)))
      }
    }

    override def unbind(key: String, value: BsonObjectId): Map[String, String] = {
      Map(key -> value.getValue.toHexString)
    }
  }
}
