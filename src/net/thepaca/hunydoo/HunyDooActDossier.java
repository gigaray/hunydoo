/**
 * 
 */
package net.thepaca.hunydoo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import net.thepaca.hunydoo.HunyDewDBAdapterLoca;
import net.thepaca.hunydoo.HunyDewLocationItem;
import net.thepaca.hunydoo.HunyDewLocationStore;

import net.thepaca.hunydoo.HunyDooFulfillerService;
import net.thepaca.hunydoo.R;
import net.thepaca.hunydoo.HunyDooActDossier.udpCliMain;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * @author MomNDad
 *
 */

public class HunyDooActDossier extends Activity{

	private TextView hdLocTextView ;
	// for the prototype this all we care about
	private String dossierNotes ;
	
	// for the dialog box
//	 private static final int DIALOG_TEXT_ENTRY = 1;

	 // HunyDewLocationStore is an ArryaList of HunyDewLocationItem
	 HunyDewLocationStore locaStore = HunyDewLocationStore.getLocaStoreSingletonObj();
	 HunyDewLocationItem locaItem;
	 
	 // adding db support to store the location
	 HunyDewDBAdapterLoca hdDBAdapter;
	 
	 
	 String currentLocaname;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		// 1.0 bind to HDFulfiller Service
		Intent bindIntent = new Intent(HunyDooActDossier.this, HunyDooFulfillerService.class);
		boolean bRet = getApplicationContext().bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
        
		Log.i(getClass().getSimpleName(), "bindService in Dossier " + ((bRet == true) ? "succeeds" : "fails"));       
        
        // 2.0 Get the output UI
        hdLocTextView = new TextView(this);
        
        // 3.0 iew the bitmap
        setContentView(new BitMapView(this));
        
	}
	 @Override
	 protected void onResume() {
		 
		 super.onResume();
		 
	 }
	 
	 /** Stop the updates when Activity is paused */
	 @Override
		protected void onPause() {
			
		 super.onPause();
			
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
	}
	
	public static String CURRENT_LOCA_INFO_PREF = "CURRENT_LOCA_INFO_PREF";
	
	class BitMapView extends View {
		
		Bitmap mBitmap = null;

		public BitMapView(Context context) {
			
			super(context);
		}
		
		public BitMapView(Context context, Bitmap bm) {
			
			super(context);
			
			mBitmap = bm;
		}

		// http://android-er.blogspot.com/2010/05/draw-bitmap-on-view.html
		// http://www.higherpass.com/Android/Tutorials/Working-With-Images-In-Android/1/
		// http://mercialabs.wordpress.com/2010/06/10/android-display-bitmap-in-view-2/
		
		@Override
		protected void onDraw(Canvas canvas) {
			
			// called when view is drawn
			Paint paint = new Paint();
			
			paint.setFilterBitmap(true);

			// let us get the image to load in the dossier
			// let us also save the loca found for the dossier
			
			int mode = Activity.MODE_WORLD_READABLE ;
			SharedPreferences currentActiveLocaInfoPref = getSharedPreferences(CURRENT_LOCA_INFO_PREF, mode) ;
		//	SharedPreferences.Editor editor = currentActiveLocaInfoPref.edit();
			currentLocaname = currentActiveLocaInfoPref.getString("currentLocaname", "unknown");
			
			// sharedpreferences does not work :-) hence we bind to the service to pull 
			// the current LocalName
			/*
			 * 
			 * OPEN THIS UP TO DISPLAY THE IMAGES BASED ON WHAT IS RECIEVED FROM THE INSTITUTION
			 * 
			 * 
			int resIndex=R.drawable.fplan_default;
			
			if (hdFulfillerServiceBinder != null)	{
				Log.i(getClass().getSimpleName(), "hdFulfillerServiceBinder in Dossier is not null");
				currentLocaname = hdFulfillerServiceBinder.getLocaMovedInto();
			}
			else
				Log.i(getClass().getSimpleName(), "hdFulfillerServiceBinder in Dossier is null");
			
			if (currentLocaname.contains("Space") == true) 
				resIndex = R.drawable.fplan_astm_1fa;
			else if (currentLocaname.contains("Kite") == true)
				resIndex = R.drawable.fplan_kite;
			else if (currentLocaname.contains("Zoo") == true)
				resIndex = R.drawable.fplan_zoo;
			else if (currentLocaname.contains("Garden") == true)
				resIndex = R.drawable.fplan_garden;
			else if (currentLocaname.contains("Science") == true)
				resIndex = R.drawable.fplan_sci;
			else if (currentLocaname.contains("History") == true)
				resIndex = R.drawable.fplan_nh;
			else if (currentLocaname.contains("Apes") == true)
				resIndex = R.drawable.fplan_man;
			else if (currentLocaname.contains("unknown") == true)
				resIndex = R.drawable.fplan_none;
			
			
			// The image will be scaled so it will fill the width, and the
			// height will preserve the image’s aspect ration
			mBitmap = BitmapFactory.decodeResource(getResources(), resIndex);
			
			*/
	//		double aspectRatio = ((double) mBitmap.getWidth()) / mBitmap.getHeight();
			
			// Rect dest = new Rect(0, 0, this.getWidth(),(int) (this.getHeight() / aspectRatio));
			
			Rect dest = new Rect(0, 0, (canvas.getWidth() - 40),(canvas.getHeight() - 140));
			
			canvas.drawBitmap(mBitmap, null, dest, paint);
			
			canvas.drawText(currentLocaname, 10, 10, paint);
		}
	}
	
	private HunyDooFulfillerService hdFulfillerServiceBinder;
	
	private ServiceConnection mConnection = new ServiceConnection() {
				
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
			hdFulfillerServiceBinder = null;
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			// hdFulfillerServiceBinder = ((HunyDooFulfillerService.DossierBinder)service).getService();
	
		}
	};
	
	// connector to Wireless sensor Network
	private String getSensorData() {

		String uiStg = "GET tak/sensor/listTemp";
		
		udpCliMain ucm;
		
		String stgRecvd;
		
		stgRecvd = "unable to connect";
		
		try {
			ucm = new udpCliMain();
			
			ucm.sendRequest(uiStg);
			
			stgRecvd = ucm.recvResponse();

			ucm.udpCliSock.close();
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
						
		System.out.println("recvd: " + stgRecvd.length() + "[" + stgRecvd + "]");

		return stgRecvd;
	}
	
	public class udpCliMain {
		
		InetAddress ipa;
		int serverPort = 20201;
		
		DatagramSocket udpCliSock;
		
		String recvString;
		
		public udpCliMain() throws Exception {
			// TODO Auto-generated constructor stub
			
			udpCliSock = new DatagramSocket(serverPort);
			
			ipa = InetAddress.getLocalHost();
			ipa = InetAddress.getByName("192.168.1.100");
		}
		
		private void sendRequest(String uiStg) throws Exception {
			
			//send socket
			DatagramPacket udpPacketxmt = new DatagramPacket(uiStg.getBytes(), uiStg.length(), ipa, serverPort);
			
			udpCliSock.send(udpPacketxmt);
			
		}
		
		private String recvResponse() throws Exception{
			
			byte[] inbuf = new byte[256];
			
			// receive socket
			DatagramPacket udpPackRecv = new DatagramPacket(inbuf, inbuf.length, ipa, serverPort);
			
			udpCliSock.receive(udpPackRecv);
			
			int numBytesRecvd = udpPackRecv.getLength();
		
			System.out.println("recvd: " + numBytesRecvd );
			
			recvString = new String(udpPackRecv.getData(), 0, (numBytesRecvd + 1));
			
			System.out.println("recvd: " + recvString);
			
			return recvString;
			
		}
	}
	
}
