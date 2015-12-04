/**
 * 
 */
package net.thepaca.hunydoo;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * @author MomNDad
 *
 */
public class HunyDooLocationListener implements LocationListener {
	 private  double latitude;
	    private  double longitude;

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location loc) {
		latitude = loc.getLatitude();
		longitude = loc.getLongitude();
		String currentLocation = "Lat: " + loc.getLatitude() + " Lon: " + loc.getLongitude();
		
		Log.i(getClass().getSimpleName(),currentLocation);
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

    public  double getLatitude() {
        return latitude;
    }

    public  double getLongitude() {
        return longitude;
    }
    
}
