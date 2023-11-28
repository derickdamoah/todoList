package repositories

import com.typesafe.config.ConfigFactory
import org.mongodb.scala._
import io.github.cdimascio.dotenv.Dotenv

import scala.util.Try

class MongoRepository {
  val mongodbconfig = ConfigFactory.load().getConfig("mongodb")
  private val dotenv = Dotenv.configure().ignoreIfMissing().load()
  private val connection_string: String = Try(mongodbconfig.getString("connectionString")).getOrElse(s"mongodb+srv://${dotenv.get("MONGO_DB_USERNAME")}:${dotenv.get("MONGO_DB_PASSWORD")}@todo-list-cluster.elkwww6.mongodb.net/?retryWrites=true&w=majority")
  private val database_name: String = mongodbconfig.getString("database")
  private val collection_name: String = mongodbconfig.getString("collection")
  lazy val mongoClient: MongoClient = MongoClient(connection_string)

  val database: MongoDatabase = mongoClient.getDatabase(database_name)

  val collection: MongoCollection[Document] = database.getCollection(collection_name)

}
