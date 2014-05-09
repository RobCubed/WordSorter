package com.robcubed.wordsorter;

import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class App extends Application<WordSorterConfiguration> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	@Override
	public void initialize(Bootstrap<WordSorterConfiguration> b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(WordSorterConfiguration c, Environment e) throws Exception {
		LOGGER.info("Method App#run() called");
		for (int i =0; i < c.getMessageRepetitions(); i++) {
			System.out.println(c.getMessage());
		}
		System.out.println(c.getAdditionalMessage());
		
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(e, c.getDataSourceFactory(), "mysql");
		e.jersey().register(new WordResource(jdbi, e.getValidator()));
	}
	
	public static void main(String[] args) throws Exception {
		new App().run(args);
	}

}