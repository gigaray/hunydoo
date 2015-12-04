/**
 * 
 */
package net.thepaca.hunydoo;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

/**
 * @author MomNDad
 *
 * this adapter class encapsulates database interaction by providing strongly 
 * typed methods for 
 *   (i)  add/delete/update
 *   (ii) open/close/query
 * 
 * Also used to record db Constants such as table and column names
 * 
 */
public class HunyDewDBAdapterTask {

	/**
	 * 
	 */
	private static final String DB_TASK_NAME = "hdDBTask.db";
	private static final int DB_TASK_VERSION = 1;
	private static final String DB_TASK_TABLE = "hdTaskStore";
	
	//index (primary) key for the two table
	public static final String KEY_TASK_ID = "_id";
		
	// now enter each column name and index for HDTask
	public static final String KEY_TASK_NAME = "_TaskName";
	public final static int COL_TASK_NAME = 1;
	public static final String KEY_TASK_FLAGS = "_t_status"; // Completed
	public static final int COL_TASK_FLAGS = 2;
	public static final String KEY_TASK_LOCA_NAME = "_t_l_name"; // Foreign key 
	public final static int COL_TASK_LOCA_NAME = 3;
	public static final String KEY_TASK_CONTACT_STATUS = "_t_contact_flag";
	public static final int COL_TASK_CONTACT_STATUS = 4;
	public static final String KEY_TASK_CREATED = "_l_create_date";
	public static final int COL_TASK_CREATED = 5;
	public static final String KEY_TASK_NOTES = "_t_notes";
	public static final int COL_TASK_NOTES = 6;
	
	private SQLiteDatabase db;				// variable to hold db instance
	private final Context context;			// context of the app using the db
	private HunyDewDBOpenHelper hdHelper;	// db open/upgrade helper
	
	//private SQLiteStatement insertStmt;
	//private static final String DB_INSERT_TASK = "insert into" + DB_TASK_TABLE + "(name) values (?)";
	
	// store an instance of the HunyDewDBOpenHelper
	public HunyDewDBAdapterTask(Context _ctxt) {
		
		context = _ctxt;
		
		hdHelper = new HunyDewDBOpenHelper(context, DB_TASK_NAME, null, DB_TASK_VERSION);
	}
	
	public void open() throws SQLiteException {

		try {
			
			db = hdHelper.getWritableDatabase();
		}
		catch (SQLiteException ex) {
			// if writeable instaqnce cannot be opened
			
			db = hdHelper.getReadableDatabase();
		}
}

	public void close() {
		if (db != null)
			if (db.isOpen())
				db.close();
	}
	
	public long insertHDTask(HunyDewTaskItem hdTask) {
		
		// create a new row  of values to insert
		ContentValues newHDTaskValues = new ContentValues();
		
		// now assign value to each row
		newHDTaskValues.put(KEY_TASK_NAME, hdTask.getHDTask());
		newHDTaskValues.put(KEY_TASK_LOCA_NAME, hdTask.getLocation());
		newHDTaskValues.put(KEY_TASK_CREATED, hdTask.getDatecreated());
		
		// now insert the row
		return db.insert(DB_TASK_TABLE, null, newHDTaskValues);
	}
	
	public boolean deleteHDTask(long _rowIndex) {
	
		return db.delete(DB_TASK_TABLE, KEY_TASK_ID + "=" + _rowIndex, null) > 0;
	}
	
	public boolean deleteHDTask(String taskName) {
		
		return db.delete(DB_TASK_TABLE, KEY_TASK_NAME + "=" + taskName, null) > 0;
	}
	
	public void deleteAllHDTask() {
		this.db.delete(DB_TASK_TABLE, null, null);
	}
	
	public boolean updateHDTask (long _rowIndex, String _task) {
		
		ContentValues newHDTaskValues = new ContentValues();
		
		newHDTaskValues.put(KEY_TASK_NAME, _task);
		
		return db.update(DB_TASK_TABLE, newHDTaskValues, KEY_TASK_ID + "=" + _rowIndex, null) > 0;
	}
	
	public boolean updateHDTask (HunyDewTaskItem hdTask) {
		
		ContentValues newHDTaskValues = new ContentValues();
		
		newHDTaskValues.put(KEY_TASK_NAME, hdTask.getHDTask());
		newHDTaskValues.put(KEY_TASK_LOCA_NAME, hdTask.getLocation());
		newHDTaskValues.put(KEY_TASK_CREATED, hdTask.getDatecreated());

		// now update the row
		// return db.update(DB_TASK_TABLE, newHDTaskValues, KEY_TASK_ID + "=" + _rowIndex, null) > 0;
		
		return db.update(DB_TASK_TABLE, newHDTaskValues, KEY_TASK_NAME + "=" + hdTask.getHDTask(), null) > 0;
	}
	
	//  now methods to handle queries follows
	public Cursor getAllHDTaskItemsCursor() {
		
	//	String[] columns = new String[] {KEY_TASK_ID, KEY_TASK_NAME, KEY_TASK_LOCA_NAME, KEY_TASK_CREATED};

		// 	 query(String table, 
		//				String[] columns, 
		//				String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
		return db.query(DB_TASK_TABLE,
						// columns, 
						null, 
						null, null, null, null, null);						
	}
	
	public Cursor setCursorHDTaskItem(long _rowIndex) throws SQLException {
		
		Cursor result = db.query(true, DB_TASK_TABLE, 
									new String[] {KEY_TASK_ID, KEY_TASK_NAME, KEY_TASK_LOCA_NAME,  KEY_TASK_CREATED},
									KEY_TASK_ID + "=" + _rowIndex,
									null, null, null, null, null); 
		
		if((result.getCount() == 0) || (!result.moveToFirst())) {
			throw new SQLException(" no HDTaskItem found for row: " + _rowIndex);
		}
		
		return result;
	}
	
	public HunyDewTaskItem getHDTaskItem (long _rowIndex) throws SQLException {
		
		Cursor result = db.query(true, DB_TASK_TABLE, 
									new String[] {KEY_TASK_ID, KEY_TASK_NAME, KEY_TASK_LOCA_NAME,  KEY_TASK_CREATED},
									KEY_TASK_ID + "=" + _rowIndex,
									null, null, null, null, null); 
		
		if((result.getCount() == 0) || (!result.moveToFirst())) {
			throw new SQLException(" no HDTaskItem found for row: " + _rowIndex);
		}
		
		String task = result.getString(COL_TASK_NAME);
		
		HunyDewTaskItem hdTaskItem = new HunyDewTaskItem(task); 
		
		return hdTaskItem;
	} 
	
public int getHDTaskItemRowIndex (String taskName) throws SQLException {
		
		Cursor result = db.query(true, DB_TASK_TABLE, 
									new String[] {KEY_TASK_ID},
									KEY_TASK_NAME + "=" + "\"" + taskName + "\"",
									null, null, null, null, null); 
		
		if((result.getCount() == 0) || (!result.moveToFirst())) {
			throw new SQLException(" no HDTaskItem found for row: " + taskName);
		}
		
		int _rowIdx = result.getInt(0);
		
		result.deactivate();
		
		return _rowIdx;
	}
	
	/*
	public List <String> selectAll() {
		
		List<String> list = new ArrayList<String>();
		
		Cursor cursor = this.db.query(DB_HD_TABLE, 
								new String[] {"name"}, 
								null, null, null, null, 
								"name desc");
		
		if (cursor.moveToFirst()) {
			
			do {
				
				list.add(cursor.getString(0));
				
			} while(cursor.moveToNext());
		}
		
		if (cursor != null && !cursor.isClosed()) {
			
			cursor.close();
		}
		
		return list;
	}
	 */
	
	// HunydewDBOpenHelper is used to simplify version management of the database
	private static class HunyDewDBOpenHelper extends SQLiteOpenHelper {

		/**
		 * @param context
		 * @param name
		 * @param factory
		 * @param version
		 */
		public HunyDewDBOpenHelper(Context context, String name,
									CursorFactory factory, int version) {
			
			super(context, name, factory, version);
		}
		
		public HunyDewDBOpenHelper(Context _ctx) {
			super(_ctx, DB_TASK_NAME, null, DB_TASK_VERSION);
		}
		
		// mark the SQL statement to create a new database and its tables

		private static final String DB_CREATE_TASK = "create table " + DB_TASK_TABLE + "("
		+ KEY_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ KEY_TASK_NAME + "  TEXT NOT NULL,"
		+ KEY_TASK_FLAGS +  " INTEGER,"
		+ KEY_TASK_LOCA_NAME + " TEXT NOT NULL,"
		+ KEY_TASK_CONTACT_STATUS + " STRING,"
		+ KEY_TASK_CREATED + " INTEGER,"
		+ KEY_TASK_NOTES + " BLOB"
		+ ");";


		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase arg0) {
			
			arg0.setVersion(DB_TASK_VERSION);
			
			arg0.execSQL(DB_CREATE_TASK);
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
			// Log.w("HuneyDewDBOpenHelper", " ... upgrading ...");
			
			db.setVersion(DB_TASK_VERSION);
			
			// drop table if it exists			
			db.execSQL("DROP TABLE IF EXISTS " + DB_CREATE_TASK);
			
			// create a new one
			onCreate(db);
		}
	}

	public void exportAllHDTask() {
		
	//	boolean mExternalStorageAvailable = false;
    //	boolean mExternalStorageWriteable = false;
	
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			
		    // We can read and write the media
			//  mExternalStorageAvailable = mExternalStorageWriteable = true;
		    File path = Environment.getExternalStorageDirectory();
		    String EXPORT_DB_NAME = "/hd/hdDbTask.xml";

			HunyDewDBExim hdDBExport = new HunyDewDBExim(context, db, path.getAbsolutePath(), EXPORT_DB_NAME);
			
			hdDBExport.exportData();
			
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    //  mExternalStorageAvailable = true;
         	//	  mExternalStorageWriteable = false;
		    
			 Toast.makeText(context.getApplicationContext(), context.getResources().getText(R.string.loca_storAvailButNotAvail), Toast.LENGTH_LONG).show();
			 
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
	       //	 mExternalStorageAvailable = mExternalStorageWriteable = false;
			 Toast.makeText(context.getApplicationContext(), context.getResources().getText(R.string.loca_extStorNotAvail), Toast.LENGTH_LONG).show();
		}
		/*
		 String TAG = "Testing ";
		Log.d(TAG , "These should work in all versions of Android:");
	      Log.d(TAG, "Environment.getExternalStorageDirectory() = " 
	            + Environment.getExternalStorageDirectory());
	      Log.d(TAG, "Environment.getDataDirectory() = " 
	            + Environment.getDataDirectory());
	      Log.d(TAG, "Environment.getDownloadCacheDirectory() = " 
	            + Environment.getDownloadCacheDirectory());
	      Log.d(TAG, "Environment.getRootDirectory() = " 
	            + Environment.getRootDirectory());

	      Log.d(TAG, "These were added in FroYo (SDK v8):");
	      Log.d(TAG,
	            "Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) = " 
	                  + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
	      Log.d(TAG, "getExternalCacheDir() = " + context.getExternalCacheDir());
	      Log.d(TAG, "getExternalFilesDir(null) = " 
	            + context.getExternalFilesDir(null));
	      Log.d(TAG,
	            "getExternalFilesDir(Environment.DIRECTORY_MOVIES) = " 
	                  + context.getExternalFilesDir(Environment.DIRECTORY_MOVIES));
	      */

				
	}

}
