package com.robcubed.wordsorter;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.skife.jdbi.v2.DBI;

import com.robcubed.wordsorter.Word;
import com.sun.jersey.api.client.*;

@Produces(MediaType.TEXT_HTML)
@Path("/")
public class ClientResource {
	private final WordDAO wordDao;
	private Client client;
	
	public ClientResource(Client client, DBI jdbi) {
		this.client = client;
		this.wordDao = jdbi.onDemand(WordDAO.class);
	}
	

	@GET
	public WordView index() {
		Word w = new Word();
		return new WordView(w);
	}
	
	@GET
	@Path("showWord")
	public WordView showWord(@QueryParam("word") String word) {
		WebResource wordResource = client.resource("http://localhost:7050/words/" + word);
		Word w = wordResource.get(Word.class);
		return new WordView(w);
	}
	
	@GET
	@Path("newWord")
	public Response newWord(@QueryParam("word") String word) {
		WebResource wordResource = client.resource("http://localhost:7050/words/");
		ClientResponse response = wordResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, new Word(0, word, 1));
		if (response.getStatus() == 201) {
			// Created
			return Response.status(302).entity("Word successfully added. Can be found at " + response.getHeaders().getFirst("Location")).build();
		} else {
			return Response.status(422).entity(response.getEntity(String.class)).build();
		}
	}
	
	@GET
	@Path("stats")
	public StatsView statsView() {
		
		List<WordResults> topTen = wordDao.getTopTen();		
		List<WordResults> bottomTen = wordDao.getBottomTen();
		
		int totalUnique = wordDao.getWordCount();
		
		Stats s = new Stats(topTen, bottomTen, totalUnique);
		System.out.println(s.getTopTen().toString());
		System.out.println(s.getBottomTen().toString());
		System.out.println(s.getTotalUnique());

		Tracking t = wordDao.getTracking();
		return new StatsView(s, t);
	}
}
