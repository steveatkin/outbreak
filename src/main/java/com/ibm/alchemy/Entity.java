package com.ibm.alchemy;


public class Entity {
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

}
