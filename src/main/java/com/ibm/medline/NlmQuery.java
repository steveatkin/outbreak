package com.ibm.medline;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NlmQuery {
	private static final Logger logger = LoggerFactory.getLogger(NlmQuery.class);
	
	public NlmSearchResult getSearchResult(String term) {
		NlmSearchResult results = new NlmSearchResult();
		
		RequestConfig requestConfig = RequestConfig.custom().
				setConnectTimeout(30 * 1000).build();
		HttpClient httpClient = HttpClientBuilder.create().
				setDefaultRequestConfig(requestConfig).build();
		
		try {
			URIBuilder builder = new URIBuilder();
			builder.setScheme("http").setHost("wsearch.nlm.nih.gov").setPath("/ws/query")
				.setParameter("db", "healthTopics")
				.setParameter("term", term)
				.setParameter("retmax", "1");
			URI uri = builder.build();
			
			HttpGet httpGet = new HttpGet(uri);
			
			httpGet.setHeader("Content-Type", "text/plain");
			HttpResponse httpResponse = httpClient.execute(httpGet);
			
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				BufferedReader rd = new BufferedReader(
				        new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				
				JAXBContext jaxbContext = JAXBContext.newInstance(NlmSearchResult.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				results = (NlmSearchResult) jaxbUnmarshaller.unmarshal(rd);
			}
			else {
				logger.error("could not get terms from Medline {}", httpResponse.getStatusLine().getStatusCode());
			}
			
		}
		catch(Exception e) {
			logger.error("Medline error: {}", e.getMessage());
		}
		
		return results;
	}

}
