package net.thepaca.hunydoo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

//import com.mobclix.android.sdk.MobclixAdView;
//import com.mobclix.android.sdk.MobclixAdViewListener;
//import com.mobclix.android.sdk.MobclixMMABannerXLAdView;


// Full (activity)	lifetime 	onCreate	onDestroy
// visible 			lifetime	onStart		onStop
// active 			lifetime	onResume	onPause

// public class HunyDewActRecordTask extends Activity implements MobclixAdViewListener {
public class HunyDewActRecordTask extends Activity {
	
	// Called when the activity is first created.
	// full lifetime of the activity occurs between onCreate and onDestroy
	
	// Add Unique IDs for the two Menu Item
	static final private int ADD_NEW_HDENTRY = Menu.FIRST;
	static final private int REMOVE_HDENTRY = Menu.FIRST + 1;
	
	private ListView myListview;
	private EditText myEditText;
	private ArrayList <HunyDewTaskItem> hdItems;
	private ArrayAdapter<HunyDewTaskItem> aa;
	
	private boolean addingNew = false;
	
	 HunyDewLocationStore locaStore = HunyDewLocationStore.getLocaStoreSingletonObj();
	 
	 HunyDewTaskItem hdNewItem;
	 
	 public static final int REQUEST_LOCA_SELECT= 1;
	
	 HunyDewDBAdapterTask hdDBAdapter;

	 // Include MobclixMMABannerXLAdView for Ad support
	 // MobclixMMABannerXLAdView adview_banner;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		HunyDewZLogger.startTrace("hdRecTask");
		
		super.onCreate(savedInstanceState);
	
		// inflate the project view
		setContentView(R.layout.rec_task);
		
		// Get references to UI widgets
		// when pulling widgets from manin.xml use R.id to access them

		// 1.0 Work on the List View of the items
		// 1.1 pull "ListView" layout out from the main.xml
		myListview = (ListView) findViewById(R.id.hunyListView);

		// 1.2 pull the editable "edit View" layout from the main.xml
		// EditText is a thin shim over TextView that configures itself to be
		// editable
		myEditText = (EditText) findViewById(R.id.hunyEditText);
		
		// final indicates it cannot be changed later
		// now create the back-end for the List View Widget
		// 1.3 create the array list of to do list items
		// use the Item layout created for hunyDew
		hdItems = new ArrayList<HunyDewTaskItem>();
		
		aa = new ArrayAdapter<HunyDewTaskItem>(this, R.layout.hditemview, hdItems);
		
		// 1.3.1 bind the array adapter to the listView
		myListview.setAdapter(aa);
		
		// 1.4 DB Access
		hdDBAdapter = new HunyDewDBAdapterTask(this);
		
	//	hdDBAdapter.open();
		
	//	populateHDTaskList();
		
		// 1.5 now register the callback to be invoked when a key is pressed in
		// this view.
		myEditText.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					switch (keyCode) {
					case KeyEvent.KEYCODE_ENTER: 
		//			case KeyEvent.KEYCODE_DPAD_CENTER:
						
						hdNewItem = new HunyDewTaskItem(myEditText.getText().toString());
						
						// insert the task into the database
						// hdDBAdapter.insertHDTask(hdNewItem);
						updateTaskListArray();
						
						hdItems.add(0, hdNewItem);

						// First get the location
						ArrayList<String> locaList = locaStore.getLocaList();
						
						Log.v("locaStore has", Integer.toString(locaList.size()));
						
						// Now launch the list box with the name of the place
						Intent intentLocaList = new Intent(HunyDewActRecordTask.this, HunyDewLocaListSelector.class);
						startActivityForResult(intentLocaList, REQUEST_LOCA_SELECT);
				
						
						myEditText.setText("");
						aa.notifyDataSetChanged();
						cancelAdd();
						return true;
						
					default:
						break;
					}
				return false;
			}

		});

		// 2.0 creating the context menu ..
		registerForContextMenu(myListview);
		
		// 3.0 setup the view for the Ads Served by MobClix
	    // show ads only if the pref is set to true
	    Context ctx = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
 	    int valueRead;
	    valueRead = prefs.getInt(HunyDewActPreferences.PREF_ADS_DISPLAY_MODE, 
			   				HunyDewActPreferences.ADS_SETTINGS.TURN_ADS_OFF.ordinal());
	    int ads_off = HunyDewActPreferences.ADS_SETTINGS.TURN_ADS_OFF.ordinal();
	    int ads_on = HunyDewActPreferences.ADS_SETTINGS.TURN_ADS_ON.ordinal();
	   
	    if (valueRead == ads_off) {
		   // do nothing
	    }
	   
	    if (valueRead == ads_on) {
	    	   //
		       // adview_banner = (MobclixMMABannerXLAdView) findViewById(R.id.advertising_banner_view);
		       // adview_banner.addMobclixAdViewListener(this);	    
		       // adview_banner.getAd();
	    }      
		
		
	}

	// store a cursor over all the
	Cursor hdTaskListCursor;
	private void populateHDTaskList() {

		hdTaskListCursor = hdDBAdapter.getAllHDTaskItemsCursor();
		startManagingCursor(hdTaskListCursor);
		
		updateTaskListArray();
		
		return;
	}

	private void updateTaskListArray() {
	
		String created;
		
		hdTaskListCursor.requery();
		
		hdItems.clear();
		
		if (hdTaskListCursor.moveToFirst()) {
			
			do {
				
				String task_name = hdTaskListCursor.getString(HunyDewDBAdapterTask.COL_TASK_NAME);
				
				created = hdTaskListCursor.getString(HunyDewDBAdapterTask.COL_TASK_CREATED);

				String locaName = hdTaskListCursor.getString(HunyDewDBAdapterTask.COL_TASK_LOCA_NAME);
				
				HunyDewTaskItem newTaskItem = new HunyDewTaskItem(task_name, created, locaName);
				
				hdItems.add(0, newTaskItem);
				
			} while(hdTaskListCursor.moveToNext());
		}
		
		aa.notifyDataSetChanged();
	}

	// at the end of the LocaListSelector Activity the user woiuld have assigned a task to 
	// the newly created task
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		
		case REQUEST_LOCA_SELECT:
			
			if (resultCode == RESULT_CANCELED) {
				Log.v("locaStoreSelection", " CANCELED");
			}
			else {
				Log.v("locaStoreSelection", data.getStringExtra("SelectedLocation"));
				
				hdNewItem.setLocaName(data.getStringExtra("SelectedLocation"));
				
				hdDBAdapter.open();
				
				// insert the task into the database
				hdDBAdapter.insertHDTask(hdNewItem);
				
				hdDBAdapter.close();
			}
			break;
			
		default:
			break;
			
		}
		
	}
	
	/* 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		
		// 1.1 create and add new menu items
		// 1.2 then assign the icons to those menu items
		// 1.3 finally assign the shortcut
		MenuItem itemAdd = menu.add(0, ADD_NEW_HDENTRY, Menu.NONE, R.string.addItem);
		itemAdd.setIcon(R.drawable.add_new_item);
		//itemAdd.setShortcut('0', 'a');
		
		MenuItem itemRemove = menu.add(0, REMOVE_HDENTRY, Menu.NONE, R.string.removeItem);
		itemRemove.setIcon(R.drawable.remove_item);
		//itemRemove.setShortcut('1', 'r');
		
		//MenuItem itemAdd
		
		/*
		MenuInflater inflaterTask = getMenuInflater();
		
		inflaterTask.inflate(R.menu.task_opt_menu, menu);
		*/
		return true; 
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override public boolean onPrepareOptionsMenu(Menu menu) {
		
		super.onPrepareOptionsMenu(menu);
		/*
		int idx = myListview.getSelectedItemPosition();
		
		String removeTitle = getString(addingNew ? R.string.cancelLabel : R.string.removeItem);
		
		MenuItem itemRemove = menu.findItem(REMOVE_HDENTRY);
		itemRemove.setTitle(removeTitle);
		itemRemove.setVisible(addingNew || idx > -1);
		*/
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.setHeaderTitle(R.string.hdrTitle);
		menu.add(0, REMOVE_HDENTRY, Menu.NONE, R.string.removeItem);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		int index = myListview.getSelectedItemPosition();
		
		switch(item.getItemId()) {
		
			case REMOVE_HDENTRY: {
				if (addingNew)  
					cancelAdd(); 
				else 
					removeHDItem(index);
				
				return true;
			}
			
			case ADD_NEW_HDENTRY: {
				addNewHDItem();
				return true;
			}
			
			case R.id.TaskSpeech:
				Toast.makeText(this, "speech", Toast.LENGTH_LONG);
				break;
				
			case R.id.TaskText:
				Toast.makeText(this, "text", Toast.LENGTH_LONG);
				break;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		super.onContextItemSelected(item);
		
		switch (item.getItemId()) {
		
			case REMOVE_HDENTRY: {
				AdapterView.AdapterContextMenuInfo menuInfo;
				
				menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
				int index = menuInfo.position;
				
				removeHDItem(index);
				
				return true;
			}
		}
		
		return false;
	}
	
	// Menu item selection handlers
	
	private void cancelAdd() {
		
		addingNew = false;
		
		myEditText.setVisibility(View.GONE);
	}
	
	private void addNewHDItem() {
		addingNew = true;
		
		myEditText.setVisibility(View.VISIBLE);
		
		myEditText.requestFocus();
	}
	
	private void removeHDItem(int index) {

		HunyDewTaskItem hdItemToRemove =  hdItems.get(index);
		
		int _rowIndex = hdDBAdapter.getHDTaskItemRowIndex(hdItemToRemove.hdTaskName);
		
		hdDBAdapter.deleteHDTask(_rowIndex);
		
		updateTaskListArray();
		
	}
	

	@Override
	public void onConfigurationChanged(Configuration _newConfig) {

		super.onConfigurationChanged(_newConfig);

		if (_newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

		}

		if (_newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {

		}

	}

	// Called after onCreate has finished - used to restore UI state
	// The bundle has also been passed to onCreate
	@Override 
	public void onRestoreInstanceState (Bundle savedInstanceState) {
		
		super.onRestoreInstanceState(savedInstanceState);
		
		// TODO - to restore UI state from the savedInstanceState
	}
	
	// called before subsequent visible lifetimes for an activity process
	// Called after onStop
	// followed by calls to onStart and onResume
	@Override
	public void onRestart() {
		
		super.onRestart();
		
		aa.notifyDataSetChanged();
		
		// TODO - load changes knowing that the activity has already been 
		// visible within this process
	}
	
	// called at the start of the visible lifetime
	// visible lifetime bound by onStart and onStop
	// onStart and onStop used to register/unregister broadcast receivers
	@Override
	public void onStart() {
		
		super.onStart();
		
		// TODO - apply any required UI changes now that activity is visible
	}
	
	// called at the start of the active lifetime
	// going foreground and ready to receive user inputs
	@Override
	public void onResume() {
		
		super.onResume();
		
		// TODO - resume any paused UI updates, threads or process as 
		// required by the activity but suspended when it was made inactive
		
		hdDBAdapter.open();
		
		populateHDTaskList();
	}
	
	// called to save UI state change at the end of the active lifestyle
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		
		super.onSaveInstanceState(savedInstanceState);
		
		// TODO - save UI state changes to the saved Instnce state - this
		// bundle will be passed to onCreate if the process is killed and
		// restarted
	}
	
	//called at the end of active lifetime
	// counterpart to onResume
	@Override
	public void onPause() {
		// suspend UI updates, threads or CPU intensive processes that don't 
		// need to be updated when the Activity isn't the active foreground 
		// activity
		
		super.onPause();
		
		// close the connection to database
		hdDBAdapter.close();
	}
	
	// called at the end of the visible lifetime
	// called at no longer visible to the user :-(
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
		
		HunyDewZLogger.stopTrace("hdRecTask");
	}
	
	/*
	public void onSuccessfulLoad(MobclixAdView view) {
		HunyDewZLogger.write(getApplicationContext(),"TaskTab", "The ad request was successful!");
		view.setVisibility(View.VISIBLE);
	}

	public void onFailedLoad(MobclixAdView view, int errorCode) {
		HunyDewZLogger.write(getApplicationContext(),"TaskTab", "The ad request failed with error code: " + errorCode);
		view.setVisibility(View.GONE);
	}

	public void onAdClick(MobclixAdView adView) {
		HunyDewZLogger.write(getApplicationContext(),"TaskTab", "Ad clicked!");
	}

	public boolean onOpenAllocationLoad(MobclixAdView adView, int openAllocationCode) {
		HunyDewZLogger.write(getApplicationContext(),"TaskTab", "The ad request returned open allocation code: " + openAllocationCode);
		return false;
	}

	public void onCustomAdTouchThrough(MobclixAdView adView, String string) {
		HunyDewZLogger.write(getApplicationContext(),"TaskTab", "The custom ad responded with '" + string + "' when touched!");
	}
	public String keywords()	{ return "location,mobclix";}
	public String query()		{ return "query";}
	*/
}