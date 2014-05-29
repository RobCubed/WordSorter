package com.robcubed.wordsorter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

class TrackingMapper implements ResultSetMapper<Tracking> {
	public Tracking map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new Tracking(r.getInt("idtracking"), r.getInt("totalLinesSubmitted"), r.getInt("totalFilesSubmitted"), r.getInt("averageLinesSubmitted"), r.getInt("mostLinesSubmitted"));
	}	

}
