package com.ibm.alchemy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AlchemyNews {

	@JsonProperty("result")
	private AlchemyNewsResult result = new AlchemyNewsResult();
	
	@JsonProperty("result")
	public AlchemyNewsResult getResult() {
		return result;
	}

	@JsonProperty("result")
	public void setResult(AlchemyNewsResult result){
		this.result = result;
	}
}
