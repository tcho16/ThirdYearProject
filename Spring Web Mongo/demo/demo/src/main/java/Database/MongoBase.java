package Database;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.util.Iterator;

public class MongoBase {

    MongoClient mongoClient = null;

    public MongoBase() {
        try {
            mongoClient = new MongoClient("localhost", 27017);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printCollection(String databaseName, String collectionName) {

        MongoDatabase db = mongoClient.getDatabase("SpringWeb");

    }

    public String documentToJSON(String id, String databaseName, String collectionName){
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document checkIfRecordExists = new Document();
        checkIfRecordExists.put("_id", id);
        FindIterable<Document> documents = collection.find(checkIfRecordExists);
        if(documents.first() == null){
            return "Did not any records";
        }else{
            return documents.first().toJson();
        }
    }

    public void addGPSEntry(String id, String longitude, String latitude, String status, String databaseName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document checkIfRecordExists = new Document();
        checkIfRecordExists.put("_id", id);
        FindIterable<Document> setOfDocuments = collection.find(checkIfRecordExists);

        if (setOfDocuments.first() == null) {
            System.out.println("In if statement");
            Document tableEntry = new Document();
            tableEntry.put("_id", id);
            tableEntry.put("longitude", longitude);
            tableEntry.put("latitude", latitude);
            tableEntry.put("status", status);

            collection.insertOne(tableEntry);
        }else {
            System.out.println("In else statement");
            Document doc = new Document();
            doc.put("_id", id);
            Document doc2 = new Document();
            doc2.append("$set", new BasicDBObject().append("status", status));
            collection.findOneAndUpdate(doc, doc2);
        }
        closeConnection();
    }

    public void closeConnection() {
        mongoClient.close();
    }

    public String allDocumentToJSON(String databaseName, String collectionName) {
        StringBuilder str = new StringBuilder();
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        FindIterable<Document> documents = collection.find();
        for(Document doc : collection.find()){
            str.append(doc.toJson());
        }
        return str.toString();

    }
}
