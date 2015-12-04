/**
 * 
 * 5/24	AR	enable UI for speech Input
 * 5/28 Moved speen input to LocOnMap
 */
package net.thepaca.hunydoo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Full (activity) lifetime onCreate onDestroy
// visible lifetime onStart onStop
// active lifetime onResume onPause

// we will use the Menu item to display the list of location and user will
// identify which ones have to be deleted

public class HunyDewActRegisterLoc extends Activity implements LocationListener {

	// private static final String TAG = "HunyDew-Location";
	private static final String[] Stg = { "Out of Service", "Unavailable",
			"Available" };

	// Add Unique IDs for the two Menu Item
	static final private int LIST_NEW_HD_LOCAENTRY = Menu.FIRST;
	static final private int SPEECH_INPUT_HD_LOCAENTRY = Menu.FIRST + 1;

	private static final int VOICE_RECOGNITION_REQUEST = 1234;
	
	private TextView hdLocTextView;
	private LocationManager hdLocMgr;
	private String hdLocProvider;

	// for the prototype this all we care about
	private String locaName;
	private double lat, lon;

	// for the dialog box
	// private static final int DIALOG_TEXT_ENTRY = 1;

	// HunyDewLocationStore is an ArryaList of HunyDewLocationItem
	HunyDewLocationStore locaStore = HunyDewLocationStore
			.getLocaStoreSingletonObj();
	HunyDewLocationItem locaItem;

	// adding db support to store the location
	HunyDewDBAdapterLoca hdDBAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// when the user presses center let her enter the location name
		/*
		 * if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
		 * 
		 * AlertDialog.Builder ab = new AlertDialog.Builder(this);
		 * ab.setTitle(getResources().getText(R.string.locaName));
		 * ab.setMessage(getResources().getText(R.string.locaNameMesg));
		 * 
		 * final EditText input = new EditText(this); ab.setView(input);
		 * 
		 * ab.setPositiveButton(getResources().getText(R.string.okLabel), new
		 * DialogInterface.OnClickListener() {
		 * 
		 * public void onClick(DialogInterface dialog, int whichButton) {
		 * 
		 * locaName = input.getText().toString();
		 * 
		 * hdLocTextView.append( "\n" + locaName + " @ " + lat + ",  " + lon);
		 * 
		 * HunyDewZLogger.write(getApplicationContext(),"regLoc","(" + locaName
		 * + ") @ " + lat + ",  " + lon);
		 * 
		 * locaStore.setLocationItem(locaName, lat, lon);
		 * 
		 * // insert the loca info into the database
		 * hdDBAdapter.insertHDLoca(locaName, lat, lon);
		 * 
		 * return; } });
		 * 
		 * ab.setNegativeButton(getResources().getText(R.string.cancelLabel),
		 * new DialogInterface.OnClickListener() {
		 * 
		 * public void onClick(DialogInterface dialog, int whichButton) {
		 * 
		 * locaName = "locaNoName";
		 * 
		 * // hdLocTextView.append("\n(" + locaName + ") @ " + lat + ",  " +
		 * lon);
		 * 
		 * HunyDewZLogger.write(getApplicationContext(),"regLoc",
		 * "  Location canceled ");
		 * 
		 * return; } });
		 * 
		 * ab.show();
		 * 
		 * return true; } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
		 * 
		 * 
		 * }
		 */
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		// 1.1 create and add new menu items
		// 1.2 then assign the icons to those menu items
		// 1.3 finally assign the shortcut
		MenuItem itemAdd = menu.add(0, LIST_NEW_HD_LOCAENTRY, Menu.NONE,
				R.string.addLocaEntry);
		itemAdd.setIcon(R.drawable.add_new_item);
		// itemAdd.setShortcut('0', 'a');

		// speech support moved to LocOnMap due to Licensing 
		// restrictions in GeoCoding when using Google Geocoding
		// 1.0 check if recognition activity is present
		final boolean speechInputAvailable = HunyDewUtils.isIntentAvailable(getApplicationContext(), RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		
		MenuItem itemAdd2 = menu.add(0, SPEECH_INPUT_HD_LOCAENTRY, Menu.NONE,
		R.string.addSpeechInpLoca);
		itemAdd2.setIcon(R.drawable.add_new_item);
		itemAdd2.setEnabled(speechInputAvailable);
			
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {

		case LIST_NEW_HD_LOCAENTRY: {
			addCurrentLoca();
			return true;
		}

		// speech support moved to LocOnMap due to Licensing 
		// restrictions in GeoCoding when using Google Geocoding
		
	//	case SPEECH_INPUT_HD_LOCAENTRY: {
	//		recvSpeechInput();
	//		return true;
	//	}
		}
		return false;
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

	private void addCurrentLoca() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(getResources().getText(R.string.locaName));
		ab.setMessage(getResources().getText(R.string.locaNameMesg));

		final EditText input = new EditText(this);
		ab.setView(input);

		ab.setPositiveButton(getResources().getText(R.string.okLabel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						locaName = input.getText().toString();

						hdLocTextView.append("\n" + locaName + " @ " + lat
								+ ",  " + lon);

						HunyDewZLogger.write(getApplicationContext(), "regLoc",
								"(" + locaName + ") @ " + lat + ",  " + lon);

						locaStore.setLocationItem(locaName, lat, lon);

						// insert the loca info into the database
						hdDBAdapter.insertHDLoca(locaName, lat, lon);

						return;
					}
				});

		ab.setNegativeButton(getResources().getText(R.string.cancelLabel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						locaName = "locaNoName";

						// hdLocTextView.append("\n(" + locaName + ") @ " + lat
						// + ",  " + lon);

						HunyDewZLogger.write(getApplicationContext(), "regLoc",
								"  Location canceled ");

						return;
					}
				});

		ab.show();

		return;

	}

	public void onCreate(Bundle savedInstanceState) {

		HunyDewZLogger.startTrace("hdRegLoc");

		super.onCreate(savedInstanceState);

		// Get the output UI
		hdLocTextView = new TextView(this);
		setContentView(hdLocTextView);

		hdLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// list all providers
		List<String> providers = hdLocMgr.getAllProviders();
		if (providers.size() < 1)
			Toast.makeText(getApplicationContext(),
					getResources().getText(R.string.loca_SP_None),
					Toast.LENGTH_LONG).show();

		for (String provider : providers) {
			HunyDewZLogger.write(getApplicationContext(), "provider", provider);
		}

		// setup the db Access
		hdDBAdapter = new HunyDewDBAdapterLoca(this);

		// hdDBAdapter.open();

		// populateHDLocaList();

		Criteria hdCrit = new Criteria();
		hdCrit.setAccuracy(Criteria.ACCURACY_COARSE);
		hdLocProvider = hdLocMgr.getBestProvider(hdCrit, true);

		Location location = hdLocMgr.getLastKnownLocation(hdLocProvider);

		updateWithNewLocation(location);
	}

	private void updateWithNewLocation(Location location) {

		String latLongString;

		if (location != null) {
			lat = location.getLatitude();
			lon = location.getLongitude();

			latLongString = "\n>>Lat: " + lat + " Lon: " + lon;
		} else {
			latLongString = "\n!!Current location info not available!! ";
		}

		hdLocTextView.append(latLongString);
	}

	@Override
	protected void onResume() {

		super.onResume();

		// open the database
		hdDBAdapter.open();

		// now load the entries from the database
		populateHDLocaList();

		// ms meters
		hdLocMgr.requestLocationUpdates(hdLocProvider, 60000, 100, this);
	}

	/** Stop the updates when Activity is paused */
	@Override
	protected void onPause() {

		super.onPause();

		hdDBAdapter.close();

		hdLocMgr.removeUpdates(this);
	}

	public void onLocationChanged(Location location) {
		updateWithNewLocation(location);
	}

	public void onProviderDisabled(String provider) {
		// let okProvider be bestProvider
		// re-register for updates
		// hdLocTextView.append("\n\nProvider Disabled: " + provider);
		HunyDewZLogger.write(getApplicationContext(), "RegLoc",
				"Provider Disabled: " + provider);
	}

	public void onProviderEnabled(String provider) {
		// is provider better than bestProvider?
		// is yes, bestProvider = provider
		// hdLocTextView.append("\n\nProvider Enabled: " + provider);
		HunyDewZLogger.write(getApplicationContext(), "RegLoc",
				"Provider Enabled: " + provider);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// hdLocTextView.append("\n\nProvider Status Changed: " + provider +
		// ", Status="
		// + Stg[status] + ", Extras=" + extras);
		HunyDewZLogger.write(getApplicationContext(), "RegLoc",
				"Provider Status Changed: " + provider + ", Status="
						+ Stg[status] + ", Extras=" + extras);
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

		HunyDewZLogger.stopTrace("hdRegLoc");
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

		// hdItems.clear();

		locaStore.clear();

		hdLocTextView.setText("");

		int countEntries = hdLocaListCursor.getCount();

		HunyDewZLogger.write(getApplicationContext(), "Found ", countEntries
				+ "entries in Loca DB.");

		if (hdLocaListCursor.moveToFirst()) {

			do {
				String locaName = hdLocaListCursor
						.getString(HunyDewDBAdapterLoca.COL_LOCA_NAME);

				// long created =
				// hdTaskListCursor.getLong(HunyDewDBAdapter.COL_TASK_CREATED);

				// HunyDewTaskItem newTaskItem = new HunyDewTaskItem(task_name);

				// hdItems.add(0, newTaskItem);
				double lat = hdLocaListCursor
						.getDouble(HunyDewDBAdapterLoca.COL_LOCA_LAT);
				double lon = hdLocaListCursor
						.getDouble(HunyDewDBAdapterLoca.COL_LOCA_LON);

				locaStore.setLocationItem(locaName, lat, lon);

				hdLocTextView.append("\n|| " + locaName);

				HunyDewZLogger.write(getApplicationContext(), "updateLocaList",
						locaName + " is at Lat:" + lat + " Lon:" + lon);

			} while (hdLocaListCursor.moveToNext());
		}

		// aa.notifyDataSetChanged();
	}
}
