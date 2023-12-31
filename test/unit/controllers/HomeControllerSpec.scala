package unit.controllers

import akka.stream.Materializer
import controllers.HomeController
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import play.api.test._
import unit.testUtils.TestUtils
import views.html.homePageView

import scala.concurrent.Future

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting with MockitoSugar with TestUtils{

  val stubHomepageView: homePageView = inject[homePageView]

  implicit val materializer: Materializer = app.materializer

  when(stubMongoService.getAllTasks).thenReturn(Future.successful(testMockResults))

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents(), stubMongoService, stubHomepageView)
      val home = controller.home().apply(FakeRequest(GET, "/").withCSRFToken)

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Todo List")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.home().apply(FakeRequest(GET, "/").withCSRFToken)

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Todo List")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Todo List")
    }
  }
}
