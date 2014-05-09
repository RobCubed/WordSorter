package com.robcubed.wordsorter;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.robcubed.wordsorter.Word;

public interface WordDAO {	
	@Mapper(WordMapper.class)
	@SqlQuery("select * from word where id = :id")
	Word getWordById(@Bind("id") int id);
	
	@GetGeneratedKeys
	@SqlUpdate("insert into word (id, word) values (NULL, :word) on duplicate key update timesUsed = timesUsed + 1")
	int createWord(@Bind("word") String word);

	@Mapper(WordMapper.class)
	@SqlQuery("select * from word where word = :word")
	Word getWordByWord(@Bind("word") String word);
	
}