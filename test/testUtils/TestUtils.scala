package testUtils

import models.TodoModel
import org.mongodb.scala.bson.BsonObjectId
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Configuration
import services.MongoService

trait TestUtils{

  lazy val stubMongoService: MongoService = mock[MongoService]
  val mockConfiguration: Configuration = mock[Configuration]
  val testID: BsonObjectId = BsonObjectId("000000000000000000000000")
  val testMockResults: Seq[TodoModel] = Seq(
    TodoModel.apply(_id = Some(testID), title = "test title1", description = "test description1"),
    TodoModel.apply(_id = Some(testID), title = "test title2", description = "test description2")
  )

  val testSingleResult: TodoModel = TodoModel.apply(_id = Some(testID), title = "test title", description = "test description")


}
