package com.onirica.carrousel;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Results extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
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
