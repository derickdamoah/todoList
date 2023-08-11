package models

import org.mongodb.scala.bson.BsonObjectId

case class TodoModel(_id: Option[BsonObjectId], title: String, description:String)

