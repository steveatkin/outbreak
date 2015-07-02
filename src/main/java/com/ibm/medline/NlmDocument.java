package com.ibm.medline;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType; 
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "document")
@XmlAccessorType(XmlAccessType.FIELD)
public class NlmDocument {
	
	@XmlAttribute(name = "rank")
	private int rank;
	
	@XmlAttribute(name = "url")
	private String url;

	@XmlElement(name = "content")
	private ArrayList<NlmContent> contents;
	
	public ArrayList<NlmContent> getContents() {
		return contents;
	}
	
	public int getRank() {
		return rank;
	}
	
	public String getUrl() {
		return url;
	}
}
