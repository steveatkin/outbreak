package com.ibm.alchemy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.likethecolor.alchemy.api.Client;
import com.likethecolor.alchemy.api.call.AbstractCall;
import com.likethecolor.alchemy.api.call.RankedNamedEntitiesCall;
import com.likethecolor.alchemy.api.entity.ConceptAlchemyEntity;
import com.likethecolor.alchemy.api.entity.NamedEntityAlchemyEntity;
import com.likethecolor.alchemy.api.entity.Response;
import com.likethecolor.alchemy.api.call.RankedConceptsCall;
import com.likethecolor.alchemy.api.call.type.CallTypeUrl;
import com.rometools.rome.feed.synd.SyndEntry;

public class Alchemy {
	private static final Logger logger = LoggerFactory.getLogger(Alchemy.class);
	private static String apiKey;

	static {
		apiKey = System.getenv("ALCHEMY_API_KEY");
		logger.debug("Alchemy api key {}", apiKey);
	}
	
	public Alchemy() {
		
	}
	
	private static <T> List<T> copyIterator(Iterator<T> iter) {
	    List<T> copy = new ArrayList<T>();
	    while (iter.hasNext())
	        copy.add(iter.next());
	    return copy;
	}
	
	public Alert getAlert(SyndEntry entry) {
		Alert alert = new Alert();
		alert.setHashCode(entry.hashCode());
		alert.setTitle(entry.getTitle());
		alert.setDate(entry.getPublishedDate());
		String url = entry.getLink();
		
		try {
			if(url != null && apiKey != null) {
				Client client = new Client(apiKey);
				AbstractCall<NamedEntityAlchemyEntity> rankedEntitiesCall = new RankedNamedEntitiesCall(new CallTypeUrl(url));
				Response<NamedEntityAlchemyEntity> entityResponse = client.call(rankedEntitiesCall);
				
				NamedEntityAlchemyEntity entityalchemyEntity;
				Iterator<NamedEntityAlchemyEntity> iter = entityResponse.iterator();
				
				while(iter.hasNext()) {
					entityalchemyEntity = iter.next();
					String type = entityalchemyEntity.getType();
					
					if(type.equalsIgnoreCase("HealthCondition")) {
						Entity condition = new Entity(
								entityalchemyEntity.getText(),
								entityalchemyEntity.getScore(),
								type,
								copyIterator(entityalchemyEntity.subtypeIterator())
								);
						alert.addHealthCondition(condition);
					}
					else if(type.equalsIgnoreCase("Country")) {
						Entity location = new Entity(
								entityalchemyEntity.getText(),
								entityalchemyEntity.getScore(),
								type,
								copyIterator(entityalchemyEntity.subtypeIterator())
								);
						alert.addRelatedLocation(location);
					}
				}
			}
			
			
		}
		catch(IOException e) {
			logger.error("could not get entities from alchemy {}", e.getMessage());
		}
		
		return alert;
	}

	public ArrayList<Concept> getConcepts(String url) {
		ArrayList<Concept> concepts = new ArrayList<Concept>();
		
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