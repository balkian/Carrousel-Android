package com.onirica.carrousel;

import java.util.ArrayList;

public class Match {
	public class Goal {
		public int min;
		public boolean isLocal;
		public String who;
		public Goal (int min, boolean isLocal, String who) {
			this.min = min;
			this.isLocal = isLocal;
			this.who = who;
		}
	}
	
	public enum State {
		MATCH_NO_STARTED,
		MATCH_FIRST_ROUND,
		MATCH_HALF_TIME,
		MATCH_SECOND_ROUND,
		MATCH_ENDED;
	}
	public String localTeam;
	public String visitorTeam;
	public State state;
	public int min;
	public String id;
	public ArrayList<Goal> goals;
	
	public Match(String id, String localTeam, String visitorTeam) {
		this.id = id;
		this.localTeam = localTeam;
		this.visitorTeam = visitorTeam;
		state = State.MATCH_NO_STARTED;
		min = 0;
		goals = new ArrayList<Goal>();
	}
	
	public Match(String id, String localTeam, String visitorTeam, State state, int min) {
		this.id = id;
		this.localTeam = localTeam;
		this.visitorTeam = visitorTeam;
		this.state = state;
		this.min = min;
		goals = new ArrayList<Goal>();
	}
	
	public void addGoal(Goal goal) {
		goals.add(goal);
	}
	
	public int getLocalGoals() {
		int count = 0;
		for (Goal g : goals)
			if (g.isLocal)
				count++;
		return count;
		}
	
	public int getVisitorGoals() {
		int count = 0;
		for (Goal g : goals)
			if (!g.isLocal)
					count++;
		return count;
	}
	
	public String toString() {
		return localTeam + " " + getLocalGoals() + " - " + getVisitorGoals() + " " + visitorTeam;
	}
	
	public String getId() {
		return id;
	}
	
}
