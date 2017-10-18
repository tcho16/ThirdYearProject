package DAO;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DAOImplementation implements DAOInterface {

    @Autowired
    MongoClient mongoClient;

    public DAOImplementation() {
    }

    public String documentToJSON(String id, String databaseName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        String returnString = "";
        Document checkIfRecordExists = new Document();
        checkIfRecordExists.put("_id", id);
        FindIterable<Document> documents = collection.find(checkIfRecordExists);

        if (documents.first() == null) {
            returnString = "{Did not find any records}";
        } else {
            returnString = documents.first().toJson();
        }
        //closeConnection();
        return returnString;
    }

    public void addGPSEntry(String id, String longitude, String latitude, String status, String databaseName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateObj = new Date();

        Document checkIfRecordExists = new Document();
        checkIfRecordExists.put("_id", id);
        FindIterable<Document> setOfDocuments = collection.find(checkIfRecordExists);

        if (setOfDocuments.first() == null) {
            Document mainEntry = new Document();
            mainEntry.append("_id", id);
            mainEntry.append("longitude", longitude);
            mainEntry.append("latitude", latitude);

            List<BasicDBObject> timeDateUsage = new ArrayList<>();
            timeDateUsage.add(new BasicDBObject(df.format(dateObj).toString(), status));
            mainEntry.put("timeDateOfUsage", timeDateUsage);
            collection.insertOne(mainEntry);
        } else {
            Document doc = new Document();
            doc.put("_id", id);
            collection.updateOne(doc, new Document(
                            "$push", new Document("timeDateOfUsage",
                            new Document(df.format(dateObj).toString(), status))
                    )
            );
        }
    }

    public void closeConnection() {
        mongoClient.close();
    }

    public String allDocumentToJSON(String databaseName, String collectionName) {
        StringBuilder str = new StringBuilder();
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        for (Document doc : collection.find()) {
            str.append(doc.toJson());
        }
        return str.toString();
    }
}
