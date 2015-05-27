package com.ibm;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.ibm.alchemy.Alchemy;
import com.ibm.alchemy.Alert;
import com.ibm.alchemy.Entity;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.google.common.collect.Collections2;

@Path("/outbreak")
public class FeedReader implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(FeedReader.class);
	private static ConcurrentHashMap<Integer, Alert> alertsMap = new ConcurrentHashMap<>();
	
	
	public void run() {
		connectFeeds();
	}
	
	public FeedReader() {
	
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
	public Response getAlerts(@QueryParam("hscore") double hscore, @QueryParam("lscore") double lscore) throws IOException{
		List<Alert> alerts;
		
		if(hscore > 0 && lscore > 0) {
			alerts = getFilteredAlerts(hscore, lscore);
		}
		else {
			// make shallow copy
			alerts = new ArrayList<Alert>(alertsMap.values());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		//String listContents = mapper.writeValueAsString(alerts);
		String listContents = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(alerts);
		
		return Response.ok(listContents).build();
	}
	
	private List<Alert> getFilteredAlerts(final double hscore, final double lscore) {
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
		Collection<Alert> copy = new ArrayList<Alert>(alertsMap.values().size());
		Iterator<Alert> iterator = alertsMap.values().iterator();
		while(iterator.hasNext()){
		    copy.add(iterator.next().clone());
		}
		
		for(Alert alert : copy) {
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
			    if(!alertsMap.containsKey(entry.hashCode())) {
			    	Alert alert = alchemy.getAlerts(entry);
			    	alertsMap.put(entry.hashCode(), alert);
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
