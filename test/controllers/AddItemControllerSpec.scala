package controllers

import akka.stream.Materializer
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.OK
import play.api.test.CSRFTokenHelper.CSRFFRequestHeader
import play.api.test.Helpers.{GET, contentAsString, contentType, defaultAwaitTimeout, route, status, stubControllerComponents, writeableOf_AnyContentAsEmpty}
import play.api.test.{FakeRequest, Injecting}
import services.MongoService
import views.html.addItem

class AddItemControllerSpec  extends PlaySpec with GuiceOneAppPerSuite with Injecting with MockitoSugar{
  val stubMongoService: MongoService = mock[MongoService]
  val stubAddItemView: addItem = inject[addItem]
  implicit val materializer: Materializer = app.materializer


  // what doe MAterializer do>
//  what does the GuiceFakeApplication factory do and how does it differ from GuiceOneAppPerSuite GuiceOneAppPerTest?

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
      val addItem = controller.showAddItem().apply(FakeRequest(GET, "/addItem"))

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
}
