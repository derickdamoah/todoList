package services

import connectors.MongoConnector
import models.TodoModel
import org.bson.types.ObjectId
import org.mockito.Mockito.when
import org.mongodb.scala.Document
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.result.{DeleteResult, InsertOneResult}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers.stubControllerComponents
import play.api.test.Injecting

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class MongoServiceSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting{

  implicit val ec: ExecutionContext = stubControllerComponents().executionContext

  val mockMongoConnector: MongoConnector = mock[MongoConnector]
  val testMongoService = new MongoService(stubControllerComponents(), mockMongoConnector)
  val mockInsertOneResult: InsertOneResult = mock[InsertOneResult]

  val testDocument: Document = Document("_id" -> new ObjectId("123456789012345678901234") , "title" -> "test title", "description" -> "test description")
  val testDocument2: Document = Document("title" -> "test title", "description" -> "test description")
  val testTodoModel: TodoModel = TodoModel(Some(BsonObjectId("123456789012345678901234")), "test title", "test description")

  "The createTask() method" should {
    "successfully create a new task and return true" in {
      when(mockMongoConnector.createTask(testDocument2))
        .thenReturn(Future.successful(mockInsertOneResult))

      when(mockInsertOneResult.wasAcknowledged()).thenReturn(true)
      val result = testMongoService.createTask("test title", "test description")
      result.onComplete{ value =>
        value mustBe Success(true)
      }
    }

    "fail to create a new task and return false" in {
      when(mockMongoConnector.createTask(testDocument2))
        .thenReturn(Future.successful(mockInsertOneResult))

      when(mockInsertOneResult.wasAcknowledged()).thenReturn(false)
      val result = testMongoService.createTask("test title", "test description")
      result.onComplete{ value =>
        value mustBe Success(false)
      }
    }

    "throw an exception when it fails to create a new task" in {

      when(mockMongoConnector.createTask(testDocument2))
        .thenReturn(Future.failed(new Exception("")))

      val exception = intercept[Exception]{
        testMongoService.createTask("test title", "test description").futureValue
      }

      exception.getMessage must include("an unexpected error occurred while creating a new item:")
    }
  }

  "The getAllTasks() method" should {
    "successfully return a sequence of TodoModel" in{
      when(mockMongoConnector.getAllTasks).thenReturn(Future.successful(Seq(testDocument)))
      val result = testMongoService.getAllTasks
      result.futureValue mustBe Seq(testTodoModel)
    }

    "return and exception" in{
      when(mockMongoConnector.getAllTasks).thenReturn(Future.failed(new Exception("")))
      val exception = intercept[Exception]{
        testMongoService.getAllTasks.futureValue
      }
      exception.getMessage must include("An error occurred while retrieving items:")
    }
  }

  "The getOneTask() method" should{
    "successfully return and option TodoModel" in {
      when(mockMongoConnector.getOneTask(new ObjectId("123456789012345678901234"))).thenReturn(Future.successful(Some(testDocument)))
      val result = testMongoService.getOneTask("123456789012345678901234")
      result.futureValue mustBe Some(testTodoModel)
    }

    "successfully an exception" in {
      when(mockMongoConnector.getOneTask(new ObjectId("123456789012345678901234"))).thenReturn(Future.failed(new Exception("")))
      val exception = intercept[Exception] {
        testMongoService.getOneTask("123456789012345678901234").futureValue
      }
      exception.getMessage must include("An error occurred while retrieving item:")
    }
  }

  "The updateOneTask() method" should {
    "successfully update the item" in {
      val id =new ObjectId("123456789012345678901234")
      when(mockMongoConnector.updateOneTask(id,
        Document("$set" -> Document("title" -> "test title2", "description" -> "test description2")))
      ).thenReturn(Future.successful(testDocument))

      val result = testMongoService.updateOneTask("123456789012345678901234", "test title2", "test description2")

      result.futureValue mustBe testDocument
    }

    "return an exception" in {
      val id =new ObjectId("123456789012345678901234")
      when(mockMongoConnector.updateOneTask(id,
        Document("$set" -> Document("title" -> "test title2", "description" -> "test description2")))
      ).thenReturn(Future.failed(new Exception("")))

      val exception = intercept[Exception] {
        testMongoService.updateOneTask("123456789012345678901234", "test title2", "test description2").futureValue
      }

      exception.getMessage must include(s"Could not update item with id: 123456789012345678901234;")
    }
  }

  "The deleteTask() method" should {
    "successfully delete the item" in {
      val deleteResult = mock[DeleteResult]
      when(mockMongoConnector.deleteTask(new ObjectId("123456789012345678901234")))
        .thenReturn(Future.successful(deleteResult))
      when(deleteResult.wasAcknowledged()).thenReturn(true)
      when(deleteResult.getDeletedCount).thenReturn(1)
      val result = testMongoService.deleteTask("123456789012345678901234").futureValue
      result mustBe true
    }

    "fail to delete the item" in {
      val deleteResult = mock[DeleteResult]
      when(mockMongoConnector.deleteTask(new ObjectId("123456789012345678901234")))
        .thenReturn(Future.successful(deleteResult))
      when(deleteResult.wasAcknowledged()).thenReturn(false)
      when(deleteResult.getDeletedCount).thenReturn(0)
      val result = testMongoService.deleteTask("123456789012345678901234").futureValue
      result mustBe false
    }
  }

}
