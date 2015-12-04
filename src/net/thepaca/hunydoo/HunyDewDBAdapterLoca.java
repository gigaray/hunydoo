/**
 * 
 */
package net.thepaca.hunydoo;

/*
 *  b2.0.52  6/8  make sure db is not null and is open to do a close 
 * 
 */

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;
import android.util.Log;
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
public class HunyDewDBAdapterLoca {

	/**
	 * 
	 */
	private static final String DB_LOCA_NAME = "hdDBLoca.db";
	private static final int DB_LOCA_VERSION_1 =1;
	private static final int DB_LOCA_VERSION =2;
	private static final String DB_LOCA_TABLE = "hdLocStore";
	
	//index (primary) key for the two table
	public static final String KEY_LOCA_ID = "_id";
	
	// now enter each column name and index for HDLocation
	public static final String KEY_LOCA_NAME = "_LocaName";
	public static final int COL_LOCA_NAME = 1;
	public static final String KEY_LOCA_LAT = "_l_lat";
	public static final int COL_LOCA_LAT = 2;
	public static final String KEY_LOCA_LON = "_l_lon";
	public static final int COL_LOCA_LON = 3;
	public static final String KEY_LOCA_URI = "_l_uri";
	public static final int COL_LOCA_URI = 4;
	public static final String KEY_LOCA_NOTES = "_l_notes";
	public static final int COL_LOCA_NOTES = 5;
	public static final String KEY_LOCA_CREATED = "_l_created";
	public static final int COL_LOCA_CREATED = 6;
	public static final String KEY_LOCA_FLAG= "_l_flag";
	public static final int COL_LOCA_FLAG= 7;
	
	
	private SQLiteDatabase db;				// variable to hold db instance
	private final Context context;			// context of the app using the db
	private HunyDewDBOpenHelper hdHelper;	// db open/upgrade helper
	
	public static final int DB_IS_DIRTY = 0x01;
	public static final int DB_IS_NOT_DIRTY = ~DB_IS_DIRTY;
	
	private static int statusFlag =  DB_IS_DIRTY ;
	
	//private SQLiteStatement insertStmt;
	//private static final String DB_INSERT_LOCA = "insert into" + DB_LOCA_TABLE + "(name) values (?)";
	
	// store an instance of the HunyDewDBOpenHelper
	public HunyDewDBAdapterLoca(Context _ctxt) {
		
		context = _ctxt;
		
		hdHelper = new HunyDewDBOpenHelper(context, DB_LOCA_NAME, null, DB_LOCA_VERSION);
	}
	
	public void open() throws SQLiteException {

		try {

			HunyDewZLogger.write(context,"HunyDewDBAdapterLoca", " ... open ... b4 getWritableDatabase ...");
			
			db = hdHelper.getWritableDatabase();
		}
		catch (SQLiteException ex) {
			// if writeable instaqnce cannot be opened
			
			db = hdHelper.getReadableDatabase();
		}
		
		HunyDewZLogger.write(context,"HunyDewDBAdapterLoca", " ... open ... ends ... ");
}

	public void close() {
		if (db != null)
			if (db.isOpen())
				db.close();
	}
	
	public boolean isDirty() {
		return ((statusFlag & DB_IS_DIRTY) == DB_IS_DIRTY);
	}
	
	public void setDirtyFlag() {
		statusFlag |= DB_IS_DIRTY;
	}

	public void removeDirtyFlag() {
		statusFlag &= ~DB_IS_DIRTY;
	}
	
	public long insertHDLoca(String _locaName, double _lat, double _lon) {

		// create a new row  of values to insert
		ContentValues newHDLocaValues = new ContentValues();
		
		// now assign values to each row
		newHDLocaValues.put(KEY_LOCA_NAME, _locaName);
		newHDLocaValues.put(KEY_LOCA_LAT, _lat);
		newHDLocaValues.put(KEY_LOCA_LON, _lon);
		
		setDirtyFlag();
		
		// now insert the row
		return db.insert(DB_LOCA_TABLE, null, newHDLocaValues);
	}
		
	public boolean deleteHDLoca(long _rowIndex) {
	
		setDirtyFlag();
		
		return db.delete(DB_LOCA_TABLE, KEY_LOCA_ID + "=" + _rowIndex, null) > 0;
	}
	
	public void deleteAllHDLoca() {
		
		setDirtyFlag();
		
		this.db.delete(DB_LOCA_TABLE, null, null);
	}
	
	public boolean updateHDLoca(long _rowIndex, String locaName) {
		
		ContentValues newHDLocaValues = new ContentValues();
		
		newHDLocaValues.put(KEY_LOCA_NAME, locaName);
		
		setDirtyFlag();
		
		return db.update(DB_LOCA_TABLE, newHDLocaValues, KEY_LOCA_ID + "=" + _rowIndex, null) > 0;
	}
	
	//  now methods to handle queries follows
	public Cursor getAllHDLocaItemsCursor() {
		
		String[] columns = new String[] {KEY_LOCA_ID, KEY_LOCA_NAME, KEY_LOCA_LAT, KEY_LOCA_LON};
		
		HunyDewZLogger.write(context,"LocaItemCursor", "db is open?   "+   db.isOpen());

		HunyDewZLogger.write(context,"LocaItemCursor", "db is RO?   "+   db.isReadOnly());

		Cursor c = db.query(DB_LOCA_TABLE,
				columns, null, null, null, null, null);

		if (c != null) {

			HunyDewZLogger.write(context,"opSuccess", "opSuccess?   "+   c.moveToFirst());
		
			int countColEntries = c.getColumnCount();
			HunyDewZLogger.write(context,"opSuccess", "countColEntries?   "+  countColEntries );
	
			HunyDewZLogger.write(context,"opSuccess", "countEntries?   "+  c.getCount());
			
			for( int idx = 0; idx < countColEntries; idx++ )
			{
				HunyDewZLogger.write(context, "opSuccess","column  " + c.getColumnName(idx) );
			}
		}

		return c;
	}
	
	public Cursor setCursorHDLocaItem(long _rowIndex) throws SQLException {
		
		Cursor result = db.query(true, DB_LOCA_TABLE, 
									new String[] {KEY_LOCA_ID, KEY_LOCA_NAME, KEY_LOCA_LAT, KEY_LOCA_LON},
									KEY_LOCA_ID + "=" + _rowIndex,
									null, null, null, null, null); 
		
		if((result.getCount() == 0) || (!result.moveToFirst())) {
			throw new SQLException(" no HDTaskItem found for row: " + _rowIndex);
		}
		
		return result;
	}
	
	public HunyDewLocationItem getHDLocaItem (long _rowIndex) throws SQLException {
		
		Cursor result = db.query(true, DB_LOCA_TABLE, 
									new String[] {KEY_LOCA_ID, KEY_LOCA_NAME, KEY_LOCA_LAT, KEY_LOCA_LON},
									KEY_LOCA_ID + "=" + _rowIndex,
									null, null, null, null, null); 
		
		if((result.getCount() == 0) || (!result.moveToFirst())) {
			throw new SQLException(" no HDTaskItem found for row: " + _rowIndex);
		}
		
		String loca = result.getString(COL_LOCA_NAME);
		double lat = result.getDouble(COL_LOCA_LAT);
		double lon = result.getDouble(COL_LOCA_LON);
		
		//HunyDewZLogger.write(getApplicationContext(),"getHDLocaItem", loca + " Lat:" + lat + " Lon:" + lon);
		HunyDewLocationItem hdLocaItem = new HunyDewLocationItem(loca, lat, lon); 
		
		return hdLocaItem;
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
			super(_ctx, DB_LOCA_NAME, null, DB_LOCA_VERSION);
		}
		
		// mark the SQL statement to create a new database and its tables
		private static final String DB_CREATE_LOCA = "create table " + DB_LOCA_TABLE + "("
			+ KEY_LOCA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ KEY_LOCA_NAME + "  TEXT NOT NULL,"
			+ KEY_LOCA_LAT +  " REAL,"
			+ KEY_LOCA_LON + " REAL,"
			+ KEY_LOCA_URI + " STRING,"
			+ KEY_LOCA_NOTES + " BLOB, "
			+ KEY_LOCA_FLAG+ " INTEGRER"
			+ ");";


		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase arg0) {
			
			// Log.w("HunyDewDBOpenHelper", " ... onCreate ...");
			
			arg0.setVersion(DB_LOCA_VERSION);
			
			arg0.execSQL(DB_CREATE_LOCA);
			
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
			 if (oldVersion ==DB_LOCA_VERSION_1) { 
			 		db.execSQL("ALTER  TABLE  " + DB_LOCA_TABLE + " ADD COLUMN  " + KEY_LOCA_FLAG + "  INTEGER"); 
			 }
			 else {
			//		Log.w("HuneyDewDBOpenHelper", " ... upgrading ...");
					
					db.setVersion(DB_LOCA_VERSION);
					
					// drop table if it exists
					db.execSQL("DROP TABLE IF EXISTS " + DB_LOCA_TABLE);
					
					// create a new one
					onCreate(db);
			 }
    }

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#close()
		 */
		@Override
		public synchronized void close() {
			// TODO Auto-generated method stub
			super.close();
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#getReadableDatabase()
		 */
		@Override
		public synchronized SQLiteDatabase getReadableDatabase() {
			// TODO Auto-generated method stub
			return super.getReadableDatabase();
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#getWritableDatabase()
		 */
		@Override
		public synchronized SQLiteDatabase getWritableDatabase() {
			// TODO Auto-generated method stub
			return super.getWritableDatabase();
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onOpen(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			Log.w("HunyDewDBOpenHelper", " ... onOpen ...");
			
			super.onOpen(db);
		}

	}

	public void exportAllHDLoca() {
		
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
	
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		    File path = Environment.getExternalStorageDirectory();
		    String EXPORT_DB_NAME = "/hd/hdDbLoca.xml";

			HunyDewDBExim hdDBExport = new HunyDewDBExim(context, db, path.getAbsolutePath(), EXPORT_DB_NAME);
			
			hdDBExport.exportData();
			
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		    
			 Toast.makeText(context.getApplicationContext(), context.getResources().getText(R.string.loca_storAvailButNotAvail), Toast.LENGTH_LONG).show();
			 
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
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
