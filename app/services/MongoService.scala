package services

import connectors.MongoConnector
import models.TodoModel
import org.bson.types._
import org.mongodb.scala.Document
import org.mongodb.scala.result.DeleteResult
import play.api.mvc.ControllerComponents

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MongoService @Inject()(controllerComponents: ControllerComponents, mongoConnector: MongoConnector) {
  implicit val ec: ExecutionContext = controllerComponents.executionContext

  def convertToModel(document: Document): TodoModel = {
    TodoModel(Some(document.get("_id").get.asObjectId()),  document.getString("title"), document.getString("description"))
  }

  def createTask(title: String, description: String): Future[_] = {
    val document: Document = Document("title" -> title, "description" -> description)
    mongoConnector.createTask(document)
  }

  def getAllTasks: Future[Seq[TodoModel]] = {
    mongoConnector.getAllTasks.map { documents =>documents.map(convertToModel) }
  }

  def getOneTask(id: String): Future[Option[TodoModel]] = {
    val objectId = new ObjectId(id)
    mongoConnector.getOneTask(objectId).map(document => document.map(convertToModel))
  }

  def updateOneTask(id: String, title: String, description: String): Future[Document] = {
    val objectId = new ObjectId(id)
    val document: Document = Document("$set" -> Document("title" -> title, "description" -> description))
    mongoConnector.updateOneTask(objectId, document)
  }

  def deleteTask(id: String): Future[DeleteResult] = {
    val objectId = new ObjectId(id)
    mongoConnector.deleteTask(objectId)
  }
}
