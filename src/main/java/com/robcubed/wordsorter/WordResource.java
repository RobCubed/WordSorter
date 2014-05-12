package com.robcubed.wordsorter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.sound.sampled.spi.FormatConversionProvider;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyReader;

import com.fasterxml.jackson.databind.ser.SerializerCache.TypeKey;
import com.google.common.io.ByteStreams;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;

import org.apache.commons.io.IOUtils;
import org.skife.jdbi.v2.DBI;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.core.header.FormDataContentDisposition;


@Path("/words")
@Produces(MediaType.APPLICATION_JSON)
public class WordResource {
	private final WordDAO wordDao;
	private final Validator validator;
	
	private static final String SERVER_UPLOAD_LOCATION_FOLDER = "C://Users/rob/Desktop/Upload_Files/";
	
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
	
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(FormDataMultiPart multiPart, @FormDataParam("combine_files") String combineFiles,
			@FormDataParam("no_duplicates") String noDuplicates, @FormDataParam("sorting") String sorting) throws IOException {
		// Use FormDataMultiPart to load multiple fields, even if they have identical names. (Useful for unknown number of files)
		
		boolean hasErrors = false;
		//ArrayList<List<String>> allLines = new ArrayList<>();
		HashMap<String, List<String>> finalFiles = new HashMap<>();
		HashMap<String, Integer> lineCounts = new HashMap<>();
		
		// Process files
		List<FormDataBodyPart> fields = multiPart.getFields("file"); 
		for (FormDataBodyPart field : fields) {
			InputStream file = field.getValueAs(InputStream.class); // convert to InputStream for processing
			ContentDisposition fdcp = field.type(MediaType.TEXT_PLAIN_TYPE).getContentDisposition(); // Grab the file's content disposition
			StringWriter writer = new StringWriter();
			IOUtils.copy(file, writer); // Apache Commons to read the InputString
			String fileContents = writer.toString();
			
			// TODO: Check to make sure longest line is 512 characters. If it's longer, add filename + line number + error to a map. Set hasErrors to true. 
			
			if (!fdcp.getFileName().endsWith("txt")) { // Verify it's a .txt
				return Response.status(Status.BAD_REQUEST).entity("All files must be in .txt format").build();
			} else {
				System.out.println(fdcp.getFileName());
			}
			
			// Let's add all lines to a simple list to start with.
			String[] lines = fileContents.split(System.getProperty("line.separator"));
			List<String> linesList = Arrays.asList(lines);			
			//allLines.add(linesList);
			//System.out.println(file.toString());
			//System.out.println(fileContents);
			lineCounts.put(fdcp.getFileName(), linesList.size());
			finalFiles.put(fdcp.getFileName(), linesList);
		}
				

		
		// if sorting, sort each file.
		
		// if combine_files, combine all into one list and return one file
	    // otherwise keep separate files..
		if (combineFiles != null && !combineFiles.isEmpty()) {
			List<String> tempList = new ArrayList<String>();
			for (Entry<String, List<String>> entry : finalFiles.entrySet()) {
				List<String> listString = entry.getValue();
				tempList.addAll(listString);
			}
			finalFiles.clear();
			finalFiles.put("combined.txt", tempList);
		}
		
		
		// Remove whitespace lines from each set, remove duplicates, and sort.
		for (Entry<String, List<String>> entry : finalFiles.entrySet()) {
			ArrayList<String> tempArray = new ArrayList<>();
			tempArray.addAll(entry.getValue());
			
			if (noDuplicates != null && !noDuplicates.isEmpty()) { // drop it into a hashset to clear duplicates...
				HashSet tempHash = new HashSet(); 
				tempHash.addAll(tempArray);
				tempArray.clear();
				tempArray.addAll(tempHash);
			}
			
			for (Iterator<String> itr = tempArray.iterator();itr.hasNext();) {
				String element = itr.next();
				if(!element.matches(".*\\w.*")) {
					itr.remove();
				}
			}
			
			if (sorting != null && !sorting.isEmpty()) {
				if (sorting.equals("a_to_z")) {
					Collections.sort(tempArray);
				} else if (sorting.equals("z_to_a")) {
					Collections.sort(tempArray); // sort it first a-z..
					Collections.reverse(tempArray); // now reverse it
				} else if (sorting.equals("randomize")) {
					long seed = System.nanoTime();
					Collections.shuffle(tempArray, new Random(seed));					
				}
			}
			
			List<String> tempList = tempArray; 
			finalFiles.put(entry.getKey(), tempList);			
		}
		
		// if remove_duplicates, remote any duplicates PER FILE, regardless of combine_files.
		

		
		
		/* FOR TESTING
		System.out.println("All Lines: ");
		
		for(String line : allLines) { 
			System.out.println(line);
		}*/
		System.out.println();
		
		System.out.println("Individual file sizes before removing whitespace: ");
		System.out.println(lineCounts.toString());
		System.out.println();
		
		System.out.println(finalFiles.toString());
		// END FOR TESTING

		
		
		
		return Response.status(200).entity("Ok").build();
				
	}
	
	private void saveFile(InputStream uploadedInputStream, String serverLocation) {
		try {
			OutputStream outputStream = new FileOutputStream(new File(serverLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			
			outputStream = new FileOutputStream(new File(serverLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
