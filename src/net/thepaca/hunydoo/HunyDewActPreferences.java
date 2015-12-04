/* 
 * 
 */

package net.thepaca.hunydoo;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author MomNDad
 * 
 */
public class HunyDewActPreferences extends Activity {

	SharedPreferences prefs;

	CheckBox continue_fulfiller, show_smartdoo, clear_AllDB, clear_TaskOnlyDB;

	public static final String PREF_CONT_FULFILLER = "PREF_CONT_FULFILLER";
	public static final String PREF_SHOW_SMARTDOO = "PREF_SHOW_SMARTDOO";
	public static final String SETTINGS_CLEAR_ALLDB = "SETTINGS_CLEAR_ALLDB";
	public static final String SETTINGS_CLEAR_TASKONLYDB = "SETTINGS_CLEAR_TASKONLYDB";

	protected static final String PREF_ADS_DISPLAY_MODE = "ADS_DISPLAY_MODE";

	public enum ADS_SETTINGS {
		TURN_ADS_ON, TURN_ADS_OFF
	};

	protected static final String PREF_LOG_SETTINGS_MODE = "LOGS_SETTINGS_MODE";

	public enum LOG_SETTINGS {
		TURN_LOGS_WRITE_ON, TURN_LOGS_WRITE_OFF
	};

	// adding db support to store the location
	HunyDewDBAdapterLoca hdDBAdapterLoca;
	HunyDewDBAdapterTask hdDBAdapterTask;

	static final private int HIDDEN_CONFIG_ENTRY = Menu.FIRST;

	public void onCreate(Bundle savedInstanceState) {

		HunyDewZLogger.startTrace("hdPref");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.hd_pref);

		// 1.0a print out the version number
		String versName;

		try {
			PackageInfo manager = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			versName = manager.versionName;

		} catch (NameNotFoundException e) {

			versName = "?.?";
		}

		TextView tVer = (TextView) findViewById(R.id.show_version_details);

		tVer.setText("  v" + versName + "    ");

		// 1.0 b Put out Button to send an email with feedback....
		Button sendFeedbackButton = (Button) findViewById(R.id.emailButton);
		sendFeedbackButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

				String[] recipients = new String[] { "info@thepaca.net" };

				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
						recipients);

				String versName;
				try {
					PackageInfo manager = getPackageManager().getPackageInfo(
							getPackageName(), 0);
					versName = manager.versionName;
				} catch (NameNotFoundException e) {
					versName = "?.?";
				}
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						getResources().getText(R.string.feedbackMessage) + "  "
								+ versName);

				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						getResources().getText(R.string.extraText));

				// emailIntent.setType("application/octet-stream");
				emailIntent.setType("text/plain");

				startActivity(Intent.createChooser(emailIntent, getResources()
						.getString(R.string.sendEmail)));

				finish();
			}
		});

		// 1.0d Help Button
		Button recvHelpButton = (Button) findViewById(R.id.helpButton);
		recvHelpButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder helpDialog = new AlertDialog.Builder(
						HunyDewActPreferences.this);
				helpDialog.setTitle(getResources().getText(R.string.helpText));
				helpDialog.setCancelable(false);

				helpDialog.setNeutralButton(R.string.okLabel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Toast.makeText(
										getApplicationContext(),
										getResources().getText(
												R.string.helpFurther),
										Toast.LENGTH_LONG).show();
							}
						});
				helpDialog.setMessage(Html.fromHtml(getResources().getString(
						R.string.helpDetails)));
				helpDialog.show();
			}
		});

		// 2.0 User preference - do we keep the fulfiller running after closing
		// the app
		// the default is 'yes' - the 'no' is not yet implemented - TBD
		// TextView textview_continue_fulfiller = (TextView)
		// findViewById(R.id.continue_fulfiller_res_prompt);
		// textview_continue_fulfiller.setText(R.string.continue_fulfiller_prompt);
		continue_fulfiller = (CheckBox) findViewById(R.id.continue_fulfiller_res_checkbox);

		// 3.0 Is smartDoo to be shown - default is yes - ni is yet to be
		// implemented
		// support for "no" - TBD
		// /TextView textview_show_smartdoo = (TextView)
		// findViewById(R.id.show_smartdoo_res_prompt);
		// textview_show_smartdoo.setText(R.string.show_smartdoo_prompt);
		// SMARTDOO DISABLED
		/*
		 * show_smartdoo = (CheckBox)
		 * findViewById(R.id.show_smartdoo_res_checkbox);
		 */

		// 4.0 how do we want to clean up the database?
		// check the Location only does not help so it is either Task or All
		hdDBAdapterTask = new HunyDewDBAdapterTask(this);
		hdDBAdapterLoca = new HunyDewDBAdapterLoca(this);

		clear_AllDB = (CheckBox) findViewById(R.id.show_clear_alldb_res_checkbox);

		clear_TaskOnlyDB = (CheckBox) findViewById(R.id.show_clear_taskonly_res_checkbox);

		Context ctx = getApplicationContext();
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		updateHDUIFromPrefs();

		// 5.0 now handle the OK and Cancel buttons
		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				saveHDUIFPrefs();
				HunyDewActPreferences.this.setResult(RESULT_OK);

				// now handle the databases
				if (clear_AllDB.isChecked() == true) {

					hdDBAdapterTask.open();
					hdDBAdapterTask.deleteAllHDTask();
					hdDBAdapterTask.close();

					hdDBAdapterLoca.open();
					hdDBAdapterLoca.deleteAllHDLoca();
					hdDBAdapterLoca.close();
				}

				if (clear_TaskOnlyDB.isChecked() == true) {

					hdDBAdapterTask.open();
					hdDBAdapterTask.deleteAllHDTask();
					hdDBAdapterTask.close();

				}
				finish();
			}
		});

		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HunyDewActPreferences.this.setResult(RESULT_CANCELED);
				finish();
			}
		});

	}

	private void updateHDUIFromPrefs() {

		// s boolean cont_fulfillerChecked =
		// prefs.getBoolean(PREF_CONT_FULFILLER, true);
		// TBD - close if requested - until it is implemented disable UI on the
		// checkbox
		// yet to implement full close- the service continues to work
		boolean cont_fulfillerChecked = true;
		continue_fulfiller.setChecked(cont_fulfillerChecked);
		continue_fulfiller.setEnabled(false);
		continue_fulfiller.setClickable(false);
		continue_fulfiller.setPressed(true);

		// boolean show_smartdooChecked = prefs.getBoolean(PREF_SHOW_SMARTDOO,
		// true);
		// TBD - enable if requested - until it is implemented disable UI on the
		// checkbox
		// yet to implement SmartDoo
		/*
		 * boolean show_smartdooChecked = false;
		 * show_smartdoo.setChecked(show_smartdooChecked);
		 * show_smartdoo.setEnabled(false); show_smartdoo.setClickable(false);
		 * show_smartdoo.setPressed(false);
		 */
	}

	private void saveHDUIFPrefs() {

		boolean cont_fulfillerChecked = continue_fulfiller.isChecked();

		// TBD - yet to implement SmartDoo
		/* boolean show_smartdoocChecked = show_smartdoo.isChecked(); */

		Editor editor = prefs.edit();

		editor.putBoolean(PREF_CONT_FULFILLER, cont_fulfillerChecked);
		/* editor.putBoolean(PREF_SHOW_SMARTDOO, show_smartdoocChecked); */

		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		// 1.1 create and add new menu items
		// 1.2 then assign the icons to those menu items
		// 1.3 finally assign the shortcut
		MenuItem itemAdd = menu.add(0, HIDDEN_CONFIG_ENTRY, Menu.NONE,
				R.string.hiddenConfigCmd);
		itemAdd.setIcon(R.drawable.add_new_item);
		// itemAdd.setShortcut('0', 'a');

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {

		case HIDDEN_CONFIG_ENTRY: {
			handleHiddenConfig();
			return true;
		}

		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */

	public void handleHiddenConfig() {

		// when the user presses center let him get the edit box to enter the
		// Configure command

		AlertDialog.Builder configAlertDailog = new AlertDialog.Builder(
				HunyDewActPreferences.this);
		configAlertDailog.setTitle(getResources().getText(
				R.string.configureText));
		configAlertDailog.setMessage(getResources().getText(
				R.string.configureMessage));

		final EditText configInput = new EditText(HunyDewActPreferences.this);
		configAlertDailog.setView(configInput);

		configAlertDailog.setPositiveButton(
				getResources().getText(R.string.okLabel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						String cmdRecvd = configInput.getText().toString();

						HunyDewZLogger.write(getApplicationContext(), "Pref",
								"cmd:  " + cmdRecvd);

						updateConfigure(cmdRecvd);
						return;
					}

					private void updateConfigure(String cmdRecvd) {

						String key, value;

						if (cmdRecvd.contains("=") == false)
							return;

						StringTokenizer st = new StringTokenizer(cmdRecvd, "=;");

						while (st.hasMoreTokens()) {

							try {
								key = st.nextToken();
								value = st.nextToken();
							} catch (NoSuchElementException e) {
								return;
							}

							if (key == null) {
								break;
							}
							if (value == null) {
								break;
							}
							if (key.equals("db")) { // "db=export"

								if (value.equals("export")) {

									exportDBToFile();
								}
							}

							if (key.equals("ads")) {
								if (value.equals("on")) {
									manageAdsDisplay(ADS_SETTINGS.TURN_ADS_ON);
								} else if (value.equals("off")) {
									manageAdsDisplay(ADS_SETTINGS.TURN_ADS_OFF);
								}
							}

							if (key.equals("log")) {
								if (value.equals("on")) {
									manageLog(LOG_SETTINGS.TURN_LOGS_WRITE_ON);
								} else if (value.equals("off")) {
									manageLog(LOG_SETTINGS.TURN_LOGS_WRITE_OFF);
								}

							}
						}

					}

					private void manageLog(LOG_SETTINGS log_Setting) {

						Editor editor = prefs.edit();

						switch (log_Setting) {
						case TURN_LOGS_WRITE_OFF:
						case TURN_LOGS_WRITE_ON:
							editor.putInt(PREF_LOG_SETTINGS_MODE,
									log_Setting.ordinal());
							break;

						default:
							break;
						}
						editor.commit();
					}

					private void exportDBToFile() {

						HunyDewZLogger.write(getApplicationContext(),
								"exportDBToFile", " starts:  ");

						hdDBAdapterLoca.open();
						hdDBAdapterLoca.exportAllHDLoca();
						hdDBAdapterLoca.close();

						hdDBAdapterTask.open();
						hdDBAdapterTask.exportAllHDTask();
						hdDBAdapterTask.close();

						HunyDewZLogger.write(getApplicationContext(),
								"exportDBToFile", " ends:  ");
					}

					private void manageAdsDisplay(ADS_SETTINGS ad_setting) {

						Editor editor = prefs.edit();

						switch (ad_setting) {
						case TURN_ADS_ON:
						case TURN_ADS_OFF:
							editor.putInt(PREF_ADS_DISPLAY_MODE,
									ad_setting.ordinal());
							break;

						default:
							break;
						}
						editor.commit();
					}
				});

		configAlertDailog.setNegativeButton(
				getResources().getText(R.string.cancelLabel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						String cmdRecvd = "null=null";

						return;
					}
				});

		configAlertDailog.show();

		/** config ends */

		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		HunyDewZLogger.stopTrace("hdPref");
	}
}
