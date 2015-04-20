package com.ewaywidget.response;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ewaywidget.WidgetProvider;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class ParseXmlResponse extends Activity {

	private static final String LOG_TAG = "My Log";

	public StringBuilder sb;

	public String string_reader;

	public String result;
	public String nameTAG;
	public int deepValue;

	public ParseXmlResponse(String citId ,String wdgtId) {

		ResponseString responseString = new ResponseString(citId,wdgtId);
		ExecutorService ex = Executors.newCachedThreadPool();

		Future<String> str = ex.submit(responseString);

		String resultXML = null;
		try {
			resultXML = str.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		string_reader = resultXML;

		sb = new StringBuilder();

		try {
			XmlPullParser xpp = prepareXpp();

			boolean isDone = false;
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT
					&& (isDone != true)) {
				switch (xpp.getEventType()) {
				case XmlPullParser.START_TAG:
					// Log.d(LOG_TAG, "END_TAG: name = " + xpp.getName());
					nameTAG = xpp.getName();
					deepValue = xpp.getDepth();
					break;
				case XmlPullParser.TEXT:
					// Log.d(LOG_TAG, "text = " + xpp.getText());
					if (nameTAG.equals("title") && deepValue == 3) {
						sb.append(xpp.getText());
					}
					break;

				default:
					break;
				}

				xpp.next();
			}
			Log.d(LOG_TAG, "END_DOCUMENT");

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	XmlPullParser prepareXpp() throws XmlPullParserException {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		// check support for namespace
		factory.setNamespaceAware(true);
		// create parser
		XmlPullParser xpp = factory.newPullParser();
		// gives parser on Reader enter
		if (string_reader != null) {
			xpp.setInput(new StringReader(string_reader));
		}
		return xpp;
	}

	public String getParseXML() {
		result = sb.toString();
		
		return result;
	}

}
