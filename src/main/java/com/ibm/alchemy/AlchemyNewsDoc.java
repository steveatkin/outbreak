package com.ibm.alchemy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AlchemyNewsDoc {
	@JsonProperty("id")
	private String id = "";
	
	@JsonProperty("source")
	private AlchemyNewsSource source = new AlchemyNewsSource();
	
	@JsonProperty("timestamp")
	private long timestamp = 0;
	
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id){
		this.id = id;
	}
	
	@JsonProperty("timestamp")
	public long getTimestamp() {
		return timestamp;
	}

	@JsonProperty("timestamp")
	public void setTimestamp(long timestamp){
		this.timestamp = timestamp;
	}
	
	@JsonProperty("source")
	public AlchemyNewsSource getSource() {
		return source;
	}

	@JsonProperty("source")
	public void setSource(AlchemyNewsSource source){
		this.source = source;
	}

}
