package com.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * Week 2, Homework 2.
 * @author Josue Araujo (NeMuX)
 * @version 1.0
 */
public class App 
{
    public static void main( String[] args )
    {
    	MongoClient client = new MongoClient();
    	MongoDatabase db = client.getDatabase("students");
    	MongoCollection<Document> col = db.getCollection("grades");    	
    	MongoCursor<Document> cDoc = col.find(new Document("type","homework"))
    			.sort(new Document("student_id",1).append("score",1)).iterator();
    	try{
    		int student_id = 0;
		while(cDoc.hasNext()){
			Document item = cDoc.next();
			if (item.getInteger("student_id") != student_id ){
					System.out.println(item.toJson());
					student_id = item.getInteger("student_id");
					col.deleteOne(new Document("_id",item.get("_id")));
				}
			}
    	} finally{
    		cDoc.close();
    		client.close();
    		System.out.println("Done...");
    	}
    }
}