package com.robcubed.wfpresource;

import com.sun.jersey.core.header.ContentDisposition;

public class WFPContents {
	private String fileContents;
	private boolean txtFile;
	private ContentDisposition fdcp;
	
	public String getFileContents() {
		return fileContents;
	}
	
	public void setFileContents(String fileContents) {
		this.fileContents = fileContents;
	}
	
	public boolean isTxtFile() {
		return txtFile;
	}
	
	public void setTxtFile(boolean txtFile) {
		this.txtFile = txtFile;
	}

	public void setFdcp(ContentDisposition fdcp) {
		this.fdcp = fdcp;
	}

	public ContentDisposition getFdcp() {
		return fdcp;
	}
	
	
}
