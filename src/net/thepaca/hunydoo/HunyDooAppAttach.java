/**
 * 
 */
package net.thepaca.hunydoo;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Closeable;
import net.thepaca.hunydoo.HunyDewZLogger;

import com.appattach.tracking.appAttachTracking;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * This is a shim  
 *
 */
public class HunyDooAppAttach {
	
	// for SharedPreferences
	static final int INSTALLED = 0;
    private static final String FIRST_RUN_CONNECTED = "firstRun.connected";
    private static final String FIRST_RUN = "firstRun";
    
    static int countRun = 0;

    /**
     * callback to let the activity know when the user has accepted the EULA.
     */
    static interface OnEulaAgreedTo {

        /**
         * Called when the user has accepted the eula and the dialog closes.
         */
        void onEulaAgreedTo();
    }

    /**
     * connects to appAttach
     *
     * @param activity The Activity to finish if the user rejects the EULA.
     * @return Whether the user has agreed already.
     */
    static boolean connectOne(final Activity activity, final int flag) {
    	
        final SharedPreferences preferences = activity.getSharedPreferences(FIRST_RUN,
                Activity.MODE_PRIVATE);
        
        if (!preferences.getBoolean(FIRST_RUN_CONNECTED, false)) {
        	
            appAttachTracking.event(appAttachTracking.INSTALLED);

            preferences.edit().putBoolean(FIRST_RUN_CONNECTED, true).commit();
        }
        else
        {
        	countRun++;
        	
        	String mesg = String.format("%d runs ", countRun);
        	
        	appAttachTracking.event(mesg);
        	
        }
        return true;
    }

    static boolean connect(final Activity activity, final int flag) {
    	
    	 appAttachTracking.init(activity);
         appAttachTracking.event("Launched");
         
         // append appAttachID to general "buy now" url
         String url = "http://securitycode.store.appattach.com?offer=" + appAttachTracking.getAppAttachID();
         HunyDewZLogger.write(activity, "URL: ",  url);
        
         HunyDewZLogger.write(activity, "Referrer: ", appAttachTracking.getReferrer());
        return true;
    }

}
