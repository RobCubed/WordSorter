package com.robcubed.wordsorter;


import io.dropwizard.views.View;

public class WordView extends View {
	private final Word word;
	
	WordView(Word word) {
		super("word.ftl");
		this.word = word;
	}
	
	public Word getWord() {
		return word;
	}
}
