package com.robcubed.wordsorter;


import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;


public class WordSorterConfiguration extends Configuration {
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();
		
	@JsonProperty
	private String saveLocation;
	
	public DataSourceFactory getDataSourceFactory() {
		return database;
	}
	
	public String getSaveLocation() {
		return saveLocation;
	}
	
}
