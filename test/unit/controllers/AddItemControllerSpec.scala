package unit.controllers

import akka.stream.Materializer
import controllers.{AddItemController, routes}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.CSRFTokenHelper.CSRFFRequestHeader
import play.api.test.Helpers.{GET, POST, contentAsString, contentType, defaultAwaitTimeout, redirectLocation, route, status, stubControllerComponents, writeableOf_AnyContentAsEmpty}
import play.api.test.{FakeRequest, Injecting}
import unit.testUtils.TestUtils
import views.html.addItem

import scala.concurrent.Future

class AddItemControllerSpec  extends PlaySpec with GuiceOneAppPerSuite with Injecting with MockitoSugar with TestUtils{
  val stubAddItemView: addItem = inject[addItem]
  implicit val materializer: Materializer = app.materializer


  "AddItemController.showAddItem()" should {

    "render the add item page from a new instance of the controller" in {
      val controller = new AddItemController(stubControllerComponents(),stubMongoService, stubAddItemView)
      val addItem = controller.showAddItem().apply(FakeRequest(GET, "/addItem").withCSRFToken)
      status(addItem) mustBe OK
      contentType(addItem) mustBe Some("text/html")
      contentAsString(addItem) must include("Add Item")
    }

    "render the add item page from the application" in {
      val controller = inject[AddItemController]
      val addItem = controller.showAddItem().apply(FakeRequest(GET, "/addItem").withCSRFToken)

      status(addItem) mustBe OK
      contentType(addItem) mustBe Some("text/html")
      contentAsString(addItem) must include ("Add Item")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/addItem")
      val addItem = route(app, request).get

      status(addItem) mustBe OK
      contentType(addItem) mustBe Some("text/html")
      println(addItem)
      contentAsString(addItem) must include ("Add Item")
    }
  }

  "AddItemController.addItem()" should {
    "redirect to the homepage when a valid form data is submitted" in {
      val testController = new AddItemController(stubControllerComponents(), stubMongoService, stubAddItemView)
      when(stubMongoService.createTask("test title", "test description")).thenReturn(Future.successful(true))
      val result = testController.addItem().apply(
        FakeRequest(POST, "/addItem")
        .withFormUrlEncodedBody( "title" -> "test title 6", "description" -> "test description")
      )

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.HomeController.home().url)

    }

    "re-render the add item page when an invalid form data is submitted" in {

      val testController = new AddItemController(stubControllerComponents(), stubMongoService, stubAddItemView)
      when(stubMongoService.createTask("test title", "")).thenReturn(Future.successful(false))
      val result = testController.addItem().apply(
        FakeRequest(POST, "/addItem")
        .withFormUrlEncodedBody( "title" -> "", "description" -> "")
          .withCSRFToken
      )

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Add Item")
    }
  }
}
