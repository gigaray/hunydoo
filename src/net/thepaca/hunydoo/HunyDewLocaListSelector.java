/**
 * 
 */
package net.thepaca.hunydoo;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author MomNDad
 *
 */
public class HunyDewLocaListSelector extends ListActivity {

	/**
	 * 
	 */
	
	HunyDewLocationStore locaStore = HunyDewLocationStore.getLocaStoreSingletonObj();
	
	public HunyDewLocaListSelector() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		ArrayList <String> locaList = locaStore.getLocaList();
		
		ArrayAdapter<String> locaListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locaList);
		
		setListAdapter(locaListAdapter);
		
		getListView().setTextFilterEnabled(true);
	}

    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	Object o = this.getListAdapter().getItem(position);
    	String loca = o.toString();
    	Intent returnIntent = new Intent();
    	returnIntent.putExtra("SelectedLocation",loca);
    	setResult(RESULT_OK,returnIntent);    	
    	finish();
    }
    
}
