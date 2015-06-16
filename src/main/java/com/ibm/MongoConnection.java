package com.ibm;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoConnection {
	private static final Logger logger = LoggerFactory.getLogger(MongoConnection.class);
	private static String mongoService = "mongolab";
	private static String baseURLMongo = "";
	
	
	private static MongoCollection alerts;
	private static MongoCollection locations;
	
	static {
		try {
			JSONObject sysEnv = getVcapServices();
			MongoClient mongo;
			DB db;
			
			logger.info("Processing VCAP_SERVICES");
			logger.info("Looking for: "+ mongoService);

			if (sysEnv != null && sysEnv.containsKey(mongoService)) {
				JSONArray services = (JSONArray)sysEnv.get(mongoService);
				JSONObject service = (JSONObject)services.get(0);
				JSONObject credentials = (JSONObject)service.get("credentials");
				baseURLMongo = (String)credentials.get("uri");
				MongoClientURI mongoURI = new MongoClientURI(baseURLMongo);
				mongo = new MongoClient(mongoURI);
				String dbName = mongoURI.getDatabase();
				db = mongo.getDB(dbName);
				logger.info("baseURL  = "+ baseURLMongo);
			}
			else {
				logger.debug("Starting mongo localhost");
				mongo = new MongoClient("127.0.0.1", 27017);
				db = mongo.getDB("outbreak");
				logger.debug("Mongo up localhost");
			}
			
			
			Jongo jongo = new Jongo(db);
			alerts = jongo.getCollection("alerts");
			locations = jongo.getCollection("countries");	
		}
		catch(Exception e) {
			logger.error("Could not connect to mongodb {}", e.toString());
		}
	}
	
	private static JSONObject getVcapServices() {
        String envServices = System.getenv("VCAP_SERVICES");
        JSONObject sysEnv = null;

        if (envServices == null) {
        	logger.info("VCAP Services not found, using predfined meta-information");
        	return null;
        }

        try {
        	sysEnv = JSONObject.parse(envServices);
        }
        catch(Exception e) {
        	logger.error("Error parsing VCAP_SERVICES: {}", e.getMessage());
        }

        return sysEnv;
    }
	
	protected static MongoCollection getAlertsCollection() {
		return alerts;
	}
	
	protected static MongoCollection getLocationsCollection() {
		return locations;
	}
}
