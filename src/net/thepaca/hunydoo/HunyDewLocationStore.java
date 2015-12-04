/**
 * 
 */
package net.thepaca.hunydoo;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author MomNDad
 *
 */
public class HunyDewLocationStore {

	private static HunyDewLocationStore _locaStoreSingletonObj;
	
	/**
	 * 
	 */
	ArrayList<HunyDewLocationItem> hdLocaList;
	
	private HunyDewLocationStore() {
		// TODO Auto-generated constructor stub
	}

	public static HunyDewLocationStore getLocaStoreSingletonObj() {
		
		if (_locaStoreSingletonObj == null) {
			
			_locaStoreSingletonObj = new HunyDewLocationStore();
			
		}
		
		return _locaStoreSingletonObj;
	}
	
	public Object clone() throws CloneNotSupportedException {
		
		throw new CloneNotSupportedException("not for HunyDewLocationStore");
	}
	
	public void setLocationItem(String _locaName, double _lat, double _lon) {
		
		HunyDewLocationItem locaItem = new HunyDewLocationItem(_locaName, _lat, _lon);
		
		if(hdLocaList == null) {
		
			hdLocaList = new ArrayList<HunyDewLocationItem>();
		}
		
		hdLocaList.add(locaItem);
	}
	
	public String getLocationName(double _lat, double _lon) {
		
		HunyDewLocationItem locaItem ;
		
		Iterator<HunyDewLocationItem> e = hdLocaList.iterator();
		
		while (e.hasNext()) {
			
			locaItem = e.next();
			
			if ((locaItem.hdLocaLat == _lat) && (locaItem.hdLocaLon == _lon)) {
				
				return locaItem.hdLocaName;
			}
		}
		return "NOT FOUND";
	}
	
	public ArrayList<String> getLocaList() {
		
		HunyDewLocationItem locaItem ;
		
		ArrayList<String> locaList = new ArrayList<String>();
		
		if (hdLocaList != null) {
			
			Iterator<HunyDewLocationItem> e = hdLocaList.iterator();
			
			while (e.hasNext()) {
				
				locaItem = e.next();
				
				locaList.add(locaItem.hdLocaName);
			}
		}
		
		// stick in "SmartDew" as the last element
		// locaList.add("SmartDew");
		
		return locaList;
	}
	
	public String getLocaName(double _inLat, double _inLon) {
		
		final double ACCEPTABLE_CLOSENESS_IN_METERS = 200;
		
		HunyDewLocationItem locaItem ;
//		double lat, lon;
//		int iLat, iLon;
		
		if (hdLocaList != null) {
			
			Iterator<HunyDewLocationItem> e = hdLocaList.iterator();
			
			while (e.hasNext()) {
				
				locaItem = e.next();

				/*
				 * 
				lat = locaItem.hdLocaLat;
				lon = locaItem.hdLocaLon;
				
				iLat = Double.compare(lat, _inLat);
				
				iLon = Double.compare(lon, _inLon);

				if ((iLat == 0) && (iLon == 0)) {
					
					return locaItem.hdLocaName;
				}

				*
				*/
				
				double km = locaItem.getDistanceFrom(_inLat, _inLon);
				
				double distanceInMeters = km * 1000;
				
				//HunyDewZLogger.write(this,"getLocaName", locaItem.hdLocaName + " is at Lat:" + locaItem.hdLocaLat + " Lon:" + locaItem.hdLocaLon);
				 //HunyDewZLogger.write(this,"getLocaName", locaItem.hdLocaName + " distance from " + _inLat + ":" + _inLon + " is " + distanceInMeters + " meters"); 
				 
//				 HunyDewZLogger.write(getApplicationContext(),"getLocaName", "ACCEPTABLE_CLOSENESS_IN_METERS: " + ACCEPTABLE_CLOSENESS_IN_METERS);
				 
				if (distanceInMeters <= ACCEPTABLE_CLOSENESS_IN_METERS) {
					
	//				HunyDewZLogger.write(getApplicationContext(),"getLocaName", "yes we are in the vicinity of " + locaItem.hdLocaName);
					
					return locaItem.hdLocaName;
				}
				
			}
		}

		return null;
	}

	public void clear() {
		
		if (hdLocaList != null)
			hdLocaList.clear();
	}
}
