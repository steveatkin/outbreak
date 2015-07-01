package com.ibm.alchemy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AlchemyNewsSource {
	@JsonProperty("enriched")
	private AlchemyNewsEnriched enriched = new AlchemyNewsEnriched();
	
	@JsonProperty("enriched")
	public AlchemyNewsEnriched getEnriched() {
		return enriched;
	}

	@JsonProperty("enriched")
	public void setEnriched(AlchemyNewsEnriched enriched){
		this.enriched = enriched;
	}
}
