package com.ibm;

import java.io.IOException;
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
		
		ObjectMapper mapper = new ObjectMapper();
		String listContents = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(news);
		
		return Response.ok(listContents).build();
	}
}
