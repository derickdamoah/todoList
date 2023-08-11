package forms

import forms.TodoForm.todoForm
import models.TodoModel
import org.mongodb.scala.bson.BsonObjectId
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class TodoFormSpec extends AnyWordSpecLike with Matchers{

  val testId: BsonObjectId = BsonObjectId("000000000000000000000000")

  "The TodoForm" should {

    "validate correctly with valid data" in {
      val validData = Map("_id" -> "000000000000000000000000", "title" -> "test title", "description" -> "test description")
      val formResult = todoForm.bind(data = validData)
      formResult.hasErrors mustBe false
    }

    "fail validation and return errors" when {

      "the title and description fields are empty" in {

        val validData = Map("title" -> "", "description" -> "")
        val formResult = todoForm.bind(data = validData)

        formResult.hasErrors mustBe true

        formResult("title").error.fold(fail("was expecting an error")){ error =>
          error.message mustBe "The title cannot be empty"
        }

        formResult("description").error.fold(fail("was expecting an error")){ error =>
          error.message mustBe "The description cannot be empty"
        }
      }

      "the title and description fields are omitted" in {

        val validData = Map("_id" -> "test id")
        val formResult = todoForm.bind(data = validData)

        formResult.hasErrors mustBe true

        formResult("title").error.fold(fail("was expecting an error")){ error =>
          error.message mustBe "error.required"
        }

        formResult("description").error.fold(fail("was expecting an error")){ error =>
          error.message mustBe "error.required"
        }
      }

    }

    "fill data correctly" in {
      val validData = Map("_id" -> "000000000000000000000000", "title" -> "test title", "description" -> "test description")
      val testModel: TodoModel = TodoModel.apply(_id = Some(testId), title = "test title", description = "test description")
      val formResult = todoForm.fillAndValidate(testModel)
      formResult.data mustBe validData
    }

  }

}
