package services

import models.TodoModel
import org.mongodb.scala._
import play.api.mvc.ControllerComponents

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ConvertModels @Inject()(controllerComponents: ControllerComponents, mongoService: MongoService) {
  implicit val ec: ExecutionContext = controllerComponents.executionContext
  def convertToModel(document: Document): TodoModel = {
    TodoModel(Some(document.get("_id").get.asObjectId()),  document.getString("title"), document.getString("description"))
  }

//  def getAllTasks: Future[Seq[TodoModel]] = {
//    mongoService.getAllTasks.map { documents =>documents.map(convertToModel) }
//  }
//
//  def getOneTask(id: String): Future[Option[TodoModel]] = {
//    mongoService.getOneTask(id).map(document => document.map(convertToModel))
//  }

}
