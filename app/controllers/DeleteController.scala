package controllers

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.MongoService
import utils.LoggerUtil

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeleteController @Inject()
(val controllerComponents: ControllerComponents,
 mongoService: MongoService,
)
  extends BaseController with I18nSupport with LoggerUtil{
  implicit val ec: ExecutionContext = controllerComponents.executionContext

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    mongoService.deleteTask(id)
    logger.info(s"[DeleteController][delete] - successfully deleted item with id: $id")
    Future.successful(Redirect(routes.HomeController.home()))
  }

}
