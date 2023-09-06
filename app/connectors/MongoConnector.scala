package connectors

import org.bson.types._
import org.mongodb.scala.Document
import org.mongodb.scala.model.{Filters, FindOneAndUpdateOptions, ReturnDocument}
import org.mongodb.scala.result.{DeleteResult, InsertOneResult}
import play.api.mvc.ControllerComponents
import repositories.MongoRepository
import utils.LoggerUtil

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MongoConnector @Inject()
(val controllerComponents: ControllerComponents,
mongoRepository: MongoRepository) extends LoggerUtil{

  implicit val ec: ExecutionContext = controllerComponents.executionContext
  def createTask(document: Document): Future[InsertOneResult] = {
    mongoRepository.collection.insertOne(document).toFuture().recover{
      case exception: Exception =>
        logger.error(s"[MongoConnector][createTask] - an unexpected error occurred while creating a new item: ${exception.getMessage}")
        throw new Exception(s"an unexpected error occurred while creating a new item: ${exception.getMessage}")
    }
  }

  def getAllTasks: Future[Seq[Document]] = {
    mongoRepository.collection.find().toFuture().recover{
      case exception: Exception =>
        logger.error(s"[MongoConnector][getAllTasks] - an unexpected error occurred: ${exception.getMessage}")
        throw new Exception(s"an unexpected error occurred: ${exception.getMessage}")
    }
  }

  def getOneTask(id: ObjectId): Future[Option[Document]] = {
    mongoRepository.collection.find(Filters.equal("_id", id)).headOption().recover{
      case e: Exception =>
        logger.error(s"[MongoConnector][getOneTask] - could not retrieve item: ${e.getMessage}")
        throw new Exception(s"could not retrieve item with id: $id ${e.getMessage}")
    }
  }

  def updateOneTask(id: ObjectId, document: Document): Future[Document] = {
    mongoRepository.collection.findOneAndUpdate(
      Filters.equal("_id", id), document, FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
    ).toFuture().recover{
      case e: Exception =>
        logger.error(s"[MongoConnector][updateOneTask] - could not update item: ${e.getMessage}")
        throw new Exception(s"could not update item: ${e.getMessage}")
    }
  }

  def deleteTask(id: ObjectId): Future[DeleteResult] = {
    mongoRepository.collection.deleteOne(Filters.equal("_id", id)).toFuture().recover{
      case e: Exception =>
        logger.error(s"[MongoConnector][deleteTask] - could not delete item: ${e.getMessage}")
        throw new Exception(s"could not delete item: ${e.getMessage}")
    }
  }
}
