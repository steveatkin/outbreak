package com.ibm.alchemy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Alert {
	private String title;
	private Date date;
	private int hashcode;
	
	private List<Entity> healthConditions = new ArrayList<Entity>();
	private List<Entity> relatedLocations = new ArrayList<Entity>();
	
	public int getHashCode() {
		return hashcode;
	}
	
	public void setHashCode(int hash) {
		hashcode = hash;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void addHealthCondition(Entity condition) {
		healthConditions.add(condition);
	}
	
	public void addRelatedLocation(Entity location) {
		relatedLocations.add(location);
	}
	
	public List<Entity> getHealthConditions () {
		return healthConditions;
	}
	
	public List<Entity> getRelatedLocations() {
		return relatedLocations;
	}
	
	public String toString() {
		return title;
	}
}
