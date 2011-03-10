package com.onirica.carrousel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class Results extends Service {
	private final IBinder mBinder = new LocalBinder();
	private HashMap<String, Match> mMatches = new HashMap<String, Match>();
	private HashSet<String> mSubscriptions = null;
	
	public HashMap<String, Match> getMatches() {
    	return mMatches;
    }
	
	public void updateSubscriptions(HashSet<String> subscriptions) {
	    	mSubscriptions = subscriptions;
	}

    public class LocalBinder extends Binder {
      Results getService() {
          return Results.this;
      }
    }
	@Override
	public IBinder onBind(Intent arg0) {
		if (mMatches.isEmpty())
			mMatches = retrieveMatches();
		return mBinder;
	}
    @Override
    public void onCreate() {
         pollingTask task = new pollingTask();
         Timer timer = new Timer(true);
         timer.scheduleAtFixedRate(task, 0, 60000);
    }
    
    private HashMap<String, Match> retrieveMatches() {
    	HashMap<String, Match> matches = new HashMap<String, Match>();
    	
    	Match match = new Match("bbva1", "Deportivo", "Atelico de Madrid");
    	matches.put(match.getId(), match);
    	
    	match = new Match("bbva2", "Betis", "Sevilla");
    	matches.put(match.getId(), match);
    	
    	match = new Match("bbva3", "Oviedo", "Sporting", Match.State.MATCH_SECOND_ROUND, 84);
    	
    	match = new Match("bbva13", "Madrid", "Barcelona", Match.State.MATCH_FIRST_ROUND, 13);
    	Match.Goal goal = match.new Goal(12, true, "Cristiano Ronaldo");
    	match.addGoal(goal);
    	matches.put(match.getId(), match);
    	
    	return matches;
}
	
    private class pollingTask extends TimerTask {
		  @Override
		  public void run() {
		  // Aqui descargaremos los resultados
		  }
	}

}
