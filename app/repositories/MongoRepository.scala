package repositories

import org.mongodb.scala._
import play.api.Configuration
import play.api.mvc.ControllerComponents

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class MongoRepository @Inject()(val controllerComponents: ControllerComponents, configuration: Configuration) {
  lazy val mongoUri: String = configuration.get[String]("mongoDatabase.uri")
  lazy val mongoClient: MongoClient = MongoClient(mongoUri)
  implicit val ec: ExecutionContext = controllerComponents.executionContext

  val database: MongoDatabase = mongoClient.getDatabase("todoDatabase")

  val collection: MongoCollection[Document] = database.getCollection("todoCollection")

}
