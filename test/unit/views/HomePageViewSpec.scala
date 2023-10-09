package unit.views

import controllers.routes
import models.TodoModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mongodb.scala.bson.BsonObjectId
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages
import play.api.mvc.{MessagesControllerComponents, Request}
import play.api.test.CSRFTokenHelper.CSRFRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout}
import play.api.test.{FakeRequest, Injecting}
import views.html.homePageView

class HomePageViewSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  val stubView: homePageView =  inject[homePageView]
  val todoModel: Seq[TodoModel] = Seq(TodoModel(Some(BsonObjectId("123456789123456789123456")), "test title", "test description"))
  implicit val request: Request[_] = FakeRequest().withCSRFToken
  val mcc: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]
  implicit val messages: Messages = mcc.messagesApi.preferred(request)
  val view: String = contentAsString(stubView.apply(todoModel))
  val doc: Document = Jsoup.parse(view)

  "the homepage view" should {

    "have the correct title" in  {
      doc.select("""title""").text() mustBe "Todo List"
    }

    "have the correct heading" in {
      doc.getElementsByTag("h1").text() mustBe "Todo List"
    }

    "have correct the item title" in {
      doc.getElementsByTag("summary").text() mustBe "test title"
    }

    "have the correct description" in {
      doc.select("""details>p""").text() mustBe "test description"
    }

    "have the correct buttons" in {
      doc.select(""".editButton>a""").text() mustBe "Edit"
      doc.select("""input[value="Delete"]""").attr("value") mustBe "Delete"
      doc.select(""".addItemButton>a""").text() mustBe "Add Item"
    }

    "have the correct delete form" in {
      doc.select("""form""").attr("action") mustBe routes.DeleteController.delete("123456789123456789123456").url
      doc.select("""form""").attr("method") mustBe "POST"
    }
  }
}
