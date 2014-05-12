package com.robcubed.wordsorter;

import com.robcubed.wordsorter.Word;

import io.dropwizard.views.View;

public class WordView extends View {
	private final Word word;
	
	public WordView(Word word) {
		super("word.ftl");
		this.word = word;
	}
	
	public Word getWord() {
		return word;
	}
}
