package com.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class RepresentativeRoute {

	public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {
		
		Collection<ShipRoute> shipRoutes = new ArrayList<>();
		// Reading the csv input file
		try (BufferedReader br = new BufferedReader(new FileReader("DEBRV_DEHAM_historical_routes.csv"))){
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {

				try {
					if (line.length() > 1 && line.contains("imo")) {
						int index = ordinalIndexOf(line, ",", 7);
						String[] values = line.substring(0, index).replaceAll("\"", "").split(",");
						String routeID = values[1] + "-" + values[2];
						String coordinates = line.substring(index + 3, line.length() - 2);
						String coordinatePts = coordinates.replaceAll("\\]\\,", "]~");
						String[] coordinatePoints = coordinatePts.split("~");
						List<Coordinate> gpsCordinates = new ArrayList<>();
						int coordinatePtCount = coordinatePoints.length;
						for(int k=0;k < coordinatePtCount; k++) {
							String coordinateStr = getStringBetweenTwoChars(coordinatePoints[k], "[", "]");
							 double longitude =  Double.parseDouble(coordinateStr.split(",")[0]);
							 double latitude =  Double.parseDouble(coordinateStr.split(",")[1]);
							 Coordinate coordinate = new Coordinate(routeID,longitude,
									 latitude);
							 gpsCordinates.add(coordinate);
						}
						

						ShipRoute shproute = new ShipRoute();
						index = index + 1;
						shproute.setOriginPort(values[3]);
						shproute.setDestinationPort(values[4]);
						shproute.setRouteID(routeID);
						shproute.setPointCount(values[6]);
						shproute.setDuration(Long.parseLong(values[5]));
						shproute.setShipID(values[0]);
						shproute.setGpsCordinates(gpsCordinates);
						if(shproute.getDestinationPort().equals("DEBRV")) {
							shproute.setDestinationPort(shproute.getOriginPort());
							shproute.setOriginPort("DEBRV");
							Collections.reverse(shproute.getGpsCordinates());
						}
						List<Coordinate> filteredCoordinates = new ArrayList<>();
						
						int i = 1;
						int fixedBlocks = 0;
						/*
						 * The gpscordinates are filtered to 100 blocks for each route
						 */
						if(shproute.getGpsCordinates().size() > 100) {
							fixedBlocks = shproute.getGpsCordinates().size()/100;
						}else {
							fixedBlocks = 1;
						}
						for(; i< shproute.getGpsCordinates().size()-1;i=i+fixedBlocks) {
							filteredCoordinates.add(shproute.getGpsCordinates().get(i));
						}
						shproute.setGpsCordinates(filteredCoordinates);
						shipRoutes.add(shproute);
					}
				} catch (Exception e) {
					System.out.println(line);
				}
			}
			
		}
		
		/*
		 * The logic is to find the median points at each block/interval
		 * then find the closest route to the median. Add 1 pt for each closest route at each block.
		 * Then take the route with highest matching points.
		 */
		List<Double> routecoordinateXPts = new ArrayList<>();
		List<Double> routecoordinateYPts = new ArrayList<>();
		List<ShipRoute> filteredRoutes = new ArrayList<>();
		filteredRoutes.addAll(shipRoutes);
		Collection<ShipRoute> shpRoutesCollected = new ArrayList<>();
		int count = 4;
		do {
			shpRoutesCollected = new ArrayList<>();
			for (int i = 0; i < 100; i++) {
				routecoordinateXPts = new ArrayList<>();
				routecoordinateYPts = new ArrayList<>();

				for (ShipRoute shpRoute : filteredRoutes) {
					if(i <shpRoute.getGpsCordinates().size()) {
						routecoordinateXPts.add(shpRoute.getGpsCordinates().get(i).getLongitude());
						routecoordinateYPts.add(shpRoute.getGpsCordinates().get(i).getLatitude());
					}
				}
				Collections.sort(routecoordinateXPts, (one, two) -> Double.compare(one, two));
				Collections.sort(routecoordinateYPts, (one, two) -> Double.compare(one, two));

				double[][] median = new double[1][2];
				double medianXPt = 0;
				double medianYPt = 0;	
				
				
				if (routecoordinateXPts.size() % 2 == 0) {
					medianXPt = routecoordinateXPts.get(routecoordinateXPts.size() / 2 -1);
				} else {
					medianXPt = routecoordinateXPts.get(routecoordinateXPts.size() / 2);
				}

				if (routecoordinateYPts.size() % 2 == 0) {
					medianYPt = routecoordinateYPts.get(routecoordinateYPts.size() / 2 -1);
				} else {
					medianYPt = routecoordinateYPts.get(routecoordinateYPts.size() / 2);
				}

				median[0][0] = medianXPt;
				median[0][1] = medianYPt;

				double leastDistance = 0;
				ShipRoute nearestRoute = null;
				boolean isFirst = true;
				for (ShipRoute shpRoute : filteredRoutes) {
					
					if(i >= shpRoute.getGpsCordinates().size()) {
						continue;
					}
					
					double[][] coordinateArray = new double[1][2];
					coordinateArray[0][0] = shpRoute.getGpsCordinates().get(i).getLongitude();
					coordinateArray[0][1] = shpRoute.getGpsCordinates().get(i).getLatitude();
					double distance = distSum(median, coordinateArray);
					if (isFirst) {
						leastDistance = distance;
						nearestRoute = shpRoute;
					} else {
						if (distance < leastDistance) {
							leastDistance = distance;
							nearestRoute = shpRoute;
						}
					}
					isFirst = false;
				}
				nearestRoute.setMatchingPts(nearestRoute.getMatchingPts() + 1);
				shpRoutesCollected.add(nearestRoute);

			}
			filteredRoutes.clear();
			filteredRoutes.addAll(shpRoutesCollected);
			count--;
		}while(count > 0 );
		Collections.sort(filteredRoutes, (one, two) -> Integer.compare((int)two.getMatchingPts(), (int)one.getMatchingPts()));
		System.out.println("Representative Route : " + filteredRoutes.iterator().next());
		
		
    }
	
	public static int ordinalIndexOf(String str, String substr, int n) {
	    int pos = str.indexOf(substr);
	    while (--n > 0 && pos != -1)
	        pos = str.indexOf(substr, pos + 1);
	    return pos;
	}
	
	public static String getStringBetweenTwoChars(String input, String startChar, String endChar) {
	    try {
	        int start = input.indexOf(startChar);
	        if (start != -1) {
	            int end = input.indexOf(endChar, start + startChar.length());
	            if (end != -1) {
	                return input.substring(start + startChar.length(), end);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return input;
	}
	
	
	public static double distSum(double[][] median,
           double [][]arr)
		{
			double sum = 0;
			double distx = Math.abs(arr[0][0] - median[0][0]);
			double disty = Math.abs(arr[0][1] - median[0][1]);
			sum = Math.sqrt((distx * distx) + (disty * disty));
		
			return sum;
		}
	
	
	
}