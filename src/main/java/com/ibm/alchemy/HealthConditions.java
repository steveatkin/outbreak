package com.ibm.alchemy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthConditions {
	private static final Logger logger = LoggerFactory.getLogger(HealthConditions.class);
	static ArrayList<Entity> conditions = new ArrayList<Entity>();
	
	static {
		try {
			Properties props = new Properties();
			props.load(HealthConditions.class.getResourceAsStream("/health_conditions.properties"));
			conditions = new ArrayList<Entity>();
			
			for (String key : props.stringPropertyNames()) {
			    String value = props.getProperty(key);
			    conditions.add(new Entity(value, "HealthCondition"));
			}
		}
		catch(IOException e) {
			logger.error("Health conditions not loaded {}", e.getMessage());
		}
	}
	
	public static boolean isHealthCondition(Entity condition) {
		return conditions.contains(condition);
	}

}
