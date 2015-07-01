package com.ibm.alchemy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AlchemyNewsEnriched {
	@JsonProperty("url")
	private AlchemyNewsURL url = new AlchemyNewsURL();
	
	@JsonProperty("url")
	public AlchemyNewsURL getURL() {
		return url;
	}

	@JsonProperty("url")
	public void setURL(AlchemyNewsURL url){
		this.url = url;
	}

}
