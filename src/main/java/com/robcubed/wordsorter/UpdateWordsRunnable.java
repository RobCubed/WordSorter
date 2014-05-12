package com.robcubed.wordsorter;



public class UpdateWordsRunnable implements Runnable {
	private volatile boolean cancelled = false;
	private Thread t;
	private String threadName;
	private WordDAO dao;
	
	@Override
	public void run() {
		System.out.println("Word DB Updating Thread Started");
		while (cancelled == false) {
			try {
				System.out.println("Going");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public UpdateWordsRunnable(String name, WordDAO dao) {
		// TODO Auto-generated constructor stub
		threadName = name;
		this.dao = dao;
	}
	
	public void cancel() {
		cancelled = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void start() {
		System.out.println("Starting thread.");
		if (t == null) {
			t = new Thread (this, threadName);
			t.start();
		}
	}
}
