package com.robcubed.wordsorter;

public class WordResults {
	private String word;
	private int timesUsed;
	
	WordResults(String word, int timesUsed) {
		this.word = word;
		this.timesUsed = timesUsed;
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getTimesUsed() {
		return timesUsed;
	}
	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}
	
	@Override
	public String toString() {
		String returnString = word + " - " + timesUsed;
		return returnString;
	}
	
	
}
