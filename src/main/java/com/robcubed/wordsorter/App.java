package com.robcubed.wordsorter;

import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.*;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.dropwizard.assets.AssetsBundle;

public class App extends Application<WordSorterConfiguration> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	@Override
	public void initialize(Bootstrap<WordSorterConfiguration> b) {
		b.addBundle(new ViewBundle());
		b.addBundle(new AssetsBundle());
	}

	@Override
	public void run(WordSorterConfiguration c, Environment e) throws Exception {
		LOGGER.info("Method App#run() called");
		System.out.println(c.getSaveLocation());
		
		
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(e, c.getDataSourceFactory(), "mysql");
		
		e.jersey().register(new WordResource(jdbi, e.getValidator(), c.getSaveLocation()));
		
		final Client client = new JerseyClientBuilder(e).build("REST Client");
		e.jersey().register(new ClientResource(client, jdbi));
		
		
	}
	
	public static void main(String[] args) throws Exception {
		new App().run(args);
	}

}