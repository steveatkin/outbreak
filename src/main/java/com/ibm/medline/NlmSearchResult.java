package com.ibm.medline;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType; 
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "nlmSearchResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class NlmSearchResult {
	
	@XmlElement(name = "term")
	private String term = "";
	
	@XmlElement(name = "file")
	private String file = "";
	
	@XmlElement(name = "server")
	private String server = "";
	
	@XmlElement(name = "count")
	private int count;
	
	@XmlElement(name = "retstart")
	private int retstart;
	
	@XmlElement(name = "retmax")
	private int retmax;
	
	@XmlElementWrapper(name = "list")
	@XmlElement(name = "document")
	private List<NlmDocument> nlmList;
	
	public List<NlmDocument> getDocuments() {
		return nlmList;
	}
	
	public int getCount() {
		return count;
	}
	
	public String getServer() {
		return server;
	}

}
