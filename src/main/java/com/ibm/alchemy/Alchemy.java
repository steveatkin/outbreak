package com.ibm.alchemy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likethecolor.alchemy.api.Client;
import com.likethecolor.alchemy.api.call.AbstractCall;
import com.likethecolor.alchemy.api.call.RankedKeywordsCall;
import com.likethecolor.alchemy.api.call.RankedNamedEntitiesCall;
import com.likethecolor.alchemy.api.entity.ConceptAlchemyEntity;
import com.likethecolor.alchemy.api.entity.KeywordAlchemyEntity;
import com.likethecolor.alchemy.api.entity.NamedEntityAlchemyEntity;
import com.likethecolor.alchemy.api.entity.Response;
import com.likethecolor.alchemy.api.call.RankedConceptsCall;
import com.likethecolor.alchemy.api.call.type.CallTypeUrl;
import com.likethecolor.alchemy.api.call.type.CallTypeText;
import com.rometools.rome.feed.synd.SyndEntry;

public class Alchemy {
	private static final Logger logger = LoggerFactory.getLogger(Alchemy.class);
	private static String apiKey;
	private static String alchemyURL;

	static {
		apiKey = System.getenv("ALCHEMY_API_KEY");
		alchemyURL = System.getenv("ALCHEMY_URL");
		logger.debug("Alchemy api url {} and key {}", alchemyURL, apiKey);
	}
	
	public Alchemy() {
		
	}
	
	public Alert createAlert(SyndEntry entry, String hash) {
		Alert alert = new Alert();
		alert.setHashCode(hash);
		alert.setTitle(entry.getTitle());
		alert.setDate(entry.getPublishedDate());
		String url = entry.getLink();
		alert.setURL(url);
		
		try {
			if(url != null && apiKey != null) {
				Client client = new Client(apiKey);
				
				// get all the keywords for the title of the news feed
				AbstractCall<KeywordAlchemyEntity> rankedKeywordsCall = new RankedKeywordsCall(new CallTypeText(entry.getTitle()));
				Response<KeywordAlchemyEntity> keywordResponse = client.call(rankedKeywordsCall);
				
				KeywordAlchemyEntity keywordalchemyEntity;
				Iterator<KeywordAlchemyEntity> keywordIterator = keywordResponse.iterator();
				
				while(keywordIterator.hasNext()) {
					keywordalchemyEntity = keywordIterator.next();
					Entity keyword = new Entity(
							keywordalchemyEntity.getKeyword(),
							keywordalchemyEntity.getScore()
							);
					
					// Just add keywords that are health conditions
					if(HealthConditions.isHealthCondition(keyword)) {
						keyword.setType("HealthCondition");
						alert.addHealthCondition(keyword);
					}
				}
				
				
				// Process the url of the RSS feed
				AbstractCall<NamedEntityAlchemyEntity> rankedEntitiesCall = new RankedNamedEntitiesCall(new CallTypeUrl(url));
				Response<NamedEntityAlchemyEntity> entityResponse = client.call(rankedEntitiesCall);
				
				alert.setConcepts(getConcepts(url));
				
				NamedEntityAlchemyEntity entityalchemyEntity;
				Iterator<NamedEntityAlchemyEntity> entityIterator = entityResponse.iterator();
				
				while(entityIterator.hasNext()) {
					entityalchemyEntity = entityIterator.next();
					String type = entityalchemyEntity.getType();
					
					if(type.equalsIgnoreCase("HealthCondition")) {
						Entity condition = new Entity(
								entityalchemyEntity.getText(),
								entityalchemyEntity.getScore(),
								type
								);
						alert.addHealthCondition(condition);
					}
					else if(type.equalsIgnoreCase("Country")) {
						Entity location = new Entity(
								entityalchemyEntity.getText(),
								entityalchemyEntity.getScore(),
								type
								);
						alert.addRelatedLocation(location);
					}	
				} 
				
			}
			
		}
		catch(IOException e) {
			logger.error("could not get entities from alchemy {}", e.getMessage());
		}
		
		logger.debug("Alert {}", alert.toString());
		return alert;
	}
	
	public List<AlchemyNewsDoc> getNewsItems(String term, String location) {
		String searchQuery = "A[" + term + "^" + location +"]";
		AlchemyNews alchemyNews = new AlchemyNews();
		
		RequestConfig requestConfig = RequestConfig.custom().
				setConnectTimeout(30 * 1000).build();
		HttpClient httpClient = HttpClientBuilder.create().
				setDefaultRequestConfig(requestConfig).build();
		
		try {
			URIBuilder builder = new URIBuilder();
			builder.setScheme("https").setHost(alchemyURL).setPath("/calls/data/GetNews")
				.setParameter("apikey", apiKey)
				.setParameter("outputMode", "json")
				.setParameter("start", "now-7d")
				.setParameter("end", "now")
				.setParameter("maxResults", "10")
				.setParameter("q.enriched.url.title", searchQuery)
				.setParameter("return", "enriched");
			URI uri = builder.build();
			
			HttpGet httpGet = new HttpGet(uri);
			
			httpGet.setHeader("Content-Type", "text/plain");
			HttpResponse httpResponse = httpClient.execute(httpGet);
			
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				BufferedReader rd = new BufferedReader(
				        new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));

				// Read all the news items
				ObjectMapper mapper = new ObjectMapper();
				alchemyNews = mapper.readValue(rd, AlchemyNews.class);
			}
			else {
				logger.error("could not get news from IBM Alchemy http code {}", httpResponse.getStatusLine().getStatusCode());
			}
			
		}
		catch(Exception e) {
			logger.error("AlchemyAPI error: {}", e.getMessage());
		}
		
		return new ArrayList<AlchemyNewsDoc>(Arrays.asList(alchemyNews.getResult().getDocs()));
	}

	private List<Concept> getConcepts(String url) {
		List<Concept> concepts = new ArrayList<Concept>();
		
		try {
			if(url != null && apiKey != null) {
				Client client = new Client(apiKey);
				
				AbstractCall<ConceptAlchemyEntity> rankedConceptsCall = new RankedConceptsCall(new CallTypeUrl(url));
				Response<ConceptAlchemyEntity> conceptResponse = client.call(rankedConceptsCall);
		
				ConceptAlchemyEntity entity;
				Iterator<ConceptAlchemyEntity> iter = conceptResponse.iterator();
			
				while(iter.hasNext()) {
					entity = iter.next();
					Concept concept = new Concept();
					concept.setConcept(entity.getConcept());
					concept.setRelevance(entity.getScore());
					concepts.add(concept);
				}
			}
		}
		catch(IOException e) {
			logger.error("could not get concepts from alchemy {}", e.getMessage());
		}
		
		logger.debug("Concepts {}", concepts.toString());
		return concepts;
	}
}
