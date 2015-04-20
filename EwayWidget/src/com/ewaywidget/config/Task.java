package com.ewaywidget.config;

import android.util.Log;

import com.ewaywidget.WidgetFactory;

public class Task implements Runnable {
	private volatile boolean isRunning = true;
	
	public int interval;
	public Task (int inter){
		
		interval= inter;
		Log.i("QWE", "RTY");
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isRunning) {

			// TODO Auto-generated method stub
			try {
				Log.d("INTERVAL_UPDATE", "mn" + interval);
				Thread.sleep(interval * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (WidgetFactory.h != null) {
				WidgetFactory.h.sendEmptyMessage(WidgetFactory.UPDATE_DATABASE);
			}
		}
	}

	public void kill() {
		Log.i("RTY", "QWE");
		isRunning = false;
	}

}
