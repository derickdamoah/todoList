package it.connectors

import connectors.MongoConnector
import org.bson.types.ObjectId
import org.mongodb.scala.Document
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers.stubControllerComponents
import play.api.test.Injecting
import repositories.MongoRepository

import scala.concurrent.ExecutionContext
class MongoConnectorISpec extends PlaySpec with GuiceOneAppPerSuite with Injecting with BeforeAndAfterAll {
  implicit val ec: ExecutionContext = stubControllerComponents().executionContext

  System.setProperty("config.resource", "test.conf")

  val testDocument =  Document("_id" -> new ObjectId("6514c6b657f23f751e245f6a"), "title" -> "test title", "description" -> "test description")
  val testUpdatedDocument =  Document("_id" -> new ObjectId("6514c6b657f23f751e245f6a"), "title" -> "updated title", "description" -> "updated description")
  val fakeDocument =  Document("_id" -> 1, "fake field" -> "fake value")
  lazy val testMongoRepository: MongoRepository = new MongoRepository()

  lazy val testMongoConnector: MongoConnector = new MongoConnector(stubControllerComponents(), testMongoRepository)

  override def afterAll(): Unit = {
    testMongoRepository.database.drop().toFuture().futureValue
  }

  "MongoConnector" should {
    "add the new item to the database" when {
      "createTask() is called" in {
        testMongoConnector.createTask(testDocument).futureValue.wasAcknowledged() mustBe true
      }
    }

    "throw an exception from the database" when {
      "createTask() is called" in {
        val exception = intercept[Exception] {
          testMongoConnector.createTask(fakeDocument).andThen(throw new Exception("an unexpected error occurred while creating a new item"))
        }
        exception.getMessage mustBe "an unexpected error occurred while creating a new item"
      }
    }

    "return all items" when {
      "getAllTasks() is called and no errors happen in the database" in {
        testMongoConnector.getAllTasks.futureValue.size mustBe 2
      }
    }

    "throw an exception from the database" when {
      "getAllTasks() is called" in {
        val exception = intercept[Exception] {
          testMongoConnector.getAllTasks.andThen(throw new Exception("an unexpected error occurred, could not retrieve all items"))
        }
        exception.getMessage mustBe "an unexpected error occurred, could not retrieve all items"
      }
    }

    "return the correct item" when {
      "getOneTask is called with the correct id" in {
        testMongoConnector.getOneTask(new ObjectId("6514c6b657f23f751e245f6a")).futureValue mustBe Some(testDocument)
      }
    }

    "return None" when {
      "getOneTask called with the wrong id" in {
        testMongoConnector.getOneTask(new ObjectId("123456789012345678901234")).futureValue mustBe None
      }
    }

    "throw an exception from the database" when {
      "getOneTask is called and there is a database error" in {
        val exception = intercept[Exception] {
          testMongoConnector.getOneTask(new ObjectId("123456789012345678901234")).andThen(throw new Exception("an unexpected error occurred could not retrieve item with id:"))
        }
        exception.getMessage mustBe "an unexpected error occurred could not retrieve item with id:"
      }
    }

    "return the updated item" when {
      "the updateOneTask method is called" in {
        testMongoConnector.updateOneTask(new ObjectId("6514c6b657f23f751e245f6a"), Document("$set" -> Document("title" -> "updated title", "description" -> "updated description"))).futureValue mustBe testUpdatedDocument
      }
    }

    "throw an exception from the database" when {
      "updateOneTask method is called and there is a database error" in {
        val exception = intercept[Exception] {
          testMongoConnector.updateOneTask(new ObjectId("6514c6b657f23f751e245f6a"), Document("$set" -> Document("title" -> "updated title", "description" -> "updated description"))).andThen(throw new Exception("an unexpected error occurred, could not update item"))
        }
        exception.getMessage mustBe "an unexpected error occurred, could not update item"
      }
    }

    "successfully delete the document" when {
      "deleteTask is called" in {
        testMongoConnector.deleteTask(new ObjectId("6514c6b657f23f751e245f6a")).futureValue.getDeletedCount mustBe 1
      }
    }

    "throw an exception from the database" when {
      "deleteTask is called and there is a database error" in {
        val exception = intercept[Exception] {
          testMongoConnector.deleteTask(new ObjectId("6514c6b657f23f751e245f6a")).andThen(throw new Exception("an unexpected error occurred, could not delete item"))
        }
        exception.getMessage mustBe "an unexpected error occurred, could not delete item"
      }
    }

  }


}


