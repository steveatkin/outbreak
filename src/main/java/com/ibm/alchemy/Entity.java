package com.ibm.alchemy;

import java.util.List;

public class Entity {
	private String name;
	private double score;
	private String type;
	private List<String> subTypes;

	public Entity(String name, double score, String type, List<String> subTypes) {
		this.name = name;
		this.score = score;
		this.type = type;
		this.subTypes = subTypes;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public List<String> getSubTypes() {
		return subTypes;
	}
	
	public double getScore() {
		return score;
	}
	
	public boolean containsSubType(String subType) {
		return subTypes.contains(subType);
	}
}
