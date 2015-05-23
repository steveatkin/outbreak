package com.ibm.alchemy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Alert implements Cloneable {
	private static final Logger logger = LoggerFactory.getLogger(Alert.class);
	private String title;
	private Date date;
	private int hashcode;
	
	private List<Entity> healthConditions = new ArrayList<Entity>();
	private List<Entity> relatedLocations = new ArrayList<Entity>();
	private List<Concept> concepts = new ArrayList<Concept>();
	
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
	
	public List<Entity> getHealthConditions() {
		return healthConditions;
	}
	
	public List<Entity> getRelatedLocations() {
		return relatedLocations;
	}
	
	public void setHealthConditions(List<Entity> healthConditions) {
		this.healthConditions = healthConditions;
	}
	
	public void setRelatedLocations(List<Entity> relatedLocations) {
		this.relatedLocations = relatedLocations;
	}
	
	public List<Concept> getConcepts() {
		return concepts;
	}
	
	public void setConcepts(List<Concept> concepts) {
		this.concepts = concepts;
	}
	
	public String toString() {
		return title;
	}
	
	@Override
	public Alert clone() {
		Alert clone = null;
		try {
			clone = (Alert) super.clone();
		}
		catch(CloneNotSupportedException e) {
			logger.error("cloning of alert not supported {}", e.getMessage());
		}
		return clone;
	}
}
