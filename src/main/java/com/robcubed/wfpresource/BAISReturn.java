package com.robcubed.wfpresource;

import java.io.ByteArrayInputStream;

public class BAISReturn {
	private ByteArrayInputStream bais;
	private String errorMessage = null;
	
	public ByteArrayInputStream getBais() {
		return bais;
	}
	
	public void setBais(ByteArrayInputStream bais) {
		this.bais = bais;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
