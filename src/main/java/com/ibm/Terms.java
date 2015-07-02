package com.ibm;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.medline.NlmContent;
import com.ibm.medline.NlmDocument;
import com.ibm.medline.NlmQuery;
import com.ibm.medline.NlmSearchResult;

@Path("/terms")
public class Terms {
	NlmQuery query = null;
	
	public Terms() {
		query = new NlmQuery();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getResults(@QueryParam("condition") String condition) throws IOException {
		NlmQuery query = new NlmQuery();
		NlmContent answer = new NlmContent();
		
		NlmSearchResult results = query.getSearchResult(condition);
		
		List<NlmDocument> docs = results.getDocuments();
		
		if(!docs.isEmpty()) {
			// Just use the first answer
			List <NlmContent> contents = docs.get(0).getContents();
			Iterator<NlmContent> itr = contents.iterator();
			
			while(itr.hasNext()) {
				NlmContent content = (NlmContent) itr.next();
				
				if(content.getName().equalsIgnoreCase("FullSummary")) {
					answer = content;
					break;
				}
			}
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		String listContents = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(answer);
		
		return Response.ok(listContents).build();
	}
}
