package unit.views

import controllers.routes
import forms.TodoForm.todoForm
import models.TodoModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.{MessagesControllerComponents, Request}
import play.api.test.CSRFTokenHelper.CSRFRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout}
import play.api.test.{FakeRequest, Injecting}
import views.html.addItem

class AddItemPageViewSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  val stubView: addItem = inject[addItem]
  val validData: Map[String, String] = Map("_id" -> "000000000000000000000000", "title" -> "test title", "description" -> "test description")
  val formResult: Form[TodoModel] = todoForm.bind(data = validData)

  implicit val request: Request[_] = FakeRequest().withCSRFToken
  val mcc: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]
  implicit val messages: Messages = mcc.messagesApi.preferred(request)
  val view: String = contentAsString(stubView.apply(formResult))
  val doc: Document = Jsoup.parse(view)

  "the homepage view" should {

    "have the correct title" in  {
      doc.select("""title""").text() mustBe "Add Item Page"
    }

    "have the correct heading" in {
      doc.getElementsByTag("h1").text() mustBe "Add Item"
    }

    "have a back button" in {
      doc.select(""".backButton>a""").text() mustBe "Back"
      doc.select(""".backButton>a""").attr("href") mustBe routes.HomeController.home().url
    }

    "have the correct form" in {
      doc.select("""form""").attr("method") mustBe "POST"
      doc.select("""form""").attr("action") mustBe routes.AddItemController.addItem().url
    }

    "have the correct labels" in {
      doc.select("""label[for="title"]""").text() mustBe "title"
      doc.select("""label[for="description"]""").text() mustBe "description"
    }

    "have the correct text inputs" in {
      doc.select("""input[id="title"]""").attr("name") mustBe "title"
      doc.select("""input[id="description"]""").attr("name") mustBe "description"
    }

    "have the correct submit button" in {
      doc.select("""input[type="submit"]""").attr("value") mustBe "Add Item"
    }

    "show the correct error messages" when {

      "the title and description field are missing" in {
        val inValid: Map[String, String] = Map("_id" -> "000000000000000000000000")
        val formResult: Form[TodoModel] = todoForm.bind(data = inValid)
        val view: String = contentAsString(stubView.apply(formResult))
        val doc: Document = Jsoup.parse(view)

        doc.select("""#title_field>.error""").text() mustBe "This field is required"
        doc.select("""#description_field>.error""").text() mustBe "This field is required"
      }

      "the title and description field are left empty" in {
        val inValid: Map[String, String] = Map("_id" -> "000000000000000000000000", "title" -> "", "description" -> "")
        val formResult: Form[TodoModel] = todoForm.bind(data = inValid)
        val view: String = contentAsString(stubView.apply(formResult))
        val doc: Document = Jsoup.parse(view)

        doc.select("""#title_field>.error""").text() mustBe "The title cannot be empty"
        doc.select("""#description_field>.error""").text() mustBe "The description cannot be empty"
      }
    }
  }
}
