package utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class MongoDbUtils {

    private static MongoClient client;

    private static synchronized MongoDatabase db() {
        if (client == null) {
            client = MongoClients.create(ConfigReader.get("mongo.uri"));
        }
        return client.getDatabase(ConfigReader.get("mongo.db"));
    }

    // Find one document where field = value, or null if none
    public static Document findOne(String collection, String field, Object value) {
        return db().getCollection(collection)
                .find(Filters.eq(field, value))
                .first();
    }

    // Count documents where field = value
    public static long count(String collection, String field, Object value) {
        return db().getCollection(collection)
                .countDocuments(Filters.eq(field, value));
    }

    // Insert one document (for test data setup)
    public static void insert(String collection, Document document) {
        db().getCollection(collection).insertOne(document);
    }

    // Delete documents where field = value (for test data cleanup)
    public static void delete(String collection, String field, Object value) {
        db().getCollection(collection)
                .deleteMany(Filters.eq(field, value));
    }
}