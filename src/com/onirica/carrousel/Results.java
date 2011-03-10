package com.onirica.carrousel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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
	
	public HashSet<String> getSubscriptions() {
	   	return mSubscriptions;
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
    	try {
    		URL url = new URL("http://192.168.1.10/matches.json");
    		URLConnection urlConnection = url.openConnection();
    		BufferedReader in = new BufferedReader(
                                	new InputStreamReader(
                                			urlConnection.getInputStream()));
    		String line;
    		StringBuilder builder = new StringBuilder();
    		while ((line = in.readLine()) != null) {
    			builder.append(line);
    		}
    		String jString = builder.toString();
    	   
    		JSONObject jObject;
    		jObject = new JSONObject(jString); 
    		JSONArray matchesArray = jObject.getJSONArray("matches");
    		if (matchesArray == null)
    			throw new Exception("No matches object in json response");
    		
    		for (int i = 0; i < matchesArray.length(); i++) {
    			JSONObject matchObject = matchesArray.getJSONObject(i);
    			
    			String id = matchObject.getString("id");
    			String localTeam = matchObject.getString("localTeam");
    			String visitorTeam = matchObject.getString("visitorTeam");
    			int min = (int) matchObject.getLong("min");
    			String stateStr = matchObject.getString("state");
    			Match.State state;
    			if (stateStr == "NOT STARTED")
    				state = Match.State.MATCH_NO_STARTED;
    			else if (stateStr == "FIRST ROUND")
    				state = Match.State.MATCH_FIRST_ROUND;
    			else if (stateStr == "HALF TIME")
    				state = Match.State.MATCH_HALF_TIME;
    			else if (stateStr == "SECOND ROUND")
    				state = Match.State.MATCH_SECOND_ROUND;
    			else if (stateStr == "ENDED")
    				state = Match.State.MATCH_ENDED;
    			else
    				state = Match.State.MATCH_NO_STARTED;
    			Match match = new Match(id, localTeam, visitorTeam, state, min);
    			JSONArray goalsArray = matchObject.getJSONArray("goals");
    			if (goalsArray != null) {
    	    		for (int j = 0; j < goalsArray.length(); j++) {
    	    			JSONObject goalObject = goalsArray.getJSONObject(j);
    	    			int goalMin = (int) goalObject.getLong("min");
    	    			boolean isLocal = goalObject.getBoolean("isLocal");
    	    			String who = goalObject.getString("who");
    	    			Match.Goal goal = match.new Goal (goalMin, isLocal, who);
    	    			match.addGoal(goal);
    	    		}
    			}
    			matches.put(match.getId(), match);
    		}
    	} catch (Exception e) {
    		Log.v("Results", "Cannot parse matches json info: " + e.getMessage()); 		
    	}
    	
    	return matches;
    } 
	
    private class pollingTask extends TimerTask {
		  @Override
		  public void run() {
		  // Aqui descargaremos los resultados
		  }
	}

}
