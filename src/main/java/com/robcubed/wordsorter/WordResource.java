package com.robcubed.wordsorter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.MessageBodyReader;

import com.fasterxml.jackson.databind.ser.SerializerCache.TypeKey;
import com.google.common.io.ByteStreams;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.skife.jdbi.v2.DBI;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.core.header.FormDataContentDisposition;


@Path("/words")
@Produces(MediaType.APPLICATION_JSON)
public class WordResource {
	private final WordDAO wordDao;
	private final Validator validator;
	
	//private static final String SERVER_UPLOAD_LOCATION_FOLDER = "C://Users/rob/Desktop/Upload_Files/";
	
	private final String saveLocation;
	
	public WordResource(DBI jdbi, Validator validator, String saveLocation) {
		wordDao = jdbi.onDemand(WordDAO.class);
		this.validator = validator;
		this.saveLocation = saveLocation;
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
			IOUtils.copy(file, writer, "UTF-8"); // Apache Commons to read the InputString
			String fileContents = writer.toString();
			
			// TODO: Check to make sure longest line is 512 characters. If it's longer, add filename + line number + error to a map. Set hasErrors to true. 
			
			if (!fdcp.getFileName().endsWith("txt")) { // Verify it's a .txt
				//throw new WebApplicationException();
				return Response.status(Status.BAD_REQUEST).entity("All files must be in .txt format - " + fdcp.getFileName()).build();
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
			
			// Drop it into a hashset to remove any duplicates...
			if (noDuplicates != null && !noDuplicates.isEmpty()) { 
				HashSet tempHash = new HashSet(); 
				tempHash.addAll(tempArray);
				tempArray.clear();
				tempArray.addAll(tempHash);
			}
			
			// Clear empty lines
			for (Iterator<String> itr = tempArray.iterator();itr.hasNext();) {
				String element = itr.next();
				if(StringUtils.isBlank(element) || StringUtils.isEmpty(element)) { // Checking for blank or empty lines
					itr.remove(); // remove them
				}
			}
			
			// Sorting/randomization
			if (sorting != null && !sorting.isEmpty()) {
				if (sorting.equals("a_to_z")) {
					Collections.sort(tempArray, String.CASE_INSENSITIVE_ORDER); // Need to ignore case for sorting.
				} else if (sorting.equals("z_to_a")) {
					Collections.sort(tempArray, String.CASE_INSENSITIVE_ORDER); // Sort it first a-z..
					Collections.reverse(tempArray); // Now reverse it
				} else if (sorting.equals("randomize")) {
					long seed = System.nanoTime();
					Collections.shuffle(tempArray, new Random(seed));
				}
			}
			
			List<String> tempList = tempArray; 
			finalFiles.put(entry.getKey(), tempList); // Replace the original List<String> with the new sorted/cleaned one
		}
		
		

		
		
		/* FOR TESTING
		System.out.println("All Lines: ");
		
		for(String line : allLines) { 
			System.out.println(line);
		}*/

		byte[] buffer = new byte[1024];
		long seedZip = System.nanoTime();
		
		String zipName = "/home/javadev/pproj/WordSorter/" + seedZip + "-zipReturn.zip";
		
		try {
			FileOutputStream fos = new FileOutputStream(zipName);
			ZipOutputStream zos = new ZipOutputStream(fos);
		
			for (Entry<String, List<String>> entry : finalFiles.entrySet()) {
				//System.out.println(entry.getKey());
				long seed = System.nanoTime();
				String fileName = "finished-" + entry.getKey();
				File file = new File("/home/javadev/pproj/WordSorter/" + seed + "-" + fileName);
				
				if (!file.exists()) {
					file.createNewFile();
				}
				
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				
				BufferedWriter bw = new BufferedWriter(fw);
				
				for (String line : entry.getValue()) {
					bw.write(line);
					bw.write(System.lineSeparator());
				}
				bw.close();			
				fw.close();
				
				ZipEntry ze = new ZipEntry(file.getName());
				zos.putNextEntry(ze);
				FileInputStream in = new FileInputStream(file.getAbsoluteFile());
				
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				in.close();
				zos.closeEntry();
			}
			zos.close();
		} catch(IOException ex){
	    	   ex.printStackTrace();
	    }		
		
		File returnFile = new File(zipName);
		return Response.ok(returnFile, MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=\"" + returnFile.getName()
				+ "\"" ).build();
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
