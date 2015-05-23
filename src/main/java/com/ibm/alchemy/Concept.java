package com.ibm.alchemy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Concept implements Cloneable {
	private static final Logger logger = LoggerFactory.getLogger(Concept.class);
	private String concept = "";
	private double relevance;


	public String getConcept() {
		return concept;
	}
	
	public void setConcept(String c) {
		concept = c;
	}
	
	public double getRelevance() {
		return relevance;
	}
	

	public void setRelevance(double d) {
		relevance = d;
	}
	
	public String toString() {
		return "ConceptT: " + concept + " Score: " + Double.toString(relevance); 
	}
	
	@Override
	protected Concept clone() {
		Concept clone = null;
		try {
			clone = (Concept) super.clone();
		}
		catch(CloneNotSupportedException e) {
			logger.error("cloning of concept not supported {}", e.getMessage());
		}
		return clone;
	}
}
