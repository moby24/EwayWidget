package com.ewaywidget.http;

import org.w3c.dom.Document;

public class Response {
	private Document data;
	
	public Response(){		
	}
	
	public Response(Response response){
		setData(response.getData());
	}

	public Document getData() {
		return data;
	}

	public void setData(Document doc) {
		this.data = doc;
	}

}
