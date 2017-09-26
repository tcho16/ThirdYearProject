package com.w1481217.demo;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;

import java.util.Collection;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"Controller"})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		try {
			// Creates a new instance of MongoDBClient and connect to localhost
			// port 27017
			MongoClient mongoClient = new MongoClient("localhost", 27017);

            // Creating a database called Hello and creating a collection called HelloCollectionSS
            MongoDatabase db = mongoClient.getDatabase("Hello");
            db.createCollection("HelloCollectionSS");

            //Displaying the current databases in Mongo
            for(String name : mongoClient.listDatabaseNames()){
                System.out.println(name);
            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
