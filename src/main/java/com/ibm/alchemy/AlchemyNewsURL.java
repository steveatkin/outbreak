package com.ibm.alchemy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AlchemyNewsURL {
	@JsonProperty("cleanedTitle")
	private String cleanedTitle = "";
	
	@JsonProperty("title")
	private String title = "";
	
	@JsonProperty("url")
	private String url = "";
	
	@JsonProperty("author")
	private String author = "";
	
	@JsonProperty("text")
	private String text = "";
	
	
	@JsonProperty("cleanedTitle")
	public String getCleanedTitle() {
		return cleanedTitle;
	}

	@JsonProperty("cleanedTitle")
	public void setCleanedTitle(String title){
		this.cleanedTitle = title;
	}
	
	@JsonProperty("url")
	public String getURL() {
		return url;
	}

	@JsonProperty("url")
	public void setURL(String url){
		this.url = url;
	}
	
	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@JsonProperty("title")
	public void setTitle(String title){
		this.title = title;
	}
	
	@JsonProperty("author")
	public String getAuthor() {
		return author;
	}

	@JsonProperty("author")
	public void setAuthor(String author){
		this.author = author;
	}
	
	@JsonProperty("text")
	public String getText() {
		return author;
	}

	@JsonProperty("text")
	public void setText(String text){
		this.text = text;
	}

}
