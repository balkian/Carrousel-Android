package com.onirica.carrousel;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ArrayAdapter;

public class Configuration extends ListActivity {
    private Results mResults;
    private Intent intent;
    private ProgressDialog progressDialog;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(getBaseContext(), Results.class);
        startService(intent);
        setContentView(R.layout.main);
        ServiceConnection conn = new ServiceConnection() {
        	@Override
        	public void onServiceConnected(ComponentName className, IBinder service) {
        		 mResults = ((Results.LocalBinder)service).getService();
        		 populateMatches();
        		 progressDialog.dismiss();
        		 
        	}
        	@Override
        	public void onServiceDisconnected(ComponentName className) {
        		mResults = null;
        		stopService(intent);
        		finish();
        	}
        };
        bindService(intent, conn,  BIND_AUTO_CREATE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving match list");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                //Log.i(TAG, "dialog cancel has been invoked");
            		stopService(intent);
                    finish();
                }
            });
       progressDialog.show();
    }
    private void populateMatches() {
    	ArrayList<Match> matches = mResults.getMatches();
    	Match[] ms = new Match[matches.size()]; 
    	matches.toArray(ms);
    	ArrayAdapter<Match> adapter = new ArrayAdapter<Match>(this, R.layout.list_item, ms);
    	setListAdapter(adapter);	
    	
    }
}