package views

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
import views.html.genericErrorView

class GenericErrorViewSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  val stubView: genericErrorView =  inject[genericErrorView]
  val todoModel: Seq[TodoModel] = Seq(TodoModel(Some(BsonObjectId("123456789123456789123456")), "test title", "test description"))
  implicit val request: Request[_] = FakeRequest().withCSRFToken
  val mcc: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]
  implicit val messages: Messages = mcc.messagesApi.preferred(request)
  val errorString = "Some unexpected error occurred"
  val view: String = contentAsString(stubView.apply(errorString))
  val doc: Document = Jsoup.parse(view)

  "the homepage view" should {

    "have the correct title" in  {
      doc.select("""title""").text() mustBe "Error"
    }

    "have the correct heading" in {
      doc.getElementsByTag("h1").text() mustBe errorString
    }

    "have a back button" in {
      doc.select(""".backButton>a""").text() mustBe "Back"
      doc.select(""".backButton>a""").attr("href") mustBe routes.HomeController.home().url
    }
  }
}
