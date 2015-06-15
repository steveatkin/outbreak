package com.ibm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
public class Starter {
	private static final Logger logger = LoggerFactory.getLogger(OutbreakApplication.class);
	private ScheduledExecutorService scheduler;
	
	
	@PostConstruct
    public void init() {
		logger.debug("EJB starting executor");
    	
    	scheduler = Executors.newSingleThreadScheduledExecutor();
    	Runnable command = new FeedReader();
    	
    	long initialDelay = 1;
        TimeUnit unit = TimeUnit.MINUTES;
        // period the period between successive executions
        long period = 60;// 30 Minutes!
 
        scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
	}
	
}
