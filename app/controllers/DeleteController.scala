package controllers

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.MongoService
import utils.LoggerUtil
import views.html.genericErrorView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DeleteController @Inject()
(val controllerComponents: ControllerComponents,
 mongoService: MongoService,
 errorView: genericErrorView
)
  extends BaseController with I18nSupport with LoggerUtil{
  implicit val ec: ExecutionContext = controllerComponents.executionContext

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    mongoService.deleteTask(id).map{
      case true =>
        logger.error(s"[DeleteController][delete] - Successfully delete item with id: $id")
        Redirect(routes.HomeController.home())
      case false =>
        logger.error(s"[DeleteController][delete] - Something went wrong: could not delete item with id: $id")
        InternalServerError(errorView(s"Something went wrong: could not delete item with id: $id"))
    }.recover{
      case e: Exception =>
        logger.error(s"[DeleteController][delete] - An Unexpected error occurred while deleting item with id: $id; ${e.getMessage}")
        InternalServerError(errorView(s"An Unexpected error occurred while deleting item with id: $id"))
    }

  }

}
