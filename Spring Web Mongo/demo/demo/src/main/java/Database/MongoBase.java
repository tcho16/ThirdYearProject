package Database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.print.Doc;

public class MongoBase {

    MongoClient mongoClient = null;

    public MongoBase(){
        try {
            // Creates a new instance of MongoDBClient and connect to localhost
            // port 27017
            mongoClient = new MongoClient("localhost", 27017);

            // Creating a database called Hello and creating a collection called HelloCollectionSS
//            MongoDatabase db = mongoClient.getDatabase("Hello");
//            db.createCollection("HelloCollectionSS");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printCollection(String databaseName, String collectionName){

            MongoDatabase db =   mongoClient.getDatabase("SpringWeb");

    }

    public void addEntry(String databaseName, String collectionName, String nameInURL) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        //DROP COLLECTION
        collection.drop();

        Document obj = new Document();
        obj.put("ULLL", nameInURL);


        collection.insertOne(obj);


    }

    public void closeConnection() {
        mongoClient.close();
    }

}
