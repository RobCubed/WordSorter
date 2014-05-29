package com.robcubed.wordsorter;

class Tracking {
	
	private int idtracking;
	private int totalLinesSubmitted;
	private int totalFilesSubmitted;
	private int averageLinesSubmitted;
	private int mostLinesSubmitted;
	
	Tracking(int idtracking, int totalLinesSubmitted, int totalFilesSubmitted, int averageLinesSubmitted, int mostLinesSubmitted) {
		this.idtracking = idtracking;
		this.totalLinesSubmitted = totalLinesSubmitted;
		this.totalFilesSubmitted = totalFilesSubmitted;
		this.averageLinesSubmitted = averageLinesSubmitted;
		this.mostLinesSubmitted = mostLinesSubmitted;
	}
	
	public int getIdtracking() {
		return idtracking;
	}
	public void setIdtracking(int idtracking) {
		this.idtracking = idtracking;
	}
	public int getTotalLinesSubmitted() {
		return totalLinesSubmitted;
	}
	public void setTotalLinesSubmitted(int totalLinesSubmitted) {
		this.totalLinesSubmitted = totalLinesSubmitted;
	}
	public int getTotalFilesSubmitted() {
		return totalFilesSubmitted;
	}
	public void setTotalFilesSubmitted(int totalFilesSubmitted) {
		this.totalFilesSubmitted = totalFilesSubmitted;
	}
	public int getAverageLinesSubmitted() {
		return averageLinesSubmitted;
	}
	public void setAverageLinesSubmitted(int averageLinesSubmitted) {
		this.averageLinesSubmitted = averageLinesSubmitted;
	}
	public int getMostLinesSubmitted() {
		return mostLinesSubmitted;
	}
	public void setMostLinesSubmitted(int mostLinesSubmitted) {
		this.mostLinesSubmitted = mostLinesSubmitted;
	}
	
	
		
}
