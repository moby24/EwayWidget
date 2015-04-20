package com.ewaywidget.config;

import com.ewaywidget.WidgetFactory;

public class MyThread extends Thread implements Runnable{

	@Override
	public void run() {
		while (true) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (WidgetFactory.h != null) {
				WidgetFactory.h.sendEmptyMessage(WidgetFactory.UPDATE_DATABASE);
			}
		}
	}

}
