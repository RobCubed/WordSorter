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
	
	@Mapper(WordResultsMapper.class)
	@SqlQuery("SELECT word, timesUsed FROM word ORDER BY timesUsed DESC LIMIT 0, 10")
	List<WordResults> getTopTen();
	
	@Mapper(WordResultsMapper.class)
	@SqlQuery("SELECT word, timesUsed FROM word ORDER BY timesUsed asc LIMIT 0, 10")
	List<WordResults> getBottomTen();
	
	@SqlQuery("SELECT COUNT(*) FROM word")
	int getWordCount();
	
	@Mapper(TrackingMapper.class)
	@SqlQuery("SELECT * FROM tracking WHERE idtracking = 1")
	Tracking getTracking();
	
	@SqlUpdate("UPDATE tracking SET totalLinesSubmitted = :totalLinesSubmitted WHERE idtracking = 1")
	void updateTotalLines(@Bind("totalLinesSubmitted") int totalLinesSubmitted);
	
	@SqlUpdate("UPDATE tracking SET totalFilesSubmitted = :totalFilesSubmitted WHERE idtracking = 1")
	void updateTotalFiles(@Bind("totalFilesSubmitted") int totalFilesSubmitted);
	
	@SqlUpdate("UPDATE tracking SET averageLinesSubmitted = :averageLinesSubmitted WHERE idtracking = 1")
	void updateAverageLines(@Bind("averageLinesSubmitted") int averageLinesSubmitted);
	
	@SqlUpdate("UPDATE tracking SET mostLinesSubmitted = :mostLinesSubmitted WHERE idtracking = 1")
	void updateMostLines(@Bind("mostLinesSubmitted") int mostLinesSubmitted);
	
}