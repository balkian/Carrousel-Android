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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class Results extends Service {
	private final IBinder mBinder = new LocalBinder();
	private HashMap<String, Match> mMatches = new HashMap<String, Match>();
	private HashSet<String> mSubscriptions = null;
	private final String RESULTS_LOG = "Results";
	private static final int SCORE_NOTIFICATION_ID = 1;
	private NotificationManager mNM;
	
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
    	mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
         pollingTask task = new pollingTask();
         Timer timer = new Timer(true);
         timer.scheduleAtFixedRate(task, 0, 60000);
    }
    
    private HashMap<String, Match> retrieveMatches() {
    	HashMap<String, Match> matches = new HashMap<String, Match>();
    	try {
    		URL url = new URL("http://www.eurielec.etsit.upm.es/~fherrera/matches.json");
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
			Log.v(RESULTS_LOG, "running task");
			if (mSubscriptions == null || mSubscriptions.isEmpty()) {
				Log.v(RESULTS_LOG, "no subscriptions, exiting");
				return;
			}
			Log.v(RESULTS_LOG, "Has " + mSubscriptions.size() + " subscriptions");
			HashMap<String, Match> matches = retrieveMatches();
			if (matches == null || matches.isEmpty())
				return;
				
			for (String id : mSubscriptions ) {
				if (matches.containsKey(id))
					checkMatch(matches.get(id));
			}
			mMatches = matches;
		}
		private void checkMatch(Match match) {
			if (match.getLocalGoals() > mMatches.get(match.getId()).getLocalGoals()) {
				notifyGoal(match.getLocalTeam(),
						   match.getLocalTeam(), match.getLocalGoals(),
						   match.getVisitorTeam(), match.getVisitorGoals());
			}
			
			if (match.getVisitorGoals() > mMatches.get(match.getId()).getVisitorGoals()) {
				notifyGoal(match.getVisitorTeam(),
						   match.getLocalTeam(), match.getLocalGoals(),
						   match.getVisitorTeam(), match.getVisitorGoals());
			}
		}
		
	}
	private void notifyGoal(String scoreTeam,
            String localTeam, int localGoals,
            String visitorTeam, int visitorGoals) {
			String titleText = scoreTeam + " Scored!";
			String contentText = scoreTeam + " Scored!\n" + localTeam + " " + localGoals + 
              " - " + visitorTeam + " " + visitorGoals;
			int icon = R.drawable.icon;
			long when = System.currentTimeMillis();

			Notification notification = new Notification(icon, titleText, when);
			Context context = getApplicationContext();
			Intent notificationIntent = new Intent(this, Results.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

			notification.setLatestEventInfo(context, titleText, contentText, contentIntent);


			mNM.notify(SCORE_NOTIFICATION_ID, notification);

}

}
