/**
 * 
 */
package net.thepaca.hunydoo;

// import com.google.android.maps.GeoPoint;

/**
 * @author MomNDad
 *
 */
public class HunyDewLocationItem {

	/**
	 * 
	 */
	
	String hdLocaName;
	double hdLocaLat, hdLocaLon;
	String hdLocaURI;
	private char unit;
	
	public HunyDewLocationItem(String _name, double _lat, double _lon) {
		
		hdLocaName = _name;
		hdLocaLat = _lat;
		hdLocaLon = _lon;
		hdLocaURI = "http://maps.google.com/maps?ll=" + _lat + "," + _lon;  // default
	}

	//getter
	public String getName() {
		return hdLocaName;
	}
	
	public double getLatitude() {
		return hdLocaLat;
	}
	
	public double getLongitude() {
		
		return hdLocaLon;
	}
	
	void setDistanceUnit(char _unit) {
		
		unit = _unit;
	}
	
	/**
	 * Method used to convert the value form radians to degrees
	 * 
	 * @param rad
	 * @return value in degrees
	 */
	/*
	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
  */
	/**
	 * Converts the value from Degrees to radians
	 * 
	 * @param deg
	 * @return value in radians
	 */
 /*	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}	
	*/
	/*
     * Haversine Formula (from R.W. Sinnott, "Virtues of the Haversine", Sky
     * and Telescope, vol. 68, no. 2, 1984, p. 159):
     *
     * See the following URL for more info on calculating distances:
     * http://www.census.gov/cgi-bin/geo/gisfaq?Q5.1
     */
	
	public double getDistanceFrom(HunyDewLocationItem otherPt) {
        
		return getDistanceFrom(otherPt.getLatitude(), otherPt.getLongitude());
   }
	
	public double getDistanceFrom(double _lat1, double _lon1) {
        
    	final double earthRadius = 6371.005076123; // Earth's mean radius in km
        
    	// now get the difference in latitudes
        double lat1 = Math.toRadians(_lat1);
        double lat2 = Math.toRadians(getLatitude());
        double dlat = (lat2 - lat1);

     //   HunyDewZLogger.write("getDistanceFrom", "new lat " + _lat1 + "hdLoc lat " + getLatitude());
        
        // now the longitude
        double lon1 = Math.toRadians(_lon1);
        double lon2 = Math.toRadians(getLongitude());
        double dlon = (lon2 - lon1);

    //    HunyDewZLogger.write("getDistanceFrom", "new lon " + _lon1 + "hdLoc lon " + getLongitude());

        double a = (Math.sin(dlat / 2)) * (Math.sin(dlat / 2))
        + (Math.cos(lat1) * Math.cos(lat2) * (Math.sin(dlon / 2)) * (Math.sin(dlon / 2)));
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        double km = earthRadius * c;
        
        return km; 
  }
	
}
