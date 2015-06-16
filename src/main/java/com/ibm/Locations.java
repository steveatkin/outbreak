package com.ibm;

import java.io.IOException;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.alchemy.Alert;
import com.ibm.alchemy.Location;

@Path("/locations")
public class Locations {
	private MongoCollection countries;
	
	public Locations() {
		countries = MongoConnection.getLocationsCollection();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getCountries() throws IOException {
		Iterator<Location> iterator;
		MongoCursor<Location> all;
		
		
		all = countries.find().as(Location.class);
		iterator = all.iterator();
		
		ObjectMapper mapper = new ObjectMapper();
		//String listContents = mapper.writeValueAsString(alerts);
		String listContents = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(iterator);
		
		return Response.ok(listContents).build();
	}
}
