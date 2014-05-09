package com.robcubed.wordsorter;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class Word {
	private final int id;
	
	@NotBlank
	@Length(min=1, max=255)
	private final String word;
	
	private final int timesUsed;
	
	public Word() {
		this.id = 0;
		this.word = null;
		this.timesUsed = 0;
	}
	
	public Word(int id, String word, int timesUsed) {
		this.id = id;
		this.word = word;
		this.timesUsed = timesUsed;
	}

	public int getId() {
		return id;
	}

	public String getWord() {
		return word;
	}

	public int getTimesUsed() {
		return timesUsed;
	}
	
	
	
}
