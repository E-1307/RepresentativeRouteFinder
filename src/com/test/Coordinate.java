package com.test;

public class Coordinate {
	
	public Coordinate (String routeID, double longitude,double latitude) {
		this.routeID=routeID;
		this.longitude=longitude;
		this.latitude=latitude;
	}
	
	private String routeID;
	private double longitude;
	
	private double latitude;
	
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getRouteID() {
		return routeID;
	}

	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}

	

}
