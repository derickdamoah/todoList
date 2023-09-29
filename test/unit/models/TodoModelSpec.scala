package unit.models

import models.TodoModel
import org.mongodb.scala.bson.BsonObjectId
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class TodoModelSpec extends AnyWordSpecLike with Matchers{

  val testID: BsonObjectId = BsonObjectId("000000000000000000000000")

  val testModel: TodoModel = TodoModel.apply(_id = Some(testID), title = "test title", description = "test description")

  val testItem: Option[(Option[BsonObjectId], String, String)] = TodoModel.unapply(testModel)


  "The TodoModel" should{

    "return the correct id" in {
      testModel._id mustBe Some(testID)
    }

    "return the correct title" in {
      testModel.title mustBe "test title"
    }

    "return the correct description" in {
      testModel.description mustBe "test description"
    }
  }

  "The Form" should{
    "return the correct id" in {
      testItem.get._1 mustBe Some(testID)
    }

    "return the correct title" in {
      testItem.get._2 mustBe "test title"
    }

    "return the correct description" in {
      testItem.get._3 mustBe "test description"
    }

  }

}
