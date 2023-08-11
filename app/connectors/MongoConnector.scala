package connectors

import org.bson.types._
import org.mongodb.scala.Document
import org.mongodb.scala.model.{Filters, FindOneAndUpdateOptions, ReturnDocument}
import org.mongodb.scala.result.DeleteResult
import repositories.MongoRepository

import javax.inject.Inject
import scala.concurrent.Future

class MongoConnector @Inject()(mongoRepository: MongoRepository) {

  def createTask(document: Document): Future[_] = {
    mongoRepository.collection.insertOne(document).toFuture()
  }

  def getAllTasks: Future[Seq[Document]] = {
    mongoRepository.collection.find().toFuture()
  }

  def getOneTask(id: ObjectId): Future[Option[Document]] = {
    mongoRepository.collection.find(Filters.equal("_id", id)).headOption()
  }

  def updateOneTask(id: ObjectId, document: Document): Future[Document] = {
    mongoRepository.collection.findOneAndUpdate(Filters.equal("_id", id), document, FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)).toFuture()
  }

  def deleteTask(id: ObjectId): Future[DeleteResult] = {
    mongoRepository.collection.deleteOne(Filters.equal("_id", id)).toFuture()
  }
}
