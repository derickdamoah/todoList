package controllers

import forms.TodoForm
import forms.TodoForm.todoForm
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.MongoService
import utils.LoggerUtil
import views.html.addItem

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AddItemController @Inject()
(val controllerComponents: ControllerComponents,
 mongoService: MongoService,
 addItemView: addItem
) extends BaseController with I18nSupport with LoggerUtil{


  def showAddItem(): Action[AnyContent] = Action.async { implicit request =>
    logger.info("[AddItemController][showAddItem] - Add Item page rendered correctly")
    Future.successful(Ok(addItemView(todoForm)))
  }

  def addItem(): Action[AnyContent] = Action.async { implicit request =>
    TodoForm.todoForm.bindFromRequest().fold(
      formWithErrors => {
        logger.warn("[AddItemController][addItem] - A validation error occurred while binding the form values")
        Future.successful(BadRequest(addItemView(formWithErrors)))
      },
      formData => {
        mongoService.createTask(formData.title, formData.description)
        logger.info("[AddItemController][addItem] - Successfully created a new item, redirecting to home page")
        Future.successful(Redirect(routes.HomeController.home()))
      }
    )

  }

}
