/**
 * 
 */
package net.thepaca.hunydoo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author MomNDad
 *
 */

enum HunyDewTaskStatus {	
	SHOWN,		// The task is shown 
	NOT_SHOWN,	// the task is not shown 
	IGNORED, 	// this task has been marked to be ignored
	PERFORMED	// this task has been performed
};

enum HDTaskShare {
	NO_SHARE,			// dont share this task or request other help
	SHARE_WITH_CONTACT,
	SAHRE_WITH_ETAILER
};

public class HunyDewTaskItem {

	/**
	 * 
	 */
	
	String 				hdTaskName;	// name of the task
	Date 				hdTaskCreated;		// when was the task created
	HunyDewTaskStatus 	hdTaskStatus; // status of this entry-
	String 				hdTaskLocation; // where should this task be performed - the location
	
	// start the constructor
	public HunyDewTaskItem(String _taskName, String _taskLocation) {
		hdTaskName = _taskName;
		hdTaskLocation = _taskLocation;
		hdTaskCreated = new Date(java.lang.System.currentTimeMillis());
		hdTaskStatus = HunyDewTaskStatus.SHOWN;
	}
	
	public HunyDewTaskItem(String _taskName) {
		hdTaskName = _taskName;
		hdTaskLocation = "None";
		hdTaskCreated = new Date(java.lang.System.currentTimeMillis());
		hdTaskStatus = HunyDewTaskStatus.SHOWN;
	}
	
	public HunyDewTaskItem(String _taskName, String dateCreated, String locaName) {
		hdTaskName = _taskName;
		hdTaskLocation = locaName;
		
		if (dateCreated == null)
			dateCreated = "1/1/2000";
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		
		try {
			hdTaskCreated = sdf.parse(dateCreated);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hdTaskCreated = new Date(java.lang.System.currentTimeMillis());
		}
		
		hdTaskStatus = HunyDewTaskStatus.SHOWN;
	}
	
	// getter
	public String getHDTask() {
		return hdTaskName;
	}
	
	public String getDatecreated() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		
		String dateStr = sdf.format(hdTaskCreated);
		
		return dateStr ;
	}
	
	public String getLocation() {
			return hdTaskLocation;
	}
	
	public HunyDewTaskStatus getStatus() {
		return hdTaskStatus;
	}
	
	@Override
	public String toString() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd"); // not localized??
		
		String dateStr = sdf.format(hdTaskCreated);
		
		return dateStr + "|" + hdTaskName + "|" + hdTaskLocation;
	}
	
	public void setLocaName(String locaName) {
		
		hdTaskLocation = locaName;
		
		return;
	}
}
