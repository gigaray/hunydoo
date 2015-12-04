/**
 * 
 */
package net.thepaca.hunydoo;

/**
 *  5/28   Add support for OptionsMenu [remove Context menu]
 *  5/30 add support to dynamically create options menu
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

/**
 * @author MomNDad
 *
 */
// MapActivity is a special sub-class of Activity, provided by the Maps library, it provides 
// important map capabilities.
public class HunyDewActRegisterLocOnMap extends MapActivity {

	protected MapView hdMapView = null;
	protected MapController hdMapController;
	 GeoPoint locPt;
	
	 String hdMapAPIKey;
	
	  private LocationManager hdLocMgr;
	  private String hdLocProvider;
	
		private static final int VOICE_RECOGNITION_REQUEST = 1234;
		private static final int LOC_SPEECH_INPUT = 0x28990901;
		private static final int REG_MARKER_LOC = 0x28990902;
		
	 // for the prototype this all we care about
	private String locaName ;
	private double lat, lon;	 
	
	 // HunyDewLocationStore is an ArryaList of HunyDewLocationItem
	 HunyDewLocationStore locaStore = HunyDewLocationStore.getLocaStoreSingletonObj();
	 HunyDewLocationItem locaItem;
	 
	 // adding db support to store the location
	 HunyDewDBAdapterLoca hdDBAdapter;
	 
		static final private int LIST_NEW_HD_LOCAENTRY = Menu.FIRST;
		
	        private int mXCorr, mYCorr;
	        
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isLocationDisplayed()
	 */
	@Override
	protected boolean isLocationDisplayed() {
		// TODO Auto-generated method stub
		return super.isLocationDisplayed();
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		
		HunyDewZLogger.startTrace("aaRegLocMap");
		
		super.onCreate(arg0);

		//
		// Calculate the corrections for placing the marker
		//
		DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mXCorr = (metrics.widthPixels / 40) - 1;
        if (metrics.heightPixels <= 320) mYCorr = 13;
        else if (metrics.heightPixels <= 400) mYCorr = 14;
        else if (metrics.heightPixels <= 480) mYCorr = 15;
        else if (metrics.heightPixels <= 800) mYCorr = 22;
        else if (metrics.heightPixels <= 854) mYCorr = 23;
        else mYCorr = 6;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Check if running inside the emulator, - the map keys to be used depend upon it   //
		// the layout File as in map_act*.xml stores the relevant map keys								//
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		boolean isDebugEmulator = HunyDewUtils.signedWithDebugKey(this, this.getClass());
		if (isDebugEmulator == true) {
			setContentView(R.layout.map_act);
		}
		else {
			setContentView(R.layout.map_act_rel_dev);
		}

		// setup HD's MapView object 
		hdMapView = (MapView) findViewById(R.id.hdMapView);
		
		hdMapView.setBuiltInZoomControls(true);  // we will use the built in Zoom Control
		// hdMapView.setSatellite(true);	// disabled to keep it perform better
		// hdMapView.setStreetView(true);	// anyways not useful to hd...
		//hdMapView.setOnTouchListener(this.onTouchEvent(null));
		//hdMapView.setOnKeyListener(this);		
		
		// notes: key classes in placing a marker on a map
		// MapView displays a Map and "has a" Overlay
		// Overlay <<abstract>> it represents a screen that can be drawn on top of a map
		// OverlayItem represents a marker on a map
		// ItemizedItem <T extends OverlayItem> <<abstract>> - it manages a list of OverlayItems also handles drawing for each OverlayItem
		// MyLocationOverlay draws the suer's current location on the map
		// Drawable represents a marker on the map
		// GeoPoint is the point on the map in micro-degrees
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// to create map markers and lay-overs- must implement the ItemizedOverlay class, which 
		// can manage a whole set of Overlay (which are the individual items placed on the map).
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// setup the location manager
		 Criteria hdCrit = new Criteria();
		 hdCrit.setAccuracy(Criteria.ACCURACY_COARSE);
		 hdCrit.setAltitudeRequired(false);
		 hdCrit.setBearingRequired(false);
		 hdCrit.setCostAllowed(true);
		 hdCrit.setPowerRequirement(Criteria.POWER_LOW);
		
		 hdLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		 // list all providers
	       List<String> providers = hdLocMgr.getAllProviders();
	       if (providers.size() < 1)
	        	 Toast.makeText(getApplicationContext(), getResources().getText(R.string.loca_SP_None), Toast.LENGTH_LONG).show();
	        
	       for (String provider : providers) {
	        	HunyDewZLogger.write(getApplicationContext(),"provider", provider);
	       }		 
		 
		 hdLocProvider = hdLocMgr.getBestProvider(hdCrit, true); 
			
		 Location location = hdLocMgr.getLastKnownLocation(hdLocProvider);

		 if (location == null) {
				// choose a dummy location to start
				String coordinates[] = {"37.41420938", "-122.0772457"}; // Computer History Museum
				 lat = Double.parseDouble(coordinates[0]);
				lon = Double.parseDouble(coordinates[1]);
		 }
		 else {
			 lat = location.getLatitude();
			 lon = location.getLongitude();
		 }
	    // do the conversion to micro-degrees
	    locPt = new GeoPoint( (int) (lat * 1E6), (int) (lon * 1E6)); 

	    // now prepare the MapView object's controller
		hdMapController = hdMapView.getController();
	    hdMapController.animateTo(locPt);
		hdMapController.setZoom(15);
		
		// make a drawable object with the Map marker
		Drawable marker = this.getResources().getDrawable(R.drawable.marker_squared_red);
		
		// prepare the overlay for the marker - map a MapOverlay and add the OverlayItem to it
		HunyDewLocasItemizedOverlay hdLocasItemized = new HunyDewLocasItemizedOverlay(marker, (int) (lat * 1E6), (int) (lon * 1E6));
	
		// 1.0 get the current overlays from the Mapview
		List<Overlay> mapOverlays = hdMapView.getOverlays();
		mapOverlays.clear();
		mapOverlays.add(hdLocasItemized);
		
		hdMapView.invalidate();	// force MapView to draw

		 HunyDewZLogger.write(getApplicationContext(),"HDMap", "onCreate - A");
		 updateWithNewLocation(location);
		 HunyDewZLogger.write(getApplicationContext(),"HDMap", "onCreate - B");
		 
		 //now setup the db Access
		 hdDBAdapter = new HunyDewDBAdapterLoca(this);
}
	
private void updateWithNewLocation(Location location) {
	 
	 String latLongString;
	 		 
	 if (location != null) {
		  lat = location.getLatitude();
		  lon = location.getLongitude();
		 
		 latLongString = "\n>>Lat: " + lat + " Lon: " + lon;
		 
		 Double dlat = location.getLatitude();
		 Double dlon = location.getLongitude();
		 
		 GeoPoint point = new GeoPoint(dlat.intValue(), dlon.intValue());

		    // do the conversion to micro-degrees
	    locPt = new GeoPoint( (int) (dlat * 1E6), (int) (dlon * 1E6)); 
		 
		 hdMapController.setCenter(point);
		 
		 hdMapController.animateTo(locPt);
		 
		 hdMapView.invalidate();	// force MapView to draw
	 }
	 else {
		 latLongString = "\n!!Current location info not available!! ";
	 }
	 
	 HunyDewZLogger.write(getApplicationContext(),"HDMap:", latLongString);
}
@Override
protected void onResume() {
	 
	 super.onResume();
	 
	 // open the database
	 hdDBAdapter.open();
	 
	 // now load the entries from the database
	 populateHDLocaList();
}

/** Stop the updates when Activity is paused */
@Override
	protected void onPause() {
		
	 super.onPause();
		
	 hdDBAdapter.close();
}

@Override
public void onStop() {
	
	// suspend remaining UI updates, threads or processing that aren't 
	// required when the activity isn't visible - incl stop sensor listener
	// time to persist all edits or state changes; the process is likely
	// to be killed soon
	
	super.onStop();
}

// called at the end of the full life time
// perform final cleanup - resources allocated in onCreate
@Override
public void onDestroy() {
	
	// cleanup any resources including ending threads, 
	// closing DB connections etc
	
	super.onDestroy();
	
	hdDBAdapter.close();
	
	HunyDewZLogger.stopTrace("aaRegLocMap");
}

// store a cursor over all the
Cursor hdLocaListCursor;
private void populateHDLocaList() {

	hdLocaListCursor = hdDBAdapter.getAllHDLocaItemsCursor();
	startManagingCursor(hdLocaListCursor);
	
	updateLocaListArray();
	
	return;
}

private void updateLocaListArray() {
	
	hdLocaListCursor.requery();
	
	locaStore.clear();
	
	//hdLocTextView.setText("");
	
	if (hdLocaListCursor.moveToFirst()) {
		
		do {
			String locaName = hdLocaListCursor.getString(HunyDewDBAdapterLoca.COL_LOCA_NAME);
			
			double lat = hdLocaListCursor.getDouble(HunyDewDBAdapterLoca.COL_LOCA_LAT);
			double lon = hdLocaListCursor.getDouble(HunyDewDBAdapterLoca.COL_LOCA_LON);
			
			locaStore.setLocationItem(locaName, lat, lon);
			
			//		hdLocTextView.append("\n || " + locaName);
			
			HunyDewZLogger.write(getApplicationContext(),"updateLocaList", locaName + " is at Lat:" + lat + " Lon:" + lon);
			
		} while(hdLocaListCursor.moveToNext());
	}
	

}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 *  License requires that the App report whether or not route information
	 *  is currently being displayed
	 */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
	 * 
	 * It comes here when you click on the map in the emulator
	 *   first ACTION_DOWN (0) then ACTION_MOVE(2) then ACTION_UP(1)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int actionType = ev.getAction();
	    switch (actionType) {
	    case MotionEvent.ACTION_UP:
	            Projection proj = hdMapView.getProjection();
	            GeoPoint loc = proj.fromPixels((int)ev.getX(), (int)ev.getY()); 
	            
	            //       String sirina=Double.toString(loc.getLongitudeE6()/1000000);
	            //       String dolzina=Double.toString(loc.getLatitudeE6()/1000000);
	            //        Toast toast = Toast.makeText(getApplicationContext(), "Lon: "+sirina+" Lat: "+dolzina, Toast.LENGTH_LONG);
	            //        toast.show();
	            
	            hdMapController.setCenter(loc);
	   		 
	   		 	hdMapController.animateTo(loc);
	   		 	locPt = loc;
	    }
		return super.dispatchTouchEvent(ev);
	}

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	
		int itemPos = Menu.FIRST;
		menu.clear();
		
		// 1.0 Check if Speech Input is visible 
		final boolean speechInputAvailable = HunyDewUtils.isIntentAvailable(getApplicationContext(), RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		MenuItem itemAdd ;
		
		// 1.1 Setup Speech Input if it is available 
	//	if (speechInputAvailable == true)  {
			itemAdd = menu.add(Menu.NONE, LOC_SPEECH_INPUT, Menu.NONE, R.string.speechinputstg);
			itemAdd.setIcon(R.drawable.ic_btn_speak_now);
			itemPos++; 
	//	}

		// 2.0 Register the current location
		itemAdd = menu.add(Menu.NONE,REG_MARKER_LOC, itemPos, R.string.onmapstg );
		itemAdd.setIcon(R.drawable.compass_base);
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()) {
		
			case REG_MARKER_LOC: {
				addLocaOnMap();
				return true;
			}
			
			case LOC_SPEECH_INPUT: {
				final boolean speechInputAvailable = HunyDewUtils.isIntentAvailable(getApplicationContext(), RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				if (speechInputAvailable)
					recvSpeechInput();
				else
					 Toast.makeText(this, "Speech Input not available", Toast.LENGTH_LONG).show();
				return true;
			}
		}
		return false;
	}

	private void addLocaOnMap() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
    	ab.setTitle(getResources().getText(R.string.locaName));
    	ab.setMessage(getResources().getText(R.string.locaNameMesg));
    	
    	final EditText input = new EditText(this);
    	ab.setView(input);
    	
    	ab.setPositiveButton(getResources().getText(R.string.okLabel), new DialogInterface.OnClickListener() {
    		
    		public void onClick(DialogInterface dialog, int whichButton) {
    			
    			locaName = input.getText().toString();
    	    	
    			HunyDewZLogger.write(getApplicationContext(),"HDMap", locaName + " @ Lat: " + lat + " Lon:  " + lon);
    			
    			locaStore.setLocationItem(locaName, lat, lon);
    			
    			// insert the loca info into the database
    			hdDBAdapter.insertHDLoca(locaName, lat, lon);
    			
    			return;
    		}
    	});

    	ab.setNegativeButton(getResources().getText(R.string.cancelLabel), new DialogInterface.OnClickListener() {
    		
    		public void onClick(DialogInterface dialog, int whichButton) {
    			
    			locaName = "locaNoName";
    			
    			HunyDewZLogger.write(getApplicationContext(),"HDMap", locaName + " @ Lat: " + lat + " Lon:  " + lon);
    			
    			return;
    		}
    	});
    	
    	ab.show();
    	
		
	}
	
	private void recvSpeechInput() {
		
		//1.1 setup the intent
		Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		
		//1.2 specify the language model
		speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "the address to register");
		speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3); // return 3 entreis
		speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		HunyDewZLogger.write(getApplicationContext(), "regLoc", 
				"(" + "current Locale: " + Locale.getDefault().getDisplayName() + ")");

		//1.3 start the actual voice recognition activity
		startActivityForResult(speechIntent, VOICE_RECOGNITION_REQUEST);
		
	}

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == VOICE_RECOGNITION_REQUEST && resultCode == RESULT_OK) {
			
			ArrayList<String> addressList;
			
			addressList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			
			int countAddress = addressList.size();
			
			if (countAddress > 1) {
				// ask user to select one
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyLongPress(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		switch (keyCode) {
        case KeyEvent.KEYCODE_CALL:
            Log.d("HDMap", "Call key long press");
            return true;
            
    }
		return super.onKeyLongPress(keyCode, event);
	}
	
	
	public class HunyDewLocasItemizedOverlay extends ItemizedOverlay<OverlayItem> {
		
		// overlayList used to put each OverlayItem objects that must be displayed on the map
		private ArrayList<OverlayItem> overlayList = new ArrayList<OverlayItem>();
		MapView mapView;

		  private Drawable marker;
		  
		// The constructor must define the default marker for each of the OverlayItems.
		// for the marker to get drawn  specify the bounds
		// the center-point at the bottom of the image to be the point at which it is
		// attached to the map coordinates
		public HunyDewLocasItemizedOverlay( Drawable defaultMarker) {
			
			super(boundCenterBottom(defaultMarker) );
		}
		
		public HunyDewLocasItemizedOverlay(MapView mapView, Drawable arg0) {
			
			super(boundCenterBottom(arg0) );
			
			populate();
			
			this.mapView =mapView;
		}

		 public HunyDewLocasItemizedOverlay(Drawable defaultMarker,  int LatitudeE6, int LongitudeE6)  {
			 
				   super(defaultMarker);

				   this.marker=defaultMarker;
				   // create locations of interest
				   GeoPoint myPlace = new GeoPoint(LatitudeE6,LongitudeE6);
				   overlayList.add(new OverlayItem(myPlace ,  "My Place", "My Place"));
				   
				  locPt = myPlace; 
				   populate();
		}
		 
		public void add(OverlayItem overlay) {
			
			overlayList.add(overlay);
			
			populate();
		}
		
		// add new OverlayItems to the ArrayList
		public void addOverlay(OverlayItem overlay) {
			
			overlayList.add(overlay);
			
			populate();
		}
		
		// populate method will call createItem to retrieve each OverlayItem
		@Override
		protected OverlayItem createItem(int i) {
			
			return overlayList.get(i);
			
		}

		// also override the size() method to return the current number of items in the overlayList 
		@Override
		public int size() {
			
			return overlayList.size(); 
		}

		// now prepare to setup the ability to handle touch events on the overlay items 
		public HunyDewLocasItemizedOverlay(Drawable drawable,  Context context) {
			
			super(drawable);
			
			//mContext = context;
		}

		
		public void addOverlayItem(OverlayItem overlayItem) {
		
			if(!overlayList.contains(overlayItem)){
		
				overlayList.add(overlayItem);
			}
		
			populate();
		}
		
		   
		
		public void clear(){
		
			overlayList.clear();
		
		}  
		
		@Override
	    public boolean onTap(int pIndex) {
		
			OverlayItem item = overlayList.get(pIndex);
		
		    item.getTitle();
		
		    mapView.getController().animateTo(item.getPoint());
		
		    Toast.makeText(mapView.getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();      
		
		    return false;
		
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {

			super.draw(canvas, mapView, shadow, when);
			
			// to translate GeoPoint to View / Screen pixels
			Point screenLocPt = new Point();	
			mapView.getProjection().toPixels(locPt, screenLocPt);
			
			Bitmap hdMapBitmap = ((BitmapDrawable) marker).getBitmap();
			
			//canvas.drawBitmap(hdMapBitmap, screenLocPt.x, screenLocPt.y-40, null); //marker is 40 pixels tall
			float xPosMarker, yPosMarker;
			
			xPosMarker = screenLocPt.x-mXCorr; 		yPosMarker = screenLocPt.y-mYCorr;
			//xPosMarker = screenLocPt.x-5; 		yPosMarker = screenLocPt.y-13;
		   //xPosMarker = screenLocPt.x-11; 		yPosMarker = screenLocPt.y-22;
			
			canvas.drawBitmap(hdMapBitmap, xPosMarker, yPosMarker, null); //marker is 40 pixels tall
			
			return true; 
		}
		
		  public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		   // TODO Auto-generated method stub
		   super.draw(canvas, mapView, shadow);
		   
		   boundCenterBottom(marker);
		  }

		/* (non-Javadoc)
		 * @see com.google.android.maps.ItemizedOverlay#onTouchEvent(android.view.MotionEvent, com.google.android.maps.MapView)
		 */
		@Override
		public boolean onTouchEvent(MotionEvent e, MapView mapView) {		
			
			// when user lifts the finger put the toast
			
			if (e.getAction() == 1) {
				
						// this is where the user touched - the Screen co-ordinates
						float  screenX = e.getX();	// used to derive the lat
						float screenY = e.getY(); // used to derive the lon
						
						String s = "scrrenX: " + screenX + " screenY:" + screenY;
						 
						HunyDewZLogger.write(getApplicationContext(),"logss", s);
						
						 // now get the projection
						 Projection p = mapView.getProjection(); 
						 GeoPoint geoPoint = p.fromPixels((int) screenX, (int) screenY);
						 
						 lat = geoPoint.getLatitudeE6()  / 1E6;
						 lon = geoPoint.getLongitudeE6() /1E6;
						 
						 s = "lat: " + lat  + "  lon: " + lon;
						 
						HunyDewZLogger.write(getApplicationContext(),"logss", s);
						
						//	 Toast.makeText(mapView.getContext(), locPt.getLatitudeE6() / 1E6 + "," +  locPt.getLongitudeE6() /1E6 , 
						//             Toast.LENGTH_LONG).show();
						
						//	 locPt = mapView.getProjection().fromPixels((int)e.getX(), (int)e.getY()); 
						 
						mapView.getController().setCenter(locPt);
						mapView.invalidate();	// force MapView to draw				
						
							
						 //			Toast.makeText(mapView.getContext(),  locNow.getLatitudeE6() / 1E6 + "," + locNow.getLongitudeE6() /1E6 , 
						 //                       Toast.LENGTH_LONG).show();
						
						return true;
			}
			else
				return false;
		}
	}
}