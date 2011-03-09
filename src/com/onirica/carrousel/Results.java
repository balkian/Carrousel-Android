package com.onirica.carrousel;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class Results extends Service {
	private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
      Results getService() {
          return Results.this;
      }
    }
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
    @Override
    public void onCreate() {
         pollingTask task = new pollingTask();
         Timer timer = new Timer(true);
         timer.scheduleAtFixedRate(task, 0, 60000);
  }
	
	private class pollingTask extends TimerTask {
		  @Override
		  public void run() {
		  // Aqui descargaremos los resultados
		  }
		}

}
