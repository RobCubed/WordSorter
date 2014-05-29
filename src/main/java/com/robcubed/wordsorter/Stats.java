package com.robcubed.wordsorter;

import java.util.List;

class Stats {
	private final int id;
	
	private final List<WordResults> topTen;
	private final List<WordResults> bottomTen;
	private final int totalUnique;
	
	public Stats(List<WordResults> topTen, List<WordResults> bottomTen, int totalUnique) {
		this.id = 0;
		this.topTen = topTen;
		this.bottomTen = bottomTen;
		this.totalUnique = totalUnique;
	}


	public List<WordResults> getTopTen() {
		return topTen;
	}



	public List<WordResults> getBottomTen() {
		return bottomTen;
	}




	public int getTotalUnique() {
		return totalUnique;
	}



	public int getId() {
		return id;
	}
	
	
}
