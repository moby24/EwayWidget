package com.ewaywidget;

import com.ewaywidget.http.Response;
import com.widget.checkid.RequestCheck;

import android.os.AsyncTask;

public class MyAsync extends AsyncTask<Void, Void, Void> {

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		RequestCheck mRequest = new RequestCheck();
		Response response = new Response(mRequest.execute(
				"kyiv", "5385"));
		return null;
	}

}
