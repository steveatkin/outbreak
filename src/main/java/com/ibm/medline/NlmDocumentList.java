package com.ibm.medline;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;  
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class NlmDocumentList<T> {

	@XmlAttribute(name = "num")
	private int num;
	
	@XmlAttribute(name = "start")
	private int start;
	
	@XmlAttribute(name = "per")
	private int per;
	
	@XmlElements({
        @XmlElement(name="document", type=NlmDocument.class)
    })
    public List<T> docs;
	
	public List<T> getDocs() {
		return docs;
	}
	
}
