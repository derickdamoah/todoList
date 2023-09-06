package services

import connectors.MongoConnector
import models.TodoModel
import org.bson.types._
import org.mongodb.scala.Document
import play.api.mvc.ControllerComponents
import utils.LoggerUtil

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MongoService @Inject()
(controllerComponents: ControllerComponents,
 mongoConnector: MongoConnector) extends LoggerUtil {
  implicit val ec: ExecutionContext = controllerComponents.executionContext

  private def convertToModel(document: Document): TodoModel = {
    TodoModel(Some(document.get("_id").get.asObjectId()),  document.getString("title"), document.getString("description"))
  }

  private def convertId(id: String): ObjectId = {
    try {
      new ObjectId(id)
    } catch {
      case exception: Exception =>
        logger.error(s"[MongoService][convertId] - could not convert id to an ObjectID: ${exception.getMessage}")
        throw new Exception(s"could not convert id to an ObjectID: ${exception.getMessage}")
    }
  }

  def createTask(title: String, description: String): Future[Boolean] = {
    val document: Document = Document("title" -> title, "description" -> description)
    mongoConnector.createTask(document).map{
      case createResult if createResult.wasAcknowledged() => true
      case _ => false
    }.recover {
      case exception: Exception =>
        logger.error(s"[MongoService][createTask] - an unexpected error occurred while creating a new item: ${exception.getMessage}")
        throw new Exception(s"an unexpected error occurred while creating a new item: ${exception.getMessage}")
    }
  }

  def getAllTasks: Future[Seq[TodoModel]] = {
    mongoConnector.getAllTasks.map{
      documents =>documents.map(convertToModel)
    }.recover{
      case exception: Exception =>
        logger.error(s"[MongoService][getAllItems] - An error occurred while retrieving items: ${exception.getMessage}")
        throw new Exception(s"An error occurred while retrieving items: ${exception.getMessage}")
    }
  }

  def getOneTask(id: String): Future[Option[TodoModel]] = {
    val objectId = convertId(id)
    mongoConnector.getOneTask(objectId).map(document => document.map(convertToModel)).recover{
      case exception: Exception =>
        logger.error(s"[MongoService][getOneTask] - An error occurred while retrieving item: ${exception.getMessage}")
        throw new Exception(s"An error occurred while retrieving item: ${exception.getMessage}")
    }
  }

  def updateOneTask(id: String, title: String, description: String): Future[Document] = {
    val objectId = convertId(id)
    val document: Document = Document("$set" -> Document("title" -> title, "description" -> description))
    mongoConnector.updateOneTask(objectId, document).recover{
      case exception: Exception =>
        logger.error(s"[MongoService][updateOneTask] - could not update item with id: $id; ${exception.getMessage}")
        throw new Exception(s"Could not update item with id: $id; ${exception.getMessage}")
    }
  }

  def deleteTask(id: String): Future[Boolean] = {
    val objectId = convertId(id)
    mongoConnector.deleteTask(objectId).map{
      case deleteResult if deleteResult.wasAcknowledged() && deleteResult.getDeletedCount > 0 =>
        logger.info(s"[MongoService][deleteTask] - successfully deleted item with id: $id")
        true
      case _ =>
        logger.error(s"[MongoService][deleteTask] - Something went wrong: could not delete item with id: $id")
        false
    }
  }
}
