package com.robcubed.wordsorter;

import io.dropwizard.views.View;

public class StatsView extends View {
	private final Stats stats;
	private final Tracking tracking;
	
	public StatsView(Stats stats, Tracking tracking) {
		super("stats.ftl");
		this.stats = stats;
		this.tracking = tracking;
		System.out.println("Stats view: " + stats.getTotalUnique());
	}
	
	public Stats getStats() {
		return stats;
	}
	
	public Tracking getTracking() {
		return tracking;
	}
}
