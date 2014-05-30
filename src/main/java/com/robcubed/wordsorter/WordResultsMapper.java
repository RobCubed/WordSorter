package com.robcubed.wordsorter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class WordResultsMapper  implements ResultSetMapper<WordResults> {
	public WordResults map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new WordResults(r.getString("word"), r.getInt("timesUsed"));
	}	

}
