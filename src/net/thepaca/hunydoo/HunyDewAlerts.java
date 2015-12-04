 /**
 * 
 */
package net.thepaca.hunydoo;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
/**
 * @author MomNDad
 *
 */
public class HunyDewAlerts {

	public String prompt(String message, Context ctxt) {
		
		// load the view
		// LayoutInflater li = LayoutInflater.from(ctxt);
		LayoutInflater li = LayoutInflater.from(ctxt);
		
		View view = li.inflate(R.layout.locname_dialog, null);
		
		// now get the builder and set the view
		AlertDialog.Builder ab = new AlertDialog.Builder(ctxt);

		ab.setTitle(ctxt.getResources().getText(R.string.locaName));
    	ab.setMessage(ctxt.getResources().getText(R.string.locaNameMesg));
		
		final EditText input = new EditText(ctxt);
		ab.setView(input);
		
		// add the buttons and listener
		HunyDewPromptListener pl = new HunyDewPromptListener(view);
		ab.setPositiveButton(ctxt.getResources().getText(R.string.okLabel), pl);
		ab.setNegativeButton(ctxt.getResources().getText(R.string.cancelLabel), pl);
		
		AlertDialog ad = ab.create();
		
		ad.show();  // this, "",getString(R.string.locname_view), true);
		
		return pl.getPromptReply();
	}
}
