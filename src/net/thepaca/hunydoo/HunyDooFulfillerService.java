/**
 * 
 */
package net.thepaca.hunydoo;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

/**
 * @author MomNDad
 *
 */

//
public class HunyDooFulfillerService extends Service {

	public static final String KEEP_RUNNING= "KEEP_RUNNING";
	
	// hook into the NDStartsHere
//	public static ServiceUpdateUIListener UI_UPDATE_LISTENER;
	private static HunyDewAAStartsHere MAIN_ACTIVITY;
	
	public static void setMainActivity(HunyDewAAStartsHere activity) {
		MAIN_ACTIVITY = activity;
	}
	
	//public static void setUpdateListener(ServiceUpdateUIListener l) {
	//	UI_UPDATE_LISTENER = l;
	//}
	
	
	// data internal to the Service
	//private static Hashtable<String, Hashtable<DataKeys, String>> fulfillerData = 
//		new Hashtables<String, Hashtable<DataKeys, String>>();
	
	private Timer hdFulfillerTimer = new Timer();
	private static final long UPDATE_INTERVAL = 180000;  // time in milliseconds between successive task executions
	private static final long DELAY_TOSTART = 15000;	// delay in milliseconds before task is to be executed.
	
	private String hdLocProvider;
	// Location currentLocation;
	LocationManager fulfillerLocManager;
	 
	HunyDewLocationStore locaStore = HunyDewLocationStore.getLocaStoreSingletonObj();
	 
	 // adding db support to store the location
	 HunyDewDBAdapterLoca hdDBAdapterLoca;
	 
	 // handle list of tasks
	 HunyDewDBAdapterTask hdDBAdapterTask;
	 Cursor hdTaskListCursor;
	 
	 // Notification related
	 private Notification hdLocaReachedNotification;

	private int fulfillerServiceStartID;
	 public static final int LOCA_REACHJED_NOTIFICATION_ID = 1;
	 
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		
		HunyDewZLogger.startTrace("fulfillerf");
		
		super.onCreate();
		
		fulfillerLocManager =
				(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener  fulfillerLocListener = new HunyDooLocationListener();
		
		// if (LocationManager.GPS_PROVIDER!=null) 
		//   hdLocProvider = LocationManager.GPS_PROVIDER;

		Criteria hdCrit = new Criteria();
	    hdCrit.setAccuracy(Criteria.ACCURACY_COARSE);
	    hdLocProvider = fulfillerLocManager.getBestProvider(hdCrit, true); 
	    															//ms  // meter
		fulfillerLocManager.requestLocationUpdates(hdLocProvider, 60000, 100, fulfillerLocListener);
		
//	    Location currentLocation = fulfillerLocManager.getLastKnownLocation(hdLocProvider);
		 
	    // Instantiate the notification
	    int iconReached = R.drawable.push_pin; // TBD - eventually this will be small size launcher Icon  
	    String textReachedLoca = getResources().getText(R.string.notiMesg).toString();
	    long whenReached = System.currentTimeMillis();	    
	    hdLocaReachedNotification = new Notification(iconReached, textReachedLoca, whenReached);
	    
	    hdLocaReachedNotification.defaults |= Notification.DEFAULT_SOUND;
		hdLocaReachedNotification.defaults |= Notification.DEFAULT_LIGHTS;
		hdLocaReachedNotification.defaults |= Notification.DEFAULT_VIBRATE;
		hdLocaReachedNotification.audioStreamType = AudioManager.STREAM_NOTIFICATION;
		
		// Repeat audio until the user responds
		hdLocaReachedNotification.flags |= Notification.FLAG_INSISTENT;
		
		// Flashing Lights
		hdLocaReachedNotification.ledARGB = 0xff00ff00;
		hdLocaReachedNotification.ledOnMS = 300;
		hdLocaReachedNotification.ledOffMS = 1000;
		hdLocaReachedNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
	    // define the notification's expanded message and intent
	    CharSequence contentTitle = getResources().getText(R.string.notiTitle);
	    CharSequence contentText = getResources().getText(R.string.notiMesg);
	    Intent notificationIntent = new Intent(this, HunyDewAAStartsHere.class);
	    
	    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	    
	    hdLocaReachedNotification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
	    	   
	   // now start the service 
		startFulfillerService();
		
		if (MAIN_ACTIVITY != null) {
			
			Log.i(getClass().getSimpleName(),"MyService Started");
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		shutdownFulfillerService();
		
		if (MAIN_ACTIVITY != null) {
			
			Log.i(getClass().getSimpleName(),"MyService Stopped");
		}
		
		// now that the timer has been canceled - stop the service
		stopSelf(fulfillerServiceStartID);
		
		HunyDewZLogger.stopTrace("fulfillerf");
	}

//
// Timer management for the service
//
	
	private TimerTask executePositionUpdate = 
		
		new TimerTask() { 
		
			public void run() { 
			
				_getPositionUpdate();
			}
		} ;
		
	private void startFulfillerService() {
		
		hdFulfillerTimer.scheduleAtFixedRate(executePositionUpdate, DELAY_TOSTART, UPDATE_INTERVAL);
		
		Log.i(getClass().getSimpleName(), "Timer started");
	}

	private void shutdownFulfillerService() {
		
		if (hdFulfillerTimer != null)
			hdFulfillerTimer.cancel();
		
		Log.i(getClass().getSimpleName(), "Timer stopped");
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 * 
	 * Called whenever the Service is started with a call to startService
	 * this function can be executed several times
	 * 
	 * It returns a value that will determine how the system will respond if this
	 * service is restarted after being killed
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// return super.onStartCommand(intent, flags, startId);
		
		if ((flags & START_FLAG_RETRY) == 0) {
			
		}
		else {
			
		}
		fulfillerServiceStartID = startId;
		
		return Service.START_STICKY; // will ensure this onStartCommand is called everytime the Service is restarted
	}

	public static Hashtable<String, Hashtable<String, String>> DataFromServlet = 
		new Hashtable<String, Hashtable<String, String>>();
	
	private static double prevLat, prevLon;
	
	private void _getPositionUpdate() {
		
		double lat =  0.0, lon = 0.0;
		
		String latlonString;
		
		// Log.i(getClass().getSimpleName(), " In getPositionUpdate ");
		
		Location currentLocation = fulfillerLocManager.getLastKnownLocation(hdLocProvider);
		
		if (currentLocation != null) {
			  lat = currentLocation.getLatitude();
			  lon = currentLocation.getLongitude();
			 
			  latlonString = "\n>>Lat: " + lat + " Lon: " + lon;
		 }
		 else {
			 
			 latlonString = "\n!!Current location info not available!! ";
		 }
		
		//Log.i(getClass().getSimpleName(), latlonString);
		
		if (currentLocation == null)  {
			
			 HunyDewZLogger.write(this,"_getPositionUpdate", "Current location info not available");
			 
			 return;
		}

		 HunyDewZLogger.write(this,"_getPositionUpdate", latlonString + " >> prevLat: " + prevLat + " prevLon: " + prevLon);
		 
		// before we do any processing disable the timer on the fulfiller service
		//shutdownFulfillerService();
		
		int iPrevCurrentLat = Double.compare(lat, prevLat);
		
		int iPrevCurrentLon = Double.compare(lon, prevLon);
		
		if ((iPrevCurrentLat == 0) && (iPrevCurrentLon == 0)) {
			// position has not changed since the last check so no 
			// further processing is required so return
			return;
		}
		
		//if (hdDBAdapterLoca.isDirty()) {
			
			// hdDBAdapterLoca.open();
			
			// hdDBAdapterLoca.close();
			
		//	hdDBAdapterLoca.removeDirtyFlag();
		//}
		
		prevLat = lat;
		prevLon = lon;
		
		String currentLocaName = locaStore.getLocaName(lat, lon);
		String taskLocaName;
		if (currentLocaName != null) {
			
			Log.i(getClass().getSimpleName(), currentLocaName);

			 HunyDewZLogger.write(getApplicationContext(),"_getPositionUpdate", "Known Location: " + currentLocaName); 
				
			hdDBAdapterTask = new HunyDewDBAdapterTask(this);
			
			hdDBAdapterTask.open();
			
			hdTaskListCursor = hdDBAdapterTask.getAllHDTaskItemsCursor();
			
			while (hdTaskListCursor.moveToNext()) {
				
				taskLocaName = hdTaskListCursor.getString(hdDBAdapterTask.COL_TASK_LOCA_NAME);
				
				if (currentLocaName.equalsIgnoreCase(taskLocaName) == true) {
					
					String taskName = hdTaskListCursor.getString(hdDBAdapterTask.COL_TASK_NAME);
										
					Log.i(getClass().getSimpleName(), taskName);
					
					 HunyDewZLogger.write(getApplicationContext(),"_getPositionUpdate", "Found Task: " + taskName); 
									
					 // now prepare the Notification
					// Get a reference to the Notification Manager
					
					String svcName = Context.NOTIFICATION_SERVICE;
					NotificationManager notiMgr = (NotificationManager) getSystemService(svcName);
					
					Context context = getApplication(); 
					String expandedText = taskName;
					String expandedTtitle  = getResources().getText(R.string.atLoca) + taskLocaName;
					
					Intent startActivityIntent = new Intent(HunyDooFulfillerService.this, 
														HunyDewAAStartsHere.class);
					
					PendingIntent launchIntent = 
						PendingIntent.getActivity(context, 0, startActivityIntent, 0);
				
					boolean isDebugEmulator = HunyDewUtils.signedWithDebugKey(this, this.getClass());
					if (isDebugEmulator == false) {
						hdLocaReachedNotification.defaults |= Notification.DEFAULT_SOUND;
						hdLocaReachedNotification.defaults |= Notification.DEFAULT_LIGHTS;
						hdLocaReachedNotification.defaults |= Notification.DEFAULT_VIBRATE;
						hdLocaReachedNotification.audioStreamType = AudioManager.STREAM_NOTIFICATION;
						
						// Repeat audio until the user responds
						hdLocaReachedNotification.flags |= Notification.FLAG_INSISTENT;
						
						// Flashing Lights
						hdLocaReachedNotification.ledARGB = 0xff00ff00;
						hdLocaReachedNotification.ledOnMS = 300;
						hdLocaReachedNotification.ledOffMS = 1000;
						hdLocaReachedNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
					}
					else {
						hdLocaReachedNotification.defaults |= Notification.DEFAULT_SOUND;
					}
					
					hdLocaReachedNotification.setLatestEventInfo(context, expandedTtitle, expandedText, launchIntent);
					
					hdLocaReachedNotification.when = java.lang.System.currentTimeMillis();
					
					notiMgr.notify(LOCA_REACHJED_NOTIFICATION_ID, hdLocaReachedNotification);

					 HunyDewZLogger.write(getApplicationContext(),"_getPositionUpdate", expandedText + expandedTtitle); 
					 
					//Toast AALocaToast = Toast .makeText(getApplicationContext(), expandedTtitle, Toast.LENGTH_LONG);
					
					//AALocaToast.setGravity(Gravity.TOP, 0, 0);

					//AALocaToast.show();
					
					break;
					
				}
			}
			
			hdDBAdapterTask.close();
			
		}
		
		// now that we have a location name verify if this name exists in the 
		// TaskList
		
		
		// now that we are done with chores start it again
		//startFulfillerService();
	}

	private  String currentLocaMovedInto;
	
	void setLocaMovedInto(String locaName) {
		
		currentLocaMovedInto = locaName;
	}
	
	String getLocaMovedInto() {
		
		return currentLocaMovedInto;
	}
	
	/*
	// It returns 0 if both the values are equal, returns value less than 0 if
	// d1 is less than d2, and returns value grater than 0 if d1 is grater than d2.
	
	private int myCompare(double lat1, double prevLat2) {
		
		// Math.abs(lat-prevLat2) < ACCEPTABLE_ERROR
		
		return 0;
	}
  */
	
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts decimal degrees to radians             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*
	 * private double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}
*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts radians to decimal degrees             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*
	  private double rad2deg(double rad) {
	
	  return (rad / Math.PI * 180.0);
	}
  */
	/*
	private int myCompare( double lat1, double lon1, double lat2, double lon2) {
		
		  double theta = lon1 - lon2;
		  
		  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		  
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 60 * 1.1515;

		  dist = dist * 1.609344 * 1000;

		  if (dist < 100)
			  return 0;
		  
	    return 1;
	}
  */
}
