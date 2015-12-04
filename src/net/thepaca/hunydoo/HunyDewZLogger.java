package net.thepaca.hunydoo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.util.Log;

public class HunyDewZLogger {

	static PrintWriter zLogWriter = null;
	
	static String logFileName;
	
	static File sdDir;
	
	static  boolean shouldLogSettingBeChecked = false;  // use this to track if the user settings has been checked
	static boolean isloggingEnabled = false;  // it is the user setting
	
	// static URI uri;
	
	private static void Initialize() {
		
		try {
				
			sdDir = Environment.getExternalStorageDirectory();
			
			File logDir =  new File (sdDir.getAbsolutePath() + "/hd/log");
			
			if (logDir.isDirectory() != true) { // (logDir.exists() != true) && 
				
				 boolean retVal = logDir.mkdirs();
				 
				 Log.v("ZLogger", "mkdirs for " + logDir.getCanonicalPath() + " returns " + retVal);
			}
			
			logFileName = sdDir.getAbsolutePath() + "/hd/log/" + VERSION.RELEASE + "_hdOut.log";
						
			if (sdDir.canWrite()) {
				
				File logFile = new File (logFileName);

				FileWriter logFileWriter = new FileWriter(logFile, true); // append to existing file
				
				zLogWriter = new PrintWriter(logFileWriter);
							
			}
			
			if (sdDir.canRead()) {
				//Log.v("ZLogger", "Can read " + sdDir.getCanonicalPath());
			}
						
			
		} catch (IOException e) {
			Log.e("ZLogger", "IO Exception with log file: " + e.getMessage());
		}
	}
	public static void resetLogEnableFlag()
	{
		shouldLogSettingBeChecked = false;
	}
	 public static void write(Context ctx, String text1, String text2 )
	    {
		 // 1.0 one time check to see if logging is enabled for this run
		if (shouldLogSettingBeChecked != true) 
		{
	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	 	    int valueRead;
		    valueRead = prefs.getInt(HunyDewActPreferences.PREF_LOG_SETTINGS_MODE, 
				   				HunyDewActPreferences.LOG_SETTINGS.TURN_LOGS_WRITE_OFF.ordinal());
		    int logWrite_off = HunyDewActPreferences.LOG_SETTINGS.TURN_LOGS_WRITE_OFF.ordinal();
		    int logWrite_on = HunyDewActPreferences.LOG_SETTINGS.TURN_LOGS_WRITE_ON.ordinal();
		   
		    if (valueRead == logWrite_off) {
			   // do nothing
		    	isloggingEnabled = false;
		    }
		   
		    if (valueRead == logWrite_on) {
		    	isloggingEnabled = true;
		    }
		    shouldLogSettingBeChecked = true;
		}
	
		// 2.0 if logging is enabled the write to log everytime
		// clean up and make this efficient!
		 if (isloggingEnabled == true) 
		 {
			 
	        if( zLogWriter == null )
	        	Initialize();
	      
	        if( zLogWriter != null )
	        {
		        Calendar rightNow = Calendar.getInstance();
		        long day = rightNow.get(Calendar.DAY_OF_YEAR);
				long hour = rightNow.get(Calendar.HOUR_OF_DAY);
		        long minutes = rightNow.get( Calendar.MINUTE );
		        long seconds = rightNow.get( Calendar.SECOND );
		        long ms = rightNow.get( Calendar.MILLISECOND );
		      
		        String time = day + ":" + hour + ":" + minutes + ":" + seconds + "." + ms;
	
		        zLogWriter.write( time + " " + text1 + " " + text2 + "\n" );
		        zLogWriter.flush();
	        }
	        else
	        	Log.e(text1, "In zLogFile Write: zLogFile not available");
	        
	        Log.v( text1, text2 );
	        
	        close();
		 }
	   }
	 
	 public static void close()
	    {
	        if( zLogWriter != null ) {
	        	zLogWriter.close();
	        	zLogWriter = null;
	        }
	    }
	 
	 public static void startTrace(String name) {
		 
		 //Debug.startMethodTracing(name);
	 }
	 
	 public static void stopTrace(String name) {
		 
		 //Debug.stopMethodTracing();
	 }
}
