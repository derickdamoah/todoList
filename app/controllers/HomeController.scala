package controllers

import play.api.i18n.I18nSupport
import play.api.mvc._
import services.MongoService
import utils.LoggerUtil
import views.html.homePageView

import javax.inject._
import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()
(val controllerComponents: ControllerComponents,
 mongoService: MongoService,
 homePageView: homePageView
) extends BaseController with I18nSupport with LoggerUtil{

  def home(): Action[AnyContent] = Action.async { implicit request =>
    implicit val ec: ExecutionContext = controllerComponents.executionContext

    val getAllItems = mongoService.getAllTasks

    getAllItems.map{
      allTasks => {
        logger.info("[HomeController][home] - successfully retrieved all items")
        Ok(homePageView(allTasks))
      }
    }

  }

}
