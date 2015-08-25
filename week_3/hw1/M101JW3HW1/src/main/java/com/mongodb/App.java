package com.mongodb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * /** Week 3, Homework 1.
 * @author Josue Araujo (NeMuX)
 * @version 1.0
 */
public class App {
	public static void main(String[] args) {
		MongoClient client = new MongoClient();
		MongoDatabase db = client.getDatabase("school");
		MongoCollection<Document> col = db.getCollection("students");

		MongoCursor<Document> cDoc = col.find(new Document("scores.type", "homework")).iterator();
		try {
			while (cDoc.hasNext()) {
				Document item = cDoc.next();
				List<Document> scoresOld = (List<Document>) item.get("scores");
				List<Document> scoresNew = new ArrayList<Document>();
				List<Double> hwScores = new ArrayList<Double>();

				for (Document d : scoresOld) {
					if (d.getString("type").equals("homework")) {
						hwScores.add(d.getDouble("score"));
					} else {
						scoresNew.add(new Document("type", d.getString("type")).append("score", d.getDouble("score")));
					}
				}
				// Erase lowest homework score
				Collections.sort(hwScores);
				hwScores.remove(0);
				for (Double s : hwScores) {
					scoresNew.add(new Document("type", "homework").append("score", s));
				}
				item.remove("scores");
				item.append("scores", scoresNew);

				//Option 1.- Delete and Reinsert document
				//col.deleteOne(new Document("_id", item.get("_id")));
				//col.insertOne(item);
				//System.out.println(item);
				
				//Option 2.- Update Document
				col.updateOne(new Document("_id", item.get("_id")),
						new Document("$unset",new Document("scores",1)));				
				col.updateOne( new Document("_id", item.get("_id")),
						new Document("$pushAll", new Document("scores", scoresNew)));				
				Document doc = col.find(new Document("_id",item.get("_id"))).first();
				System.out.println(doc);
			}
		} finally {
			cDoc.close();
			client.close();
			System.out.println("Done...");
		}
	}
}
