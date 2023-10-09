package controllers

import forms.TodoForm
import forms.TodoForm.todoForm
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.MongoService
import utils.LoggerUtil
import views.html.{editPageView, genericErrorView}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class EditItemController @Inject()
(val controllerComponents: ControllerComponents,
 mongoService: MongoService,
 editPage: editPageView,
 errorView: genericErrorView
)  extends BaseController with I18nSupport with LoggerUtil{
  implicit val ec: ExecutionContext = controllerComponents.executionContext

  def showEdit(id: String): Action[AnyContent] = Action.async { implicit request =>

    mongoService.getOneTask(id).map{
      task => {
        task match {
          case Some(value) =>
            logger.info("[EditItemController][showEdit] - successfully rendered the edit page")
            Ok(editPage(todoForm.fillAndValidate(value), id))

          case None =>
            logger.error(s"[EditItemController][showEdit] - could not find an item with id: $id")
            BadRequest(errorView(s"Something went wrong: could not find item with id: $id"))
        }
        }
    }
  }

  def postEdit(id: String): Action[AnyContent] = Action.async { implicit request =>

    mongoService.getOneTask(id).map{
      task => TodoForm.todoForm.bindFromRequest().fold(
        formWithErrors => {
          logger.warn(s"[EditItemController][postEdit] - The following validation errors occurred while binding the form values: ${formWithErrors.errors}")
          BadRequest(editPage(formWithErrors, id))
        },
        formData => {
          mongoService.updateOneTask(id, formData.title, formData.description)
          logger.warn(s"[EditItemController][postEdit] - successfully updated the item with id: $id")
          Redirect(routes.HomeController.home())
        }
      )
    }.recover{
      case exception: Exception =>
        logger.error(s"[EditItemController][postEdit] - An unexpected error occurred while updating item with id: $id; ${exception.getMessage}")
        InternalServerError(errorView(s"An Unexpected error occurred while u[dating item with id: $id"))
    }

  }
}
