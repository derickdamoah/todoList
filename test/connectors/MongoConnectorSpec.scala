package connectors

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.result.InsertOneResult
import org.mongodb.scala.{Document, FindObservable, MongoCollection, SingleObservable}
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.test.Helpers.stubControllerComponents
import play.api.test.Injecting
import repositories.MongoRepository
import testUtils.TestUtils

import scala.concurrent.{ExecutionContext, Future}

class MongoConnectorSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting with TestUtils with BeforeAndAfter{

  implicit val ec: ExecutionContext = stubControllerComponents().executionContext

  //stubs
  lazy val testConfiguration: Configuration = app.injector.instanceOf[Configuration]
  lazy val testMongoRepository: MongoRepository = new MongoRepository(stubControllerComponents(), testConfiguration)
  lazy val testStubbedConnector: MongoConnector = new MongoConnector(stubControllerComponents(), testMongoRepository)

  //mocks
  lazy val mockRepository: MongoRepository = mock[MongoRepository]
  lazy val mockInsertOneResult: InsertOneResult = mock[InsertOneResult]
  lazy val mockSingleObservable: SingleObservable[InsertOneResult] = mock[SingleObservable[InsertOneResult]]
  lazy val mockFindObservable: FindObservable[Document] = mock[FindObservable[Document]]
  lazy val mockCollection: MongoCollection[Document] = mock[MongoCollection[Document]]
  lazy val testMockedConnector = new MongoConnector(stubControllerComponents(), mockRepository)

  val testDocument = Document("title" -> "test title7", "description" -> "test description7")
  val testBsonDocument: BsonDocument = BsonDocument(testDocument)

  "calling createTask()" should{
    "successfully create a new item in the database when a there is a valid document" in {

      when(mockRepository.collection).thenReturn(mockCollection)
      when(mockCollection.insertOne(any[Document])).thenReturn(mockSingleObservable)
      when(mockSingleObservable.toFuture()).thenReturn(Future.successful(mockInsertOneResult))

      val result = testMockedConnector.createTask(Document("title" -> "test title", "description" -> "test description"))

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

    "return a list of documents when successful with Mocks" in {

      when(mockRepository.collection).thenReturn(mockCollection)
      when(mockCollection.insertOne(any[Document])).thenReturn(mockSingleObservable)
      when(mockSingleObservable.toFuture()).thenReturn(Future.successful(mockInsertOneResult))

      testMockedConnector.createTask(Document("title" -> "test title", "description" -> "test description"))

      when(mockRepository.collection).thenReturn(mockCollection)
      when(mockCollection.find()).thenReturn(mockFindObservable)
      when(mockFindObservable.toFuture()).thenReturn(Future.successful(Seq(Document("title" -> "test title", "description" -> "test description"))))


      val result  = testMockedConnector.getAllTasks

      result.futureValue.size mustBe 1
      result.futureValue mustBe Seq(Document("title" -> "test title", "description" -> "test description"))
    }

    "return a list of documents when successful with stubs" in {

      val result  = testStubbedConnector.getAllTasks

      //this is getting the documents from the actual database
      result.futureValue.size mustBe 3
    }

    "throw an exception when there is an error" in {

      //this doesnt work
      val exception = intercept[Exception] {
        testStubbedConnector.getAllTasks.failed.futureValue
      }

      exception.getMessage must contain("an unexpected error occurred:")
    }
  }

}
