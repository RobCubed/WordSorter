package com.robcubed.wordsorter;

import java.util.List;
import com.robcubed.wordsorter.WordDAO;

public class WordsRunnable implements Runnable {
	private List<String> words;
	private WordDAO wordDao;
	
	
	WordsRunnable(List<String> words, WordDAO wordDao) {
		this.words = words;
		this.wordDao = wordDao;
	}
	
	@Override
	public void run() {
		wordDao.batchWords(words);
	}

}
