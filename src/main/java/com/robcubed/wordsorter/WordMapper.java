package com.robcubed.wordsorter;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import com.robcubed.wordsorter.Word;

class WordMapper implements ResultSetMapper<Word> {
	public Word map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new Word(r.getInt("id"), r.getString("word"), r.getInt("timesUsed"));
	}
}
