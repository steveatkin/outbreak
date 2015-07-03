package com.ibm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.alchemy.Alchemy;
import com.ibm.alchemy.AlchemyNewsDoc;
import com.ibm.alchemy.AlchemyNewsURL;

@Path("/news")
public class News {
	private Alchemy alchemy;
	
	public News() {
		alchemy = new Alchemy();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getNews(@QueryParam("condition") String condition, @QueryParam("location") String location) throws IOException {
		List<AlchemyNewsDoc> news = alchemy.getNewsItems(condition, location);
		ArrayList<AlchemyNewsURL> newsUrls = new ArrayList<AlchemyNewsURL>();
		
		Iterator<AlchemyNewsDoc> itr = news.iterator();
		
		while(itr.hasNext()) {
			AlchemyNewsURL url = ((AlchemyNewsDoc)itr.next()).getSource().getEnriched().getURL();
			newsUrls.add(url);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String listContents = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newsUrls);
		
		return Response.ok(listContents).build();
	}
}
