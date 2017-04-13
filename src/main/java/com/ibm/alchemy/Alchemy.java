package com.ibm.alchemy;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;


import com.rometools.rome.feed.synd.SyndEntry;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;

import com.ibm.watson.developer_cloud.discovery.v1.Discovery;

import com.ibm.watson.developer_cloud.discovery.v1.model.document.*;
import com.ibm.watson.developer_cloud.discovery.v1.model.environment.*;

import com.ibm.watson.developer_cloud.discovery.v1.model.collection.*;
import com.ibm.watson.developer_cloud.discovery.v1.model.query.*;

// This class has been updated to use Watson Natural Language Understanding rather than Alchemy

public class Alchemy {
	private static final Logger logger = LoggerFactory.getLogger(Alchemy.class);
	NaturalLanguageUnderstanding service;
	Discovery discovery;

	private static String naturalLanguageService = "natural-language-understanding";
	private static String baseURL = "";
	private static String username = "";
	private static String password = "";
	private static String discoveryService = "discovery";
	private static String discoveryURL = "";
	private static String discoveryUsername = "";
	private static String discoveryPassword = "";

	static {
		processVCAP_Services();
	}
	
	public Alchemy() {
		logger.debug("Default constructor called");
		service = new NaturalLanguageUnderstanding(
  			NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
  			username,
  			password
		);

		discovery = new Discovery("2016-12-15");
		discovery.setEndPoint(discoveryURL);
		discovery.setUsernameAndPassword(discoveryUsername, discoveryPassword);
	}

	private static void processVCAP_Services() {
    	logger.info("Processing VCAP_SERVICES");

			JSONObject sysEnv = getVcapServices();

      logger.info("Looking for: "+ naturalLanguageService);

      if (sysEnv != null && sysEnv.containsKey(naturalLanguageService)) {
      			JSONArray services = (JSONArray)sysEnv.get(naturalLanguageService);
				JSONObject service = (JSONObject)services.get(0);
				JSONObject credentials = (JSONObject)service.get("credentials");
				baseURL = (String)credentials.get("url");
				username = (String)credentials.get("username");
				password = (String)credentials.get("password");
				logger.info("baseURL  = "+baseURL);
				logger.info("username   = "+username);
				logger.info("password = "+password);
				logger.info("Watson url {} username {} and password {}", baseURL, username, password);
    	}
		else {
				logger.info("Attempting to use locally defined service credentials");
				baseURL = System.getenv("NATURAL_LANGUAGE_URL");
				username = System.getenv("NATURAL_LANGUAGE_USERNAME");
				password = System.getenv("NATURAL_LANGUAGE_PASSWORD");
				logger.info("Watson url {} username {} and password {}", baseURL, username, password);
		}

		logger.info("Looking for: "+ discoveryService);

		if (sysEnv != null && sysEnv.containsKey(discoveryService)) {
      			JSONArray services = (JSONArray)sysEnv.get(discoveryService);
				JSONObject service = (JSONObject)services.get(0);
				JSONObject credentials = (JSONObject)service.get("credentials");
				discoveryURL = (String)credentials.get("url");
				discoveryUsername = (String)credentials.get("username");
				discoveryPassword = (String)credentials.get("password");
				logger.info("discoveryURL  = "+discoveryURL);
				logger.info("username   = "+discoveryUsername);
				logger.info("password = "+discoveryPassword);
				logger.info("Watson News url {} username {} and password {}", discoveryURL, discoveryUsername, discoveryPassword);
    	}
		else {
				logger.info("Attempting to use locally defined service credentials");
				discoveryURL = System.getenv("DISCOVERY_URL");
				discoveryUsername = System.getenv("DISCOVERY_USERNAME");
				discoveryPassword = System.getenv("DISCOVERY_PASSWORD");
				logger.info("Watson News url {} username {} and password {}", discoveryURL, discoveryUsername, discoveryPassword);
		}


    }

	private static JSONObject getVcapServices() {
        String envServices = System.getenv("VCAP_SERVICES");
        if (envServices == null) return null;
        JSONObject sysEnv = null;
        try {
        	 sysEnv = JSONObject.parse(envServices);
        } catch (IOException e) {
        	// Do nothing, fall through to defaults
        	logger.error("Error parsing VCAP_SERVICES: {} ", e.getMessage());
        }
        return sysEnv;
    }

	
	public Alert createAlert(SyndEntry entry, String hash) {
		Alert alert = new Alert();
		alert.setHashCode(hash);
		alert.setTitle(entry.getTitle());
		alert.setDate(entry.getPublishedDate());
		String url = entry.getLink();
		alert.setURL(url);
		
		try {
			if(url != null) {
				// get all the keywords for the title of the news feed
				KeywordsOptions keywordsOptions = new KeywordsOptions.Builder().limit(5).build();
				Features features = new Features.Builder().keywords(keywordsOptions).build();
				AnalyzeOptions parameters = new AnalyzeOptions.Builder().url(url).features(features).build();
				AnalysisResults results = service.analyze(parameters).execute();

				List<KeywordsResult> identifiedKeywords = results.getKeywords();
				Iterator<KeywordsResult> keywordIterator = identifiedKeywords.iterator();
				KeywordsResult keywordResult;
				
				
				while(keywordIterator.hasNext()) {
				keywordResult = keywordIterator.next();
					Entity keyword = new Entity(
							keywordResult.getText(),
							keywordResult.getRelevance()
							);
					
					// Just add keywords that are health conditions
					if(HealthConditions.isHealthCondition(keyword)) {
						keyword.setType("HealthCondition");
						alert.addHealthCondition(keyword);
					}
				}
				
				
				// Process the url of the RSS feed
				EntitiesOptions entitiesOptions = new EntitiesOptions.Builder().limit(5).build();
				features = new Features.Builder().entities(entitiesOptions).build();
				parameters = new AnalyzeOptions.Builder().url(url).features(features).build();
				results = service.analyze(parameters).execute();

				List<EntitiesResult> identifiedEntities = results.getEntities();
				Iterator<EntitiesResult> entityIterator = identifiedEntities.iterator();
				EntitiesResult entityResult;
				alert.setConcepts(getConcepts(url));
				
				while(entityIterator.hasNext()) {
					entityResult = entityIterator.next();
					String type = entityResult.getType();
					
					if(type.equalsIgnoreCase("HealthCondition")) {
						Entity condition = new Entity(
								entityResult.getText(),
								entityResult.getRelevance(),
								type
								);
						alert.addHealthCondition(condition);
					}
					else if(type.equalsIgnoreCase("Country")) {
						Entity location = new Entity(
								entityResult.getText(),
								entityResult.getRelevance(),
								type
								);
						alert.addRelatedLocation(location);
					}	
				} 
			}
		}
		catch(Exception e) {
			logger.error("could not get entities from Watson Natural Language Understanding {}", e.getMessage());
		}
		
		logger.debug("Alert {}", alert.toString());
		return alert;
	}
	
	public List<AlchemyNewsDoc> getNewsItems(String term, String location) {
		GetEnvironmentsRequest getRequest = new GetEnvironmentsRequest.Builder().build();
		GetEnvironmentsResponse getResponse = discovery.getEnvironments(getRequest).execute();

		List<Environment> envs = getResponse.getEnvironments();
		Iterator<Environment> envIter = envs.iterator();
		String environmentId = "";
		
		// Get the ID for the news environment
		while(envIter.hasNext()) {
			Environment env = envIter.next();
			String name = env.getName();

			if(name.equalsIgnoreCase("Watson News Environment")) {
				environmentId = env.getEnvironmentId();
				break;
			}
		}

		// Get the collection ID for the news environment
		GetCollectionsRequest getCollectionRequest = new GetCollectionsRequest.Builder(environmentId).build();
		GetCollectionsResponse getCollectionResponse = discovery.getCollections(getCollectionRequest).execute();
		List<Collection> collections = getCollectionResponse.getCollections();
		String collectionId = "";

		// Grab the first one
		if(!collections.isEmpty()) {
			collectionId = collections.get(0).getCollectionId();
		}

		String searchQuery = "A[" + term + "^" + location +"]";
		QueryRequest.Builder queryBuilder = new QueryRequest.Builder(environmentId, collectionId);
		queryBuilder.query(searchQuery);

		List<String> fieldList = new ArrayList<String>();
		fieldList.add("enriched");
		queryBuilder.returnFields(fieldList);
		queryBuilder.count(5);
		QueryResponse queryResponse = discovery.query(queryBuilder.build()).execute();


		// This needs a lot of work
		List<Map<String, Object>> newsResults = queryResponse.getResults();
		
		
		
		return new ArrayList<AlchemyNewsDoc>();
	}

	private List<Concept> getConcepts(String url) {
		List<Concept> concepts = new ArrayList<Concept>();
		
		try {
			if(url != null) {
				ConceptsOptions conceptOptions = new ConceptsOptions.Builder().limit(5).build();
				Features features = new Features.Builder().concepts(conceptOptions).build();
				AnalyzeOptions parameters = new AnalyzeOptions.Builder().url(url).features(features).build();
				AnalysisResults results = service.analyze(parameters).execute();

				List<ConceptsResult> identifiedConcepts = results.getConcepts();
				Iterator<ConceptsResult> iter = identifiedConcepts.iterator();
				ConceptsResult conceptResult;


				while(iter.hasNext()) {
					conceptResult = iter.next();
					Concept concept = new Concept();
					concept.setConcept(conceptResult.getText());
					concept.setRelevance(conceptResult.getRelevance());
					concepts.add(concept);
				}
			}
		}
		catch(Exception e) {
			logger.error("could not get concepts from Watson Natural Language Understanding {}", e.getMessage());
		}
		
		logger.debug("Concepts {}", concepts.toString());
		return concepts;
	}
}
