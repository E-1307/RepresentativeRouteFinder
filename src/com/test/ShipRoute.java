package com.test;

import java.util.ArrayList;
import java.util.List;

public class ShipRoute {
	
	private String originPort;
	
	private String shipID;

	private String destinationPort;
	
	private String pointCount;
	
	private String routeID;
	
	private long duration;
	
	private List<Coordinate> gpsCordinates;
	
	private int matchingPts;
	
	public String toString() {
		StringBuffer displayString = new StringBuffer("");
		
		displayString.append(getRouteID())
		.append(" ").append(getOriginPort())
		.append(" ").append(getDestinationPort())
		.append(" ").append(getShipID())
		.append(" ").append(getPointCount());
		return displayString.toString();
	}

	public long getDuration() {
		return duration;
	}
	
	public String getOriginPort() {
		return originPort;
	}

	public void setOriginPort(String originPort) {
		this.originPort = originPort;
	}

	public String getDestinationPort() {
		return destinationPort;
	}

	public void setDestinationPort(String destinationPort) {
		this.destinationPort = destinationPort;
	}

	public String getPointCount() {
		return pointCount;
	}

	public void setPointCount(String pointCount) {
		this.pointCount = pointCount;
	}

	public String getRouteID() {
		return routeID;
	}

	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public List<Coordinate> getGpsCordinates() {
		if(gpsCordinates == null) {
			gpsCordinates = new ArrayList<>();
		}
		return gpsCordinates;
	}

	public void setGpsCordinates(List<Coordinate> gpsCordinates) {
		this.gpsCordinates = gpsCordinates;
	}

	public String getShipID() {
		return shipID;
	}

	public void setShipID(String shipID) {
		this.shipID = shipID;
	}

	public int getMatchingPts() {
		return matchingPts;
	}

	public void setMatchingPts(int matchingPts) {
		this.matchingPts = matchingPts;
	}
	

}
