package com.ibm;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.ibm.alchemy.Alchemy;
import com.ibm.alchemy.Alert;
import com.ibm.alchemy.Entity;
import com.ibm.alchemy.Location;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.google.common.collect.Collections2;

@Path("/outbreak")
public class FeedReader implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(FeedReader.class);
	private MongoCollection alerts;
	private MongoCollection countries;
	
	
	public void run() {
		connectFeeds();
	}
	
	
	public FeedReader() {
		alerts = MongoConnection.getAlertsCollection();
		countries = MongoConnection.getCountriesCollection();
	}
	
	public void connectFeeds() {
		logger.debug("Reading feeds");
		// WHO
		readFeed("http://www.who.int/feeds/entity/csr/don/en/rss.xml");
		// CDC
		readFeed("http://wwwnc.cdc.gov/travel/rss/notices.xml");
		// Canada
		readFeed("http://www.phac-aspc.gc.ca/rss/tm-mv-eng.xml");
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAlerts(@QueryParam("hscore") double hscore, @QueryParam("lscore") double lscore, @QueryParam("country") String country) throws IOException{
		Iterator<Alert> iterator;
		MongoCursor<Alert> all;
		
		// see if we should first filter by country
		if(country != null) {
			all = alerts.find("{relatedLocations: { $all: [ {'$elemMatch': {'name': # } } ]} }", country).as(Alert.class);
			iterator = all.iterator();
		}
		else {
			all = alerts.find().as(Alert.class);
		}
		
		
		// see if the results need to be filter based on relevance
		if(hscore > 0 && lscore > 0) {
			iterator = getFilteredAlerts(all.iterator(), hscore, lscore).iterator();
		}
		else {
			iterator = all.iterator();
		}
		
		ObjectMapper mapper = new ObjectMapper();
		//String listContents = mapper.writeValueAsString(alerts);
		String listContents = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(iterator);
		
		return Response.ok(listContents).build();
	}
	
	private List<Alert> getFilteredAlerts(Iterator<Alert> iterator, final double hscore, final double lscore) {
		Predicate<Entity> healthPredicate = new Predicate<Entity>() {
			@Override
			public boolean apply(Entity input) {
				return (input.getRelevance() >= hscore);
			}
		};
		
		Predicate<Entity> locationPredicate = new Predicate<Entity>() {
			@Override
			public boolean apply(Entity input) {
				return (input.getRelevance() >= lscore);
			}
		};
		
		// Create a deep copy
		Collection<Alert> copy = new ArrayList<Alert>();
		
		while(iterator.hasNext()){
		    copy.add(iterator.next().clone());
		}
		
		for(Alert alert: copy) {
			Collection<Entity> result = Collections2.filter(alert.getHealthConditions(), healthPredicate);
			alert.setHealthConditions(new ArrayList<Entity>(result));
			
			result = Collections2.filter(alert.getRelatedLocations(), locationPredicate);
			alert.setRelatedLocations(new ArrayList<Entity>(result));
		}
		
		return new ArrayList<Alert>(copy);
	}
	
	
	private void readFeed(String feedSource) {
		SyndFeedInput input = new SyndFeedInput();
		
		try {
			URL url = new URL(feedSource);
			SyndFeed feed = input.build(new XmlReader(url));
			
			Alchemy alchemy = new Alchemy();
			
			List<SyndEntry> entries = feed.getEntries();
	
			for (SyndEntry entry : entries) {
				String hash = DigestUtils.md5Hex(entry.getTitle() + entry.getPublishedDate() + entry.getLink());
				long count = alerts.count("{hashcode:#}", hash);
				
				// This entry is not in the database so add it
				if(count == 0) {
			    	Alert alert = alchemy.createAlert(entry, hash);
			    	alerts.insert(alert);
			    	logger.debug("Added alert {}", alert.toString());
			    	
			    	// Add the relevant countries to the country collection database
			    	for(Entity entity: alert.getRelatedLocations()) {
			    		String name = entity.getName();
			    		
			    		count = countries.count("{name:#}", name);
			    		if(count == 0) {
			    			countries.insert(new Location(name));
			    		}
			    	}
			    }
			}
		}
		
		catch(MalformedURLException e) {
			logger.error("URL is bad {}", e.toString());
		}
		catch(FeedException | IOException  e) {
			logger.error("Error reading feed {}", e.toString());
		}
	}

}
