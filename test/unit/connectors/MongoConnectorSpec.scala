package unit.connectors

import connectors.MongoConnector
import org.bson.types.ObjectId
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.mongodb.scala.result.{DeleteResult, InsertOneResult}
import org.mongodb.scala.{Document, FindObservable, MongoCollection, SingleObservable}
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers.stubControllerComponents
import play.api.test.Injecting
import repositories.MongoRepository
import unit.testUtils.TestUtils

import scala.concurrent.{ExecutionContext, Future}

class MongoConnectorSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting with TestUtils with BeforeAndAfter{

  implicit val ec: ExecutionContext = stubControllerComponents().executionContext

  //mocks
  lazy val mockRepository: MongoRepository = mock[MongoRepository]
  lazy val mockInsertOneResult: InsertOneResult = mock[InsertOneResult]
  lazy val mockSingleObservable: SingleObservable[InsertOneResult] = mock[SingleObservable[InsertOneResult]]
  lazy val mockCollection: MongoCollection[Document] = mock[MongoCollection[Document]]
  lazy val testMockedConnector = new MongoConnector(stubControllerComponents(), mockRepository)
  lazy val mockFindObservable: FindObservable[Document] = mock[FindObservable[Document]]
  lazy val mockConnector: MongoConnector = mock[MongoConnector]
  val testDocument = Document("title" -> "test title", "description" -> "test description")
  val testId = new ObjectId("123456789012345678901234")

  "calling createTask()" should{
    "successfully create a new item in the database when a there is a valid document" in {

      when(mockRepository.collection).thenReturn(mockCollection)
      when(mockCollection.insertOne(any[Document])).thenReturn(mockSingleObservable)
      when(mockSingleObservable.toFuture()).thenReturn(Future.successful(mockInsertOneResult))

      val result = testMockedConnector.createTask(testDocument)

      result.map { res =>
        res.wasAcknowledged() mustBe true
      }

      verify(mockRepository.collection).insertOne(testDocument)
    }

    "fail to create a new item in the database when a there is an invalid document" in {

      when(mockRepository.collection).thenReturn(mockCollection)
      when(mockCollection.insertOne(any[Document])).thenReturn(mockSingleObservable)
      when(mockSingleObservable.toFuture()).thenReturn(Future.failed(new Exception("could not create item")))

      val exception = intercept[Exception]{
        testMockedConnector.createTask(testDocument).futureValue
      }

      exception.getMessage must include("an unexpected error occurred while creating a new item: could not create item")

    }
  }

  "calling getAllTasks()" should {

    "return a list of documents when successful" in {
      val mockConnector = mock[MongoConnector]

      when(mockConnector.getAllTasks).thenReturn(Future.successful(Seq(testDocument)))

      val result = mockConnector.getAllTasks

      result.futureValue.size mustBe 1
      result.futureValue mustBe Seq(testDocument)
    }

  }

  "calling getOneTask(id)" should{
    "return a document when the connector call is successful" in {
      when(mockConnector.getOneTask(testId)).thenReturn(Future.successful(Some(testDocument)))

      val result = mockConnector.getOneTask(testId)

      result.futureValue mustBe Some(testDocument)
    }

    "return a None when there is no item associated with the id" in {
      when(mockConnector.getOneTask(testId)).thenReturn(Future.successful(None))

      val result = mockConnector.getOneTask(testId)

      result.futureValue mustBe None
    }
  }

  "calling updateOneTask(id)" should{
    "return an updated document" in {
      when(mockConnector.updateOneTask(testId, testDocument)).thenReturn(Future.successful(testDocument))

      val result = mockConnector.updateOneTask(testId, testDocument)

      result.futureValue mustBe testDocument
    }

  }

  "calling deleteTask(id)" should{
    "delete the document" in {
      val mockDeleteResult = mock[DeleteResult]
      when(mockConnector.deleteTask(testId)).thenReturn(Future.successful(mockDeleteResult))

      val result = mockConnector.deleteTask(testId)

      result.futureValue mustBe mockDeleteResult
    }

  }

}
