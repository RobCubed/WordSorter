package com.robcubed.wordsorter;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;

import com.robcubed.wfpresource.WFPContents;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

public class WordFileProcessor {
	private int totalLines = 0; // all lines submitted
	private int totalFiles = 0; // number of files
	private int maxLines = 0; // highest line count - will update if it beats the old line count!
	private HashMap<String, List<String>> finalFiles = new HashMap<>();
	private HashMap<String, Integer> lineCounts = new HashMap<>();
	private WordDAO wordDao;
	
	public ByteArrayInputStream processFile(List<FormDataBodyPart> fields, String combineFiles, String noDuplicates, String sorting, WordDAO wordDao) throws IOException {
		this.wordDao = wordDao;
		
		totalFiles = fields.size();
		
		if (totalFiles < 1) {
			// TODO : There were no files. Abort.
		}
		
		for (FormDataBodyPart field : fields) {
			// Pull in data 
			WFPContents fileContents = dataIn(field);
			
			if (!fileContents.isTxtFile()) {
				//  TODO : IT'S NOT A TEXT FILE! ABORT! ABORT!
			} else {
				// put into lists
				listCreate(fileContents);
				// TODO: check for any lines longer than 255...
			}			
		}		

		// combine files if needed
		if (combineFiles != null && !combineFiles.isEmpty()) {
			combineFiles(finalFiles.entrySet());
		}
		
		// deduplicate		
		if (noDuplicates != null && !noDuplicates.isEmpty()) { 
			removeDuplicates(finalFiles.entrySet());
		}
		
		// clear any empty lines
		clearEmptyLines(finalFiles.entrySet());
		
		// sort
		if (sorting != null && !sorting.isEmpty()) {
			sortFiles(finalFiles.entrySet(), sorting);
		}
		
		// update db
		processTracking();
		
		// make the file to return it		
		ByteArrayOutputStream baos = this.finalFile();
		
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	public ByteArrayOutputStream finalFile() {
		ByteArrayOutputStream baosx = new ByteArrayOutputStream();
		
		try {			
			ZipOutputStream zosx = new ZipOutputStream(baosx);
		
			for (Entry<String, List<String>> entry : finalFiles.entrySet()) {
				
				// Let's dump the database updates off to its own thread
				Thread t = new Thread(new WordsRunnable(entry.getValue(), wordDao));
				t.start();
								
				/// NEW
				byte[] bufferx = new byte[1024];
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				for (String line : entry.getValue()) {
					baos.write(line.replaceAll("(\\r|\\n)", "").getBytes());
					baos.write(System.lineSeparator().getBytes());
				}
				byte[] bytes = baos.toByteArray();
				
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				DataInputStream dis = new DataInputStream(bais);
				
				zosx.putNextEntry(new ZipEntry(entry.getKey()));
				int length;
				while ((length = dis.read(bufferx)) > 0 ) { 
					zosx.write(bufferx, 0, length);
				}
				zosx.closeEntry();
				baos.close();
				
				/// ENDNEW
			}
			zosx.close();
		} catch(IOException ex){
	    	   ex.printStackTrace();
	    }
		
		return baosx;
		
	}
	
	public void sortFiles(Set<Entry<String, List<String>>> entrySet, String sorting) {
		for (Entry<String, List<String>> entry : finalFiles.entrySet()) {
			ArrayList<String> tempArray = new ArrayList<>();
			tempArray.addAll(entry.getValue());

			if (sorting.equals("a_to_z")) {
				Collections.sort(tempArray, String.CASE_INSENSITIVE_ORDER); // Need to ignore case for sorting.
			} else if (sorting.equals("z_to_a")) {
				Collections.sort(tempArray, String.CASE_INSENSITIVE_ORDER); // Sort it first a-z..
				Collections.reverse(tempArray); // Now reverse it
			} else if (sorting.equals("randomize")) {
				long seed = System.nanoTime();
				Collections.shuffle(tempArray, new Random(seed));
			}
			
			List<String> tempList = tempArray; 
			finalFiles.put(entry.getKey(), tempList); // Replace the original List<String>			
		}
	}

	public void clearEmptyLines(Set<Entry<String, List<String>>> entrySet) {
		for (Entry<String, List<String>> entry : finalFiles.entrySet()) {
			ArrayList<String> tempArray = new ArrayList<>();
			tempArray.addAll(entry.getValue());
			
			for (ListIterator<String> itr = tempArray.listIterator(); itr.hasNext(); ) {
				String element = itr.next();
				if(StringUtils.isBlank(element) || StringUtils.isEmpty(element)) { // Checking for blank or empty lines
					itr.remove(); // remove them
				} else if (StringUtils.startsWith(element, " ") || StringUtils.endsWith(element, " ")) {
					element = element.trim();
				}
				
				if (element.contains("\t")) { // Get rid of all the tabs
					String tempString = element.replaceAll("\\t", "");
					if (tempString.length() > 0) {
						itr.set(tempString);
					}					
				}
			}
			
			List<String> tempList = tempArray; 
			finalFiles.put(entry.getKey(), tempList); // Replace the original List<String>			
		}
	}

	public void removeDuplicates(Set<Entry<String, List<String>>> entrySet) {
		// TODO Auto-generated method stub

		for (Entry<String, List<String>> entry : finalFiles.entrySet()) {
			ArrayList<String> tempArray = new ArrayList<>();
			tempArray.addAll(entry.getValue());
			HashSet<String> tempHash = new HashSet<String>(); 
			tempHash.addAll(tempArray);
			tempArray.clear();
			tempArray.addAll(tempHash);
			List<String> tempList = tempArray; 
			finalFiles.put(entry.getKey(), tempList);
		}
		
	}

	public void combineFiles(Set<Entry<String, List<String>>> entrySet) {
		List<String> tempList = new ArrayList<String>();
		for (Entry<String, List<String>> entry : entrySet) {
			List<String> listString = entry.getValue();
			tempList.addAll(listString);			
		}
		finalFiles.clear();
		finalFiles.put("combined.txt", tempList);		
	}

	public WFPContents dataIn(FormDataBodyPart field) throws IOException {
		WFPContents wfpContents = new WFPContents();
		InputStream file = field.getValueAs(InputStream.class);
		
		ContentDisposition fdcp = field.type(MediaType.TEXT_PLAIN_TYPE).getContentDisposition(); // Grab the file's content disposition
		
		if (!fdcp.getFileName().endsWith("txt")) { // Verify it's a .txt
			wfpContents.setTxtFile(false);
			return wfpContents;
		} else {
			wfpContents.setTxtFile(true);
			wfpContents.setFdcp(fdcp);
		}
		
		StringWriter writer = new StringWriter();
		IOUtils.copy(file, writer, "UTF-8"); // Apache Commons to read the InputString
		wfpContents.setFileContents(writer.toString());
		
		return wfpContents;
	}
	
	public void listCreate(WFPContents wfpContents) {
		String[] lines = wfpContents.getFileContents().split(System.getProperty("line.separator"));
		totalLines = totalLines + lines.length;
		if (lines.length > maxLines) {
			maxLines = lines.length;
		}
		List<String> linesList = Arrays.asList(lines);			
		lineCounts.put(wfpContents.getFdcp().getFileName(), linesList.size());
		finalFiles.put(wfpContents.getFdcp().getFileName(), linesList);		
	}
	
	public void processTracking() {
		
		// TODO - MAKE THIS THREADSAFE!!!
		Tracking tracking = wordDao.getTracking();
		
		if (maxLines > tracking.getMostLinesSubmitted()) {
			wordDao.updateMostLines(maxLines);
		}

		totalLines = totalLines + tracking.getTotalLinesSubmitted();
		totalFiles = totalFiles + tracking.getTotalFilesSubmitted();
		int averageLines = totalLines / totalFiles; // TODO: float
		
		wordDao.updateAverageLines(averageLines);
		wordDao.updateTotalFiles(totalFiles);
		wordDao.updateTotalLines(totalLines);
		// end TODO
		
	}
	
	
}
