package org.example.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoDBUtil {
    private static MongoClient mongoClient;

    public static MongoClient getClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create("mongodb+srv://adriancr93_db_user:I343VSxSc1sO360d@cluster0.tjer1wm.mongodb.net/");
        }
        return mongoClient;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}
