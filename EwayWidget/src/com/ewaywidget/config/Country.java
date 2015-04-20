package com.ewaywidget.config;

public class Country {
	private String name;
	private String currentCity;
	private String defaultCity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCurrentCity(String currentCity) {
		this.currentCity = currentCity;
	}

	public String getCurrentCity() {
		return currentCity;
	}

	public void setDefaultCity(String defaultCity) {
		this.defaultCity = defaultCity;
	}

	public String getDefaultCity() {
		return defaultCity;
	}

}
