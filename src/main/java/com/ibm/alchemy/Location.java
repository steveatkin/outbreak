package com.ibm.alchemy;

import org.bson.types.ObjectId;

public class Location {
	private ObjectId _id;
	private String name;

	public Location() {
		
	}
	
	public Location(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
