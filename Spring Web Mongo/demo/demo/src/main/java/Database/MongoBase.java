package Database;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;

public class MongoBase {

    MongoClient mongoClient = null;

    public MongoBase() {
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

    public void printCollection(String databaseName, String collectionName) {

        MongoDatabase db = mongoClient.getDatabase("SpringWeb");

    }

    public void addGPSEntry(String id, String longitude, String latitude, String status, String databaseName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        collection.count();
        Document checkIfRecordExists = new Document();
        checkIfRecordExists.put("_id", id);

        FindIterable<Document> documents = collection.find(checkIfRecordExists);

        if (documents.first() == null) {
            System.out.println("In if statement");
            Document tableEntry = new Document();
            tableEntry.put("_id", id);
            tableEntry.put("longitude", longitude);
            tableEntry.put("latitude", latitude);
            tableEntry.put("status", status);

            collection.insertOne(tableEntry);
            closeConnection();
        } else {
            System.out.println("In else statement");
            Document doc = new Document();
            doc.put("_id", id);
            Document doc2 = new Document();
            doc2.append("$set", new BasicDBObject().append("status", status));
            collection.findOneAndUpdate(doc, doc2);
            closeConnection();
        }
    }

    public void addEntry(String databaseName, String collectionName, String nameInURL) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        //DROP COLLECTION
        collection.drop();

        Document obj = new Document();
        obj.put("ULLL", nameInURL);


        collection.insertOne(obj);
        closeConnection();


    }

    public void closeConnection() {
        mongoClient.close();
    }

}
