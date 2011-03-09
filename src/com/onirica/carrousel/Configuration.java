package com.onirica.carrousel;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class Configuration extends Activity {
    private Results mResults;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent intent = new Intent(getBaseContext(), Results.class);
        startService(intent);
        ServiceConnection conn = new ServiceConnection() {
        	@Override
        	public void onServiceConnected(ComponentName className, IBinder service) {
        		 mResults = ((Results.LocalBinder)service).getService();
        		 
        	}
        	@Override
        	public void onServiceDisconnected(ComponentName className) {
        		mResults = null;
        	}
		

        };
        bindService(intent, conn,  BIND_AUTO_CREATE);
    }
}