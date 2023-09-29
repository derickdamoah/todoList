import play.api._
import play.api.mvc._
import play.core.server.{ProdServerStart, RealServerProcess}

object Main {
  def main(args: Array[String]): Unit = {
//    val customConfigPath = "conf/application.conf"
//    val config = ConfigFactory.load(customConfigPath)
    ProdServerStart.start(new RealServerProcess(Seq()))
  }
}


//object Main extends App {
//  private val environment = play.api.Environment.simple()
//  private val context = ApplicationLoader.Context.create(environment)
//  private val loader = ApplicationLoader(context)
//  val app = loader.load(context)
//  LoggerConfigurator(app.classloader.getParent).foreach {
//    _.configure(context.environment, context.initialConfiguration, Map.empty)
//  }
//  ProdServerStart.start(new RealServerProcess(Seq()))
//}