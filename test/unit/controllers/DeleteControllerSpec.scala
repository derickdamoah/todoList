package unit.controllers

import controllers.DeleteController
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.http.Status.{INTERNAL_SERVER_ERROR, SEE_OTHER}
import play.api.test.Helpers.{POST, defaultAwaitTimeout, status, stubControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import unit.testUtils.TestUtils
import views.html.genericErrorView

import scala.concurrent.Future

class DeleteControllerSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting with TestUtils{

  val stubErrorView: genericErrorView = inject[genericErrorView]
  val testDeleteController: DeleteController = new DeleteController(
    controllerComponents = stubControllerComponents(),
    mongoService = stubMongoService,
    errorView = stubErrorView)

  "DeleteController.delete()" should {
    "successfully delete item with the correct id" in {

      when(stubMongoService.deleteTask("000000000000000000000000")).thenReturn(Future.successful(true))
      val result = testDeleteController.delete("000000000000000000000000").apply(FakeRequest(POST, "/delete/000000000000000000000000"))

      status(result) mustBe SEE_OTHER
    }

    "fail to delete item with the incorrect id" in {

      when(stubMongoService.deleteTask("0000000000000000000000001")).thenReturn(Future.successful(false))
      val result = testDeleteController.delete("0000000000000000000000001").apply(FakeRequest(POST, "/delete/0000000000000000000000001"))

      status(result) mustBe Status.INTERNAL_SERVER_ERROR
    }

    "fail with an unexpected error" in {
      when(stubMongoService.deleteTask("testString")).thenReturn(Future.failed(new Exception("An Unexpected error occurred while deleting item with id: testString")))
      val result = testDeleteController.delete("testString").apply(FakeRequest(POST, "/delete/testString"))

      status(result) mustBe INTERNAL_SERVER_ERROR
    }
  }

}
