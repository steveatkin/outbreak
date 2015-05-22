package com.ibm;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.alchemy.Alchemy;
import com.ibm.alchemy.Alert;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;


public class FeedReader implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(FeedReader.class);
	private static ConcurrentHashMap<Integer, Alert> alerts = new ConcurrentHashMap<>();
	
	
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
	
	public static List<Alert> getAlerts() {
		return new ArrayList<Alert>(alerts.values());
	}
	
	
	private void readFeed(String feedSource) {
		SyndFeedInput input = new SyndFeedInput();
		
		try {
			URL url = new URL(feedSource);
			SyndFeed feed = input.build(new XmlReader(url));
			
			Alchemy alchemy = new Alchemy();
			
			List<SyndEntry> entries = feed.getEntries();
			for (SyndEntry entry : entries) {
			    if(!alerts.containsKey(entry.hashCode())) {
			    	Alert alert = alchemy.getAlerts(entry);
			    	alerts.put(entry.hashCode(), alert);
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
