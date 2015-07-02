package com.ibm.medline;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

@XmlRootElement(name = "content")
@XmlAccessorType(XmlAccessType.FIELD)
public class NlmContent {
	
	@XmlAttribute(name ="name")
	private String name;
	
	@XmlValue
    private String value;
	
	private static String cleanTagPerservingLineBreaks(String html) {
	    String result = "";
	    if (html == null)
	        return html;
	    Document document = Jsoup.parse(html);
	    document.outputSettings(new Document.OutputSettings()
	            .prettyPrint(false));// makes html() preserve linebreaks and
	                                        // spacing
	    document.select("br").append("\\n");
	    document.select("p").prepend("\\n\\n");
	    result = document.html().replaceAll("\\\\n", "\n");
	    result = Jsoup.clean(result, "", Whitelist.none(),
	            new Document.OutputSettings().prettyPrint(false));
	    return result;
	}
	
	private static String unescapeHTML(String str) {
	    return StringEscapeUtils.unescapeHtml(str);
	}
	
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return unescapeHTML(cleanTagPerservingLineBreaks(value));
	}
}
