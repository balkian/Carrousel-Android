package com.onirica.carrousel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectTeam extends ListActivity{
	public static String baseURL = "http://www.eurielec.etsit.upm.es/~cathan";
	private HashMap<String, Team> mTeams = new HashMap<String, Team>();

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_selector);
        mTeams = retrieveTeams();
        populateTeams();
    	Log.d("SelectTeam","My teams are: "+mTeams.toString());
    }

    private void populateTeams() {
    	TeamAdapter adapter = new TeamAdapter((Team[])mTeams.values().toArray(new Team[0]));
    	Log.d("SelectTeam","POPULATING TEAMS");
    	setListAdapter(adapter);
    }
	
    private HashMap<String, Team> retrieveTeams() {
    	HashMap<String, Team> teams = new HashMap<String, Team>();
    	try {
    		URL url = new URL(baseURL+"/teams.json");
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
    		JSONArray teamsArray = jObject.getJSONArray("teams");
    		Log.d("SelectTeam","TeamsArray: "+teamsArray.toString());

    		if (teamsArray == null)
    			throw new Exception("No teams object in json response");
    		for (int i = 0; i < teamsArray.length(); i++) {
    			JSONObject teamsObject = teamsArray.getJSONObject(i);
    			
    			String id = teamsObject.getString("id");
    			String crest = teamsObject.getString("crest");
    			Team team = new Team(id, crest);
    			teams.put(team.getId(), team);
    		}
    	} catch (Exception e) {
    		Log.v("Results", "Cannot parse matches json info: " + e.getMessage()); 		
    	}
    	
    	return teams;
    } 

    private class TeamAdapter extends BaseAdapter  {
		private Team aTeams[];

        public TeamAdapter(Team teams[]) {
        	super();
            aTeams = teams;
        }
    	@Override
		public int getCount() {
			return aTeams.length;
		}

		@Override
		public Object getItem(int pos) {
			return (Object)aTeams[pos];
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}
		
	    private class TeamView extends LinearLayout {
			private TextView mTv;
			
			public TeamView(Context context, String text) {
				super(context);
				this.setOrientation(HORIZONTAL);
				this.setBackgroundColor(R.color.transparent);
				
				mTv = new TextView(context);
				mTv.setText(text);				
				this.addView(mTv, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
			}
			
			public void setText(String text) {
				mTv.setText(text);
			}
		}

		
		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			TeamView v;
			Team team= aTeams[pos];
			if (convertView == null) {
				v = new TeamView(parent.getContext(), team.toString());
			} else {
				v = (TeamView)convertView;
				v.setText(team.toString());
				// This is tricky: We are reusing the view for a different match.
				// We need to update the view checkbox state and we use setChecked, 
				// but this would trigger the previous event listener, clearing 
				// the model of the previous item shown with this view.
				/*v.setOnMatchCheckedChanged(null);
				v.setChecked(isSubscribed);
				v.setOnMatchCheckedChanged(new OnMatchCheckedListener(pos));*/
			}
			return v;
		}
    }

    

}
