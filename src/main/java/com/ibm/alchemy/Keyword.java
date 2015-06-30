package com.ibm.alchemy;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Keyword implements Cloneable {
	private static final Logger logger = LoggerFactory.getLogger(Keyword.class);
	private String name;
	private String type;
	private double relevance;
	
	private ObjectId _id;
	
	public Keyword() {
		
	}
	
	public Keyword(String name, String type) {
		this(name, 1.0, type);
	}
	
	public Keyword(String name, double relevance) {
		this(name, relevance, "");
	}

	public Keyword(String name, double relevance, String type) {
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
	
	public void setType(String type) {
		this.type = type;
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
	
	@Override
	public int hashCode() {
	    return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		boolean retVal = false;
		
		if(o instanceof Keyword){
			Keyword toCompare = (Keyword) o;
		    retVal = this.name.equalsIgnoreCase(toCompare.name);
		 }
		 return retVal;
	}

}
