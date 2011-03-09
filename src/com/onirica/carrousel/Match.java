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
	public ArrayList<Goal> goals;
	
	public Match(String localTeam, String visitorTeam) {
		this.localTeam = localTeam;
		this.visitorTeam = visitorTeam;
		state = State.MATCH_NO_STARTED;
		min = 0;
		goals = new ArrayList<Goal>();
	}
	
	public Match(String localTeam, String visitorTeam, State state, int min) {
		this.localTeam = localTeam;
		this.visitorTeam = visitorTeam;
		this.state = state;
		this.min = min;
		goals = new ArrayList<Goal>();
	}
	
	public void addGoal(Goal goal) {
		goals.add(goal);
	}
	
}
