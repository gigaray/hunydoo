package net.thepaca.hunydoo;

// http://www.medieval.it/freeware-eula/menu-id-102.html

//import com.mobclix.android.sdk.MobclixAdView;
//import com.mobclix.android.sdk.MobclixAdViewListener;
//import com.mobclix.android.sdk.MobclixMMABannerXLAdView;

// 
// 6/2 ARB Add support for Dossier

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TabHost;


// Full (activity)	lifetime 	onCreate	onDestroy
// visible 			lifetime	onStart		onStop
// active 			lifetime	onResume	onPause

//public class HunyDewAAStartsHere extends TabActivity implements MobclixAdViewListener {

public class HunyDewAAStartsHere extends TabActivity {

	TabHost tabHost;
	
	// Include this for Mobclix ad support
	// MobclixMMABannerXLAdView adview_banner;
	
@Override  
	public void onCreate(Bundle savedInstanceState) {
	
    	HunyDewZLogger.startTrace("AAStartsHere");
    	
		super.onCreate(savedInstanceState);  

		/*
		 Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

		        @Override
		        public void uncaughtException(Thread thread, Throwable ex) {
		            Toast.makeText(HunyDewStartsHere.this, "TOAST", Toast.LENGTH_LONG);

		        }
		    });
		 */
		
		HunyDewEULA.show(this);
		
		HunyDooAppAttach.connect(this, HunyDooAppAttach.INSTALLED);
		
		HunyDewZLogger.write(getApplicationContext(),"AAStartsHere", "---------------------------------------------------------");
		
		try {
			PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versName = manager.versionName ;
			
			HunyDewZLogger.write(getApplicationContext(),"AAStartsHere", versName);
			
		} catch (NameNotFoundException e) {
			
		}
		
		//NumberFormat nf = NumberFormat.getNumberInstance();
		//int minFrac = nf.getMaximumFractionDigits();
		//int maxFrac = nf.getMinimumFractionDigits();
		//int minInt = nf.getMinimumIntegerDigits();
		//int maxInt = nf.getMaximumIntegerDigits();
		
	//	Log.v("AA-Starts-Here", minFrac + ":" + maxFrac + ":" +  minInt + ":" + maxInt);
		
		setContentView(R.layout.main);

		Resources res = getResources(); // Resource object to get Drawables
		 
		tabHost = getTabHost();  // The activity TabHost

		// Resusable TabSpec for each tab
		TabHost.TabSpec recTaskSpec, regLocSpec, monLocSpec, regLocMapSpec, dossierTaskSpec;  
		
		// Intent for each tab
		Intent recTaskIntent, regLocIntent, monLocIntent, regLocMapIntent, dossierTaskIntent;  

	    // 2.0 Prepare for to register the current Location
		// Create an Intent to launch an Activity for the tab (to be reused)
	    regLocIntent = new Intent();
	    regLocIntent.setClass(this, HunyDewActRegisterLoc.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    regLocSpec = tabHost.newTabSpec("regLoc");
	    regLocSpec.setIndicator(getResources().getText(R.string.regLoc), res.getDrawable(R.drawable.ic_tab_acts_loc));
	    regLocSpec.setContent(regLocIntent);
	    tabHost.getChildAt(0).setHapticFeedbackEnabled(true);
	    
	    tabHost.addTab(regLocSpec);
	    
	    // 3.0 Prepare to select the Location on the Map
	    regLocMapIntent = new Intent();
	    regLocMapIntent.setClass(this, HunyDewActRegisterLocOnMap.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    regLocMapSpec = tabHost.newTabSpec("regLocMap");
	    regLocMapSpec.setIndicator(getResources().getText(R.string.locMap), res.getDrawable(R.drawable.ic_tab_acts_map));
	    regLocMapSpec.setContent(regLocMapIntent);
	   // tabHost.getChildAt(1).setHapticFeedbackEnabled(true);
	    
	    tabHost.addTab(regLocMapSpec);
	    
		// 4.0 Prepare for Record Task
		// Create an Intent to launch an Activity for the tab (to be reused)
	    recTaskIntent = new Intent();
	    recTaskIntent.setClass(this, HunyDewActRecordTask.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    recTaskSpec = tabHost.newTabSpec("recTask");
	    recTaskSpec.setIndicator(getResources().getText(R.string.recTask), res.getDrawable(R.drawable.ic_tab_acts_task));
	    recTaskSpec.setContent(recTaskIntent);
	   // tabHost.getChildAt(2).setHapticFeedbackEnabled(true);

	    // tabHost.getChildAt(0).setBackgroundColor(0x880000FF);
	    tabHost.addTab(recTaskSpec);

	    /*
	    // 4.5 Prepare the Dossier Task
		// Create an Intent to launch an Activity for the tab (to be reused)
	    dossierTaskIntent = new Intent();
	    dossierTaskIntent.setClass(this, HunyDooActDossier.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    dossierTaskSpec = tabHost.newTabSpec("dossierTask");
	    dossierTaskSpec.setIndicator(getResources().getText(R.string.dossierTask), res.getDrawable(R.drawable.ic_tab_acts_task));
	    dossierTaskSpec.setContent(dossierTaskIntent);
	    tabHost.addTab(dossierTaskSpec);
	    */
	    
	    // 5.0 Prepare for "Preference"
		// Create an Intent to launch an Activity for the tab (to be reused)
	    monLocIntent = new Intent();
	    monLocIntent.setClass(this, HunyDewActPreferences.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    monLocSpec = tabHost.newTabSpec("monLoc");
	    monLocSpec.setIndicator(getResources().getText(R.string.pref), res.getDrawable(R.drawable.ic_tab_acts_pref));
	    monLocSpec.setContent(monLocIntent);
	    
	    tabHost.addTab(monLocSpec);
	    	
	    // start by Opening the Tab 0 - Current Location
	    tabHost.setCurrentTab(0);

	    /*
	     *  following section h(6.) as been eliminate because the icon sizes now conform to the sizes as laid out in the
	     *  Icon design Guidelines (previously icons were one step bigger - hence the tweak was necessary
	     *  
	    // 6.0 for each tab image (tab icon) and label - tweak the positioning to not make the text fall on 
	    // the image - this is more a kludge - the icon should be redone to position it appropriately 
	    // and remove this piece of code
	    TabWidget vTabs = getTabWidget();
	    
	    DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    
	    int adjustBasedOnDPI[][] = {{DisplayMetrics.DENSITY_LOW, -4, -1},
	    							{DisplayMetrics.DENSITY_MEDIUM, -6, -2},
	    							{DisplayMetrics.DENSITY_HIGH, -10, -3}
	    							//, {DisplayMetrics.DENSITY_XHIGH, -12, -4}  // remember to chnage the counter below
	    };
	    
	    // DENSITY_DEFAULT == DEFAULT_MEDIUM
	    int pullUp = adjustBasedOnDPI[1][1];	
	    int pushDown = adjustBasedOnDPI[1][2];
	    
		for (int i= 2; i >=0; i--) {
			if (metrics.densityDpi == adjustBasedOnDPI[i][0]) {
				pullUp = adjustBasedOnDPI[i][1];
				pushDown = adjustBasedOnDPI[i][2];
				HunyDewZLogger.write("AAStartsHere", "DPI:" + adjustBasedOnDPI[i][0] + 
															" PullUp:" + pullUp +
															" pushDown:" + pushDown);				
			}
		}
	    
	    for (int i = 3; i>=0; i--) {
	    	
		    RelativeLayout rLayout = (RelativeLayout) vTabs.getChildAt(i);
		    
		    //Image / Tab icon is the first element in the layout
		    int left = ((ImageView) rLayout.getChildAt(0)).getPaddingLeft();
		    int top = ((ImageView) rLayout.getChildAt(0)).getPaddingTop();
		    int right = ((ImageView) rLayout.getChildAt(0)).getPaddingRight();
		    int bottom =((ImageView) rLayout.getChildAt(0)).getPaddingRight();
		    
		    ((ImageView) rLayout.getChildAt(0)).setPadding(left, pullUp, right, bottom);

		    // Text / tab Label is the second element in the layout
		    // ((TextView) rLayout.getChildAt(1)).setBackgroundColor(0x8800FF00);
			// ((TextView) rLayout.getChildAt(1)).setTextColor(0xFFFFFFFF);
			left = ((TextView) rLayout.getChildAt(1)).getPaddingLeft();
			top = ((TextView) rLayout.getChildAt(1)).getPaddingTop();
			right = ((TextView) rLayout.getChildAt(1)).getPaddingRight();
			((TextView) rLayout.getChildAt(1)).setPadding(left, top, right,  pushDown);
	    }
	    */ 	
	    // 7.0 Start the HunyDooFulfiller Service
	    Intent fulfillerserviceIntent = new Intent (this, HunyDooFulfillerService.class);
	    fulfillerserviceIntent.putExtra("Name", "Value");
	    startService(fulfillerserviceIntent);
	    
	    // 8.0 setup the view for the Ads served by MobClix
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
		       // adview_banner = (MobclixMMABannerXLAdView) findViewById(R.id.advertising_banner_view);
		       // adview_banner.addMobclixAdViewListener(this);	    
		       // adview_banner.getAd();
	   }
	    
	}


	@Override 
	protected void onDestroy() {
	
		super.onDestroy();
		
		{
		    Intent fulfillerserviceIntent = new Intent (HunyDooFulfillerService.KEEP_RUNNING);
		    
		    stopService (fulfillerserviceIntent);
		}
	
		HunyDewZLogger.stopTrace("AAStartsHere");
		
		
	}
	
	/*
	public void onSuccessfulLoad(MobclixAdView view) {
		HunyDewZLogger.write(getApplicationContext(),"AAStartsHere", "The ad request was successful!");
		view.setVisibility(View.VISIBLE);
	}

	public void onFailedLoad(MobclixAdView view, int errorCode) {
		HunyDewZLogger.write(getApplicationContext(),"AAStartsHere", "The ad request failed with error code: " + errorCode);
		view.setVisibility(View.GONE);
	}

	public void onAdClick(MobclixAdView adView) {
		HunyDewZLogger.write(getApplicationContext(),"AAStartsHere", "Ad clicked!");
	}

	public boolean onOpenAllocationLoad(MobclixAdView adView, int openAllocationCode) {
		HunyDewZLogger.write(getApplicationContext(),"AAStartsHere", "The ad request returned open allocation code: " + openAllocationCode);
		return false;
	}

	public void onCustomAdTouchThrough(MobclixAdView adView, String string) {
		HunyDewZLogger.write(getApplicationContext(),"AAStartsHere", "The custom ad responded with '" + string + "' when touched!");
	}
	public String keywords()	{ return "demo,mobclix";}
	public String query()		{ return "query";}
*/

	/* (non-Javadoc)
	 * @see android.app.TabActivity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle state) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(state);
	}


	/* (non-Javadoc)
	 * @see android.app.TabActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}