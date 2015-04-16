package com.girlagenda.girlagendaandroidapp;

import java.text.SimpleDateFormat;

import com.girlagenda.girlagendaandroidapp.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author eduarda.menezes, jackeline.miranda, karina.pinheiro,
 *         bianca.nascimento
 * 
 */

public class ActivityConfirmation extends Activity {

	private TextView textViewNamePatient;
	private TextView textViewNameMedicine;
	private TextView textViewDateStart;
	private TextView textViewDateFinish;
	private TextView textViewTime;
	private TextView textViewSound;
	private Button btnRemove;
	private Button btnSave;
	private View viewColor;
	private String medicine;
	private String patient;
	private int color;

	private long fromDate;
	private long toDate;
	private long atHour;
	private int sound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmation);

		textViewNamePatient = (TextView) findViewById(R.id.textView_name_patient);
		textViewNameMedicine = (TextView) findViewById(R.id.textView_name_medicine);
		textViewDateStart = (TextView) findViewById(R.id.textView_date_start);
		textViewDateFinish = (TextView) findViewById(R.id.textView_date_finish);
		textViewTime = (TextView) findViewById(R.id.textView_time);
		textViewSound = (TextView) findViewById(R.id.textView_sound_state);
		btnRemove = (Button) findViewById(R.id.button_remove);
		btnSave = (Button) findViewById(R.id.button_save);
		viewColor = (View) findViewById(R.id.separator_chosen);

		btnRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cleanInformation();
				finish();
				Intent actvityConfirmation = new Intent();
				actvityConfirmation.setClass(ActivityConfirmation.this, ActivityShowAlert.class);
				actvityConfirmation.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(actvityConfirmation);

			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createAlarm();
				finish();
				Intent actvityConfirmation = new Intent();
				actvityConfirmation.setClass(ActivityConfirmation.this, ActivityShowAlert.class);
				actvityConfirmation.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(actvityConfirmation);

			}
		});
		getInformation();
		showInformation();
		
		setViewColor(color);
		
		if (sound == R.id.radioButton_on) {
			textViewSound.setText(R.string.on);
		} else {
			textViewSound.setText(R.string.off);
		}

	}

	public void showInformation() {
		textViewNamePatient.setText(patient);
		textViewNameMedicine.setText(medicine);
		textViewDateStart.setText(formatDate(fromDate));
		textViewDateFinish.setText(formatDate(toDate));
		textViewTime.setText(formatTime(atHour));
	}

	public void getInformation() {

		SharedPreferences prefs = getSharedPreferences(Constants.GIRL_AGENDA,
				MODE_PRIVATE);
		medicine = prefs.getString(Constants.MEDICINE, "");
		patient = prefs.getString(Constants.PATIENT, "");
		fromDate = prefs.getLong(Constants.DATEBEGGING, 0);
		toDate = prefs.getLong(Constants.DATEFINISH, 0);
		atHour = prefs.getLong(Constants.TIME, 0);
		color = prefs.getInt(Constants.COLOR, 0);
		sound = prefs.getInt(Constants.SOUND, 0);
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

	public void createAlarm() {

		AlarmManager alarmMgr;
		PendingIntent alarmIntent;

		alarmMgr = (AlarmManager) getApplicationContext().getSystemService(
				getApplicationContext().ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
				intent, 0);

		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, atHour,
				AlarmManager.INTERVAL_DAY, alarmIntent);
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

	private void setViewColor(int id) {
		switch (id) {
		case R.id.radio_grey:
			viewColor.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.grey_square));
			break;
		case R.id.radio_blue:
			viewColor.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.blue_square));
			break;
		case R.id.radio_green:
			viewColor.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.green_square));
			break;
		case R.id.radio_yellow:
			viewColor.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.yellow_square));
			break;
		case R.id.radio_pink:
			viewColor.setBackgroundColor(getApplicationContext().getResources()
					.getColor(R.color.txt_color));
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
