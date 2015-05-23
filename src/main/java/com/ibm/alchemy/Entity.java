package com.ibm.alchemy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Entity implements Cloneable {
	private static final Logger logger = LoggerFactory.getLogger(Entity.class);
	private String name;
	private double relevance;
	private String type;

	public Entity(String name, double relevance, String type) {
		this.name = name;
		this.relevance = relevance;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public double getRelevance() {
		return relevance;
	}
	
	public String toString() {
		return "Name: " + name + "Type: " + type + "Score: " + relevance;
	}
	
	@Override
	protected Entity clone() {
		Entity clone = null;
		try {
			clone = (Entity) super.clone();
		}
		catch(CloneNotSupportedException e) {
			logger.error("cloning of entity not supported {}", e.getMessage());
		}
		return clone;
	}

}
