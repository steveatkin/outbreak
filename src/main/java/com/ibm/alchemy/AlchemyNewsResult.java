package com.ibm.alchemy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AlchemyNewsResult {
	@JsonProperty("docs")
	private AlchemyNewsDoc[] docs = new AlchemyNewsDoc[0];
	
	@JsonProperty("docs")
	public AlchemyNewsDoc[] getDocs() {
		return docs;
	}

	@JsonProperty("docs")
	public void setDocs(AlchemyNewsDoc[] docs){
		this.docs = docs;
	}
}
