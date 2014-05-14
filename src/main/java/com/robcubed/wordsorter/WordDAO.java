package com.robcubed.wordsorter;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.robcubed.wordsorter.Word;

public interface WordDAO {	
	@Mapper(WordMapper.class)
	@SqlQuery("select * from word where id = :id")
	Word getWordById(@Bind("id") int id);
	
	@GetGeneratedKeys
	int createWord(@Bind("word") String word);
	
	@SqlBatch("insert into word (id, word) values (NULL, :word) on duplicate key update timesUsed = timesUsed + 1")
	@BatchChunkSize(1000)
	void batchWords(@Bind("word") List<String> word);

	@Mapper(WordMapper.class)
	@SqlQuery("select * from word where word = :word")
	Word getWordByWord(@Bind("word") String word);
}