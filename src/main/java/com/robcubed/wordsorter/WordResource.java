package com.robcubed.wordsorter;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.skife.jdbi.v2.DBI;

import com.sun.jersey.core.header.ContentDisposition;


@Path("/words")
@Produces(MediaType.APPLICATION_JSON)
public class WordResource {
	private final WordDAO wordDao;
	private final Validator validator;
	
	
	public WordResource(DBI jdbi, Validator validator) {
		wordDao = jdbi.onDemand(WordDAO.class);
		this.validator = validator;
	}
	
	@GET
	@Path("/{word}")
	public Response getWord(@PathParam("word") String wordSearch) {
		Word word = wordDao.getWordByWord(wordSearch);
		return Response.ok(word).build();
	}
	
	@POST
	public Response createWord(Word word) throws URISyntaxException {
		Set<ConstraintViolation<Word>> violations = validator.validate(word);
		if (violations.size() > 0) {
			ArrayList<String> validationMessages = new ArrayList<String>();
			for (ConstraintViolation<Word> violation : violations) {
				validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
			}
			return Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
		} else {
			int newWordId = wordDao.createWord(word.getWord().toLowerCase());
			return Response.created(new URI(String.valueOf(newWordId))).build();			
		}
	}
	
	//@Produces("application/x-zip-compressed")
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(FormDataMultiPart multiPart, @FormDataParam("combine_files") String combineFiles,
			@FormDataParam("no_duplicates") String noDuplicates, @FormDataParam("sorting") String sorting) throws IOException {
		WordFileProcessor wfp = new WordFileProcessor();
		
		ByteArrayInputStream wfpResult = wfp.processFile(multiPart.getFields("file"), combineFiles, noDuplicates, sorting, wordDao);
		
		return Response.ok(wfpResult, MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=\"wordlists.zip\"" ).build();
	}
		
	
}
