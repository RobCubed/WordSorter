package com.robcubed.wordsorter;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.robcubed.wordsorter.Word;
import com.sun.jersey.api.client.*;

@Produces(MediaType.TEXT_HTML)
@Path("/client")
public class ClientResource {
	private Client client;
	
	public ClientResource(Client client) {
		this.client = client;
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
}
