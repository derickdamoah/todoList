package testUtils

import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import repositories.MongoRepository

trait TestRepository extends MongoRepository{
  override lazy val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017/todoDatabase")
  override val database: MongoDatabase = mongoClient.getDatabase("testDatabase")

  override val collection: MongoCollection[Document] = database.getCollection("testCollection")
}

