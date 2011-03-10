package com.onirica.carrousel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Configuration extends ListActivity {
    private Results mResults;
    private Intent intent;
    private ServiceConnection conn;
    private ProgressDialog progressDialog;
    private HashSet<String> subscribedMatches  = new HashSet<String>();
    private Button mSubscribeButton;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(getBaseContext(), Results.class);
        startService(intent);
        setContentView(R.layout.main);
        conn = new ServiceConnection() {
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
       Button b = (Button) findViewById(R.id.quit);
       b.setOnClickListener(new OnClickListener() {
       	public void onClick(View v) {
       		stopService(intent);
       		quit();
       	} 
		});
       
       mSubscribeButton = (Button) findViewById(R.id.subscribe);
       mSubscribeButton.setOnClickListener(new OnClickListener() {
       	public void onClick(View v) {
       		mResults.updateSubscriptions(subscribedMatches);
       		close();
       	} 
		});
    }
    private void close() {
    	unbindService(conn);
    	finish();
    }
    
    private void quit() {
    	unbindService(conn);
    	stopService(intent);
    	finish();
    }
    
    private void populateMatches() {
    	HashMap<String, Match> matches = mResults.getMatches();
    	MatchAdapter adapter = new MatchAdapter((Match[])matches.values().toArray(new Match[0]));
    	setListAdapter(adapter);
    }
    
    private class MatchView extends LinearLayout {
		private TextView mTv;
		private CheckBox mCb;
		private CompoundButton.OnCheckedChangeListener mListener = null;
		
		public MatchView(Context context, String text) {
			super(context);
			this.setOrientation(HORIZONTAL);
			mTv = new TextView(context);
			mTv.setText(text);
			mCb = new CheckBox(context);
			mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (mListener != null) {
							mListener.onCheckedChanged(buttonView, isChecked);							}
					}
			});
			
			this.addView(mTv, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
			this.addView(mCb, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0));
		}
		
		public void setText(String text) {
			mTv.setText(text);
		}
		
		public void setOnMatchCheckedChanged(CompoundButton.OnCheckedChangeListener listener) {
			mListener = listener;
		}
	}
    private class MatchAdapter extends BaseAdapter  {
		private Match mMatches[];

        public MatchAdapter(Match matches[]) {
        	super();
            mMatches = matches;
        }
    	@Override
		public int getCount() {
			return mMatches.length;
		}

		@Override
		public Object getItem(int pos) {
			return (Object)mMatches[pos];
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}
		public View getView(int pos, View convertView, ViewGroup parent) {
			MatchView v;
			if (convertView == null) {
				v = new MatchView(parent.getContext(), mMatches[pos].toString());
				v.setOnMatchCheckedChanged(new OnMatchCheckedListener(pos));
			} else {
				v = (MatchView)convertView;
				v.setText(mMatches[pos].toString());
			}
			return v;
		}
		private class OnMatchCheckedListener implements CompoundButton.OnCheckedChangeListener {           
	        private int mPosition;
	        OnMatchCheckedListener(int position){
	                mPosition = position;
	        }
	        @Override
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	        	Match m = (Match)getItem(mPosition);
	         	if (m != null) {
	         		if (isChecked)
	    	        	subscribedMatches.remove(mMatches[mPosition].getId());
	    		    else
	    	        	subscribedMatches.add(mMatches[mPosition].getId());
	    	    }
	        }   
	        
		}
    }
}