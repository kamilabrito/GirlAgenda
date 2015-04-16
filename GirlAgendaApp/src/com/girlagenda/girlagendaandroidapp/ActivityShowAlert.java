package com.girlagenda.girlagendaandroidapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.adbuddiz.sdk.AdBuddizLogLevel;

/**
 * @author eduarda.menezes, jackeline.miranda, karina.pinheiro,
 *         bianca.nascimento
 * 
 */

public class ActivityShowAlert extends Activity {

	private TextView textViewTime;
	private TextView textViewPatient;
	private TextView textViewMedicine;
	private TextView textViewDate;
	private LinearLayout alarmSettingsContainer;
	private LinearLayout noAlarmContainer;
	private Button btnEdit;
	private Button btnMethods;
	private Button btnCreateAlarm;
	private Button btnCancelAlarm;
	private Button btnAbout;
	private String medicine;
	private String patient;
	private long fromDate;
	private long toDate;
	private long atHour;
	private int color;
	private int sound;
	private View postIt;
	private boolean isAlarmSetted = false;
	private SoundPool soundPool;
	private int soundID;
	private boolean loaded = false;
	private int countAdd = 0;
	private boolean fromAlarm = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		
		Bundle bundle = getIntent().getExtras();
		
		if (bundle != null) {
			String extra = bundle.getString(Constants.KEY_INTENT);
			if ( extra != null && !extra.isEmpty() && extra.equals(Constants.INTENT_FROM_ALARM) ) {
				fromAlarm = true;
			} else {
				fromAlarm = false;
			}
		}
		
		AdBuddiz.setPublisherKey("9a1dff09-5892-4c38-ab19-4c7a475b1329");
		AdBuddiz.setLogLevel(AdBuddizLogLevel.Info);
		AdBuddiz.cacheAds(this); // this = current Activity
		
		getInformation();

		textViewDate = (TextView) findViewById(R.id.alert_medicine_date);
		textViewTime = (TextView) findViewById(R.id.alert_medicine_time);
		textViewPatient = (TextView) findViewById(R.id.alert_patient_name);
		textViewMedicine = (TextView) findViewById(R.id.alert_medicine_name);
		postIt = (View) findViewById(R.id.image);
		alarmSettingsContainer = (LinearLayout) findViewById(R.id.alarm_settings_container);
		noAlarmContainer = (LinearLayout) findViewById(R.id.no_alarm_container);
		btnEdit = (Button) findViewById(R.id.button_edit_alarm);
		btnMethods = (Button) findViewById(R.id.button_methods);
		btnCreateAlarm = (Button) findViewById(R.id.button_create_alarm);
		btnCancelAlarm = (Button) findViewById(R.id.button_cancel_alarm);
		btnAbout = (Button) findViewById(R.id.button_about);
		
		btnAbout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent actvityConfirmation = new Intent();
				actvityConfirmation.setClass(ActivityShowAlert.this,
						ActivityAbout.class);
				startActivity(actvityConfirmation);
			}
		});

		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent actvityConfirmation = new Intent();
				actvityConfirmation.setClass(ActivityShowAlert.this,
						ActivityRegister.class);
				actvityConfirmation.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(actvityConfirmation);
				if (AdBuddiz.isReadyToShowAd(ActivityShowAlert.this)) { 
					AdBuddiz.showAd(ActivityShowAlert.this);
				}
			}
		});

		btnCreateAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent actvityConfirmation = new Intent();
				actvityConfirmation.setClass(ActivityShowAlert.this,
						ActivityRegister.class);
				actvityConfirmation.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(actvityConfirmation);
			}
		});

		btnMethods.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent actvityConfirmation = new Intent();
				actvityConfirmation.setClass(ActivityShowAlert.this,
						ActivityMethods.class);
				actvityConfirmation.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(actvityConfirmation);
				if (AdBuddiz.isReadyToShowAd(ActivityShowAlert.this)) { 
					AdBuddiz.showAd(ActivityShowAlert.this);
				}
			}
		});
		
		btnCancelAlarm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isAlarmSetted) {
					cleanInformation();
					cancelAlarm();
					fromAlarm = false;
					noAlarmContainer.setVisibility(View.VISIBLE);
					alarmSettingsContainer.setVisibility(View.GONE);
					btnEdit.setVisibility(View.INVISIBLE);
					setViewColor(color);
					textViewPatient.setVisibility(View.INVISIBLE);
					btnCancelAlarm.setVisibility(View.INVISIBLE);
				}
			}
		});

		if (isAlarmSetted) {
			noAlarmContainer.setVisibility(View.GONE);
			alarmSettingsContainer.setVisibility(View.VISIBLE);
			btnEdit.setVisibility(View.VISIBLE);
			btnCancelAlarm.setVisibility(View.VISIBLE);
			textViewDate.setText(formatDate(getCurrentDate()));
			textViewMedicine.setText(medicine);
			textViewPatient.setText(patient);
			textViewTime.setText(formatTime(atHour));
			textViewPatient.setVisibility(View.VISIBLE);
			setViewColor(color);
		} else {
			noAlarmContainer.setVisibility(View.VISIBLE);
			alarmSettingsContainer.setVisibility(View.GONE);
			btnEdit.setVisibility(View.INVISIBLE);
			btnCancelAlarm.setVisibility(View.INVISIBLE);
			setViewColor(color);
			textViewPatient.setVisibility(View.INVISIBLE);
		}

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});
		soundID = soundPool.load(this, R.raw.alarm_sound, 1);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		fromAlarm = false;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		
		Log.e("Test", "isloaded: " + loaded);
		if (loaded && (sound == R.id.radioButton_on) && fromAlarm) {
			soundPool.play(soundID, volume, volume, 1, 0, 1f);
			Log.e("Test", "Played sound");
		}
	}

	public void getInformation() {

		SharedPreferences prefs = getSharedPreferences(Constants.GIRL_AGENDA,
				MODE_PRIVATE);
		medicine = prefs.getString(Constants.MEDICINE, "");
		patient = prefs.getString(Constants.PATIENT, "");
		atHour = prefs.getLong(Constants.TIME, 0);
		color = prefs.getInt(Constants.COLOR, -1);
		sound = prefs.getInt(Constants.SOUND, -1);
		isAlarmSetted = prefs.getBoolean(Constants.ALARM_STATUS, false);
	}
	
	public void cleanInformation() {
		SharedPreferences.Editor editor = getSharedPreferences(
				Constants.GIRL_AGENDA, MODE_PRIVATE).edit();
		editor.putString(Constants.MEDICINE, "");
		editor.putString(Constants.PATIENT, "");
		editor.putLong(Constants.DATEBEGGING, 0);
		editor.putLong(Constants.DATEFINISH, 0);
		editor.putLong(Constants.TIME, 0);
		editor.putInt(Constants.COLOR, -1);
		editor.putInt(Constants.SOUND, -1);
		editor.putBoolean(Constants.ALARM_STATUS, false);

		editor.commit();
	}

	public String formatDate(long date) {

		String currentDate = "";

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		currentDate = dateFormat.format(date);

		return currentDate;
	}

	public String formatTime(long time) {

		String currentTime = "";

		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
		currentTime = dateFormat.format(time);

		return currentTime;
	}

	public long getCurrentDate() {
		Calendar c = Calendar.getInstance();

		return c.getTimeInMillis();

	}

	private void setViewColor(int id) {
		switch (id) {
		case R.id.radio_grey:
			postIt.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.grey_square));
			break;
		case R.id.radio_blue:
			postIt.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.blue_square));
			break;
		case R.id.radio_green:
			postIt.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.green_square));
			break;
		case R.id.radio_yellow:
			postIt.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.yellow_square));
			break;
		case R.id.radio_pink:
			postIt.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.txt_color));
			break;
		default:
			postIt.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.yellow_square));
			break;
		}
	}
	
	public void cancelAlarm() {
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	
		alarmManager.cancel(sender);
	}

}
