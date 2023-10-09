package unit.controllers

import controllers.{EditItemController, routes}
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.http.Status.OK
import play.api.test.CSRFTokenHelper.CSRFRequest
import play.api.test.Helpers.{GET, POST, contentAsString, defaultAwaitTimeout, redirectLocation, status, stubControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import unit.testUtils.TestUtils
import views.html.{editPageView, genericErrorView}

import scala.concurrent.Future

class EditItemControllerSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting with TestUtils{

  val stubErrorPageView: genericErrorView = inject[genericErrorView]
  val stubEditPageView: editPageView = inject[editPageView]

  val testController = new EditItemController(stubControllerComponents(), stubMongoService, stubEditPageView, stubErrorPageView)

  "EditItemController.showEdit()" should {
    "correctly render the edit item page when called with a valid ID" in {

      when(stubMongoService.getOneTask("testValidID")).thenReturn(Future.successful(Some(testSingleResult)))
      val result = testController.showEdit("testValidID").apply(FakeRequest(GET, "/editItem/testValidID").withCSRFToken)

      status(result) mustBe OK
    }

    "correctly render the error page when called with an invalid ID" in {

      when(stubMongoService.getOneTask("testInvalidID")).thenReturn(Future.successful(None))
      val result = testController.showEdit("testInvalidID").apply(FakeRequest(GET, "/editItem/testInvalidID").withCSRFToken)

      status(result) mustBe Status.BAD_REQUEST
    }
  }

  "EditItemController.postEdit()" should {
    "successfully update the item" in {
      when(stubMongoService.getOneTask("testValidID")).thenReturn(Future.successful(Some(testSingleResult)))
      val result = testController.postEdit("testValidID").apply(
        FakeRequest(POST, "/editItem/testValidID")
        .withFormUrlEncodedBody("title" -> "test title", "description" -> "test description")
        .withCSRFToken
      )

      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(routes.HomeController.home().url)
    }

    "return a form validation error when validation errors occur" in {
      when(stubMongoService.getOneTask("testValidID")).thenReturn(Future.successful(Some(testSingleResult)))
      val result = testController.postEdit("testValidID").apply(
        FakeRequest(POST, "/editItem/testValidID")
        .withFormUrlEncodedBody("title" -> "", "description" -> "")
        .withCSRFToken
      )

      status(result) mustBe Status.BAD_REQUEST
      contentAsString(result) must include ("Edit Item")
    }

    "throw an exception when the id is invalid" in {
      when(stubMongoService.getOneTask("testValidID")).thenReturn(Future.failed(new Exception("something went wrong")))
      val result = testController.postEdit("testValidID").apply(
        FakeRequest(POST, "/editItem/testValidID")
        .withFormUrlEncodedBody("title" -> "", "description" -> "")
        .withCSRFToken
      )

      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }
  }

}
