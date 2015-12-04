/**
 * 
 */
package net.thepaca.hunydoo;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.EditText;

/**
 * @author MomNDad
 *
 */
public class HunyDewPromptListener implements OnClickListener {

	private String promptReply = null;
	
	View promptDialogView = null;
	
	/**
	 * 
	 */
	public HunyDewPromptListener(View inDialogView) {
		promptDialogView = inDialogView;
	}

	/* (non-Javadoc)
	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface arg0, int buttonId) {
		
		if (buttonId == DialogInterface.BUTTON1) {
			promptReply = getPromptText();
		}
		else {
			promptReply = null;
		}

	}
	
	private String getPromptText() {
		
		EditText et = (EditText) promptDialogView.findViewById(R.id.locname_edit);
		return et.getText().toString();
	}
	
	public String getPromptReply() {
		
		return promptReply;
	}
	
	

}
