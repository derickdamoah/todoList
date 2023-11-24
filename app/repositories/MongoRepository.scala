package repositories

import com.typesafe.config.ConfigFactory
import org.mongodb.scala._

class MongoRepository {
  val mongodbconfig = ConfigFactory.load().getConfig("mongodb")

  private val connection_string: String = mongodbconfig.getString("connectionString")
  private val database_name: String = mongodbconfig.getString("database")
  private val collection_name: String = mongodbconfig.getString("collection")
  println(Console.GREEN + ConfigFactory.load().getString("mongoDbUserName") + Console.RESET)
  lazy val mongoClient: MongoClient = MongoClient(connection_string)

  val database: MongoDatabase = mongoClient.getDatabase(database_name)

  val collection: MongoCollection[Document] = database.getCollection(collection_name)

}
