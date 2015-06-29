package com.ibm.alchemy;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Keyword implements Cloneable {
	private static final Logger logger = LoggerFactory.getLogger(Keyword.class);
	private String name;
	private double relevance;
	
	private ObjectId _id;
	
	public Keyword() {
		
	}

	public Keyword(String name, double relevance) {
		this.name = name;
		this.relevance = relevance;
	}
	
	public String getName() {
		return name;
	}
	
	
	public double getRelevance() {
		return relevance;
	}
	
	public String toString() {
		return "Name: " + name + "Score: " + relevance;
	}
	
	@Override
	protected Keyword clone() {
		Keyword clone = null;
		try {
			clone = (Keyword) super.clone();
		}
		catch(CloneNotSupportedException e) {
			logger.error("cloning of entity not supported {}", e.getMessage());
		}
		return clone;
	}

}
