package com.girlagenda.girlagendaandroidapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.girlagenda.girlagendaandroidapp.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * @author eduarda.menezes, jackeline.miranda, karina.pinheiro, bianca.nascimento 
 *
 */

public class ActivityRegister extends Activity {

	private EditText editTextPatientsName;
	private EditText editTextMedicineName;
	private TextView textViewDateBeginning;
	private TextView textViewDateFinish;
	private TextView textViewTime;
	private LinearLayout linearDateBeginning;
	private LinearLayout linearDateFinish;
	private LinearLayout linearTime;
	private RadioGroup radioGroupColor;
	private Button btnContinue;

	private DatePickerDialog fromDatePickerDialog;
	private DatePickerDialog toDatePickerDialog;
	private TimePickerDialog timePickerDialog;

	private String patient = "";
	private String medicine = "";
	private int color;
	private int startYear;
	private int startMonth;
	private int startDay;
	private int startHour;
	private int startMin;
	private String currenthour;

	private long fromDate = 0;
	private long toDate = 0;
	private long atHour = 0;
	private RadioGroup radioGroupSound;
	private int sound;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editTextPatientsName = (EditText) findViewById(R.id.editText_patients_name);
		editTextMedicineName = (EditText) findViewById(R.id.editText_medicine_name);
		textViewDateBeginning = (TextView) findViewById(R.id.textView_date_1);
		textViewDateFinish = (TextView) findViewById(R.id.textView_date_2);
		textViewTime = (TextView) findViewById(R.id.textView_time);
		btnContinue = (Button) findViewById(R.id.btn_continue);
		linearDateBeginning = (LinearLayout) findViewById(R.id.linear_date_beg);
		linearDateFinish = (LinearLayout) findViewById(R.id.linear_date_fin);
		linearTime = (LinearLayout) findViewById(R.id.linear_time);
		radioGroupColor = (RadioGroup) findViewById(R.id.radio_group_color);
		radioGroupSound = (RadioGroup) findViewById(R.id.radio_group_sound);

		getCurrentDate();

		textViewDateBeginning.setText(R.string.clicktosetdate);
		textViewDateFinish.setText(R.string.clicktosetdate);
		textViewTime.setText(R.string.clicktosettime);

		btnContinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				medicine = editTextMedicineName.getText().toString().trim();
				patient = editTextPatientsName.getText().toString().trim();
				color = radioGroupColor.getCheckedRadioButtonId();
				sound = radioGroupSound.getCheckedRadioButtonId();
				
				if ((medicine != null && !medicine.isEmpty()) 
						&& (patient != null && !patient.isEmpty()) 
						&& (color != -1)
						&& (sound != -1)
						&& (fromDate != 0)
						&& (atHour != 0)
						&& (toDate != 0)) {
					
					saveInformation();
					
					Intent actvityConfirmation = new Intent();
					actvityConfirmation.setClass(ActivityRegister.this, ActivityConfirmation.class);
					actvityConfirmation.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(actvityConfirmation);
				} else {
					Toast.makeText(getApplicationContext(), R.string.missinginfo, Toast.LENGTH_LONG).show();
				}
				
			}
		});

		linearDateBeginning.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				fromDatePickerDialog = new DatePickerDialog(
						ActivityRegister.this, listenerStartDate, startYear,
						startMonth, startDay);
				fromDatePickerDialog.show();

			}
		});

		linearDateFinish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toDatePickerDialog = new DatePickerDialog(
						ActivityRegister.this, listenerFinishDate, startYear,
						startMonth, startDay);
				toDatePickerDialog.show();
			}
		});

		linearTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				timePickerDialog = new TimePickerDialog(ActivityRegister.this,
						listenerTimePicker, startHour, startMin, true);
				timePickerDialog.show();
			}
		});

	}

	public void saveInformation() {
		SharedPreferences.Editor editor = getSharedPreferences(
				Constants.GIRL_AGENDA, MODE_PRIVATE).edit();
		editor.putString(Constants.MEDICINE, medicine);
		editor.putString(Constants.PATIENT, patient);
		editor.putLong(Constants.DATEBEGGING, fromDate);
		editor.putLong(Constants.DATEFINISH, toDate);
		editor.putLong(Constants.TIME, atHour);
		editor.putInt(Constants.COLOR, color);
		editor.putInt(Constants.SOUND, sound);
		editor.putBoolean(Constants.ALARM_STATUS, true);

		editor.commit();
	}

	private DatePickerDialog.OnDateSetListener listenerStartDate = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.YEAR, year);

			fromDate = cal.getTimeInMillis();
			textViewDateBeginning.setText(formatData(dayOfMonth, monthOfYear, year));

		}
	};

	private DatePickerDialog.OnDateSetListener listenerFinishDate = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.YEAR, year);

			toDate = cal.getTimeInMillis();
			textViewDateFinish.setText(formatData(dayOfMonth, monthOfYear, year));
		}
	};

	private TimePickerDialog.OnTimeSetListener listenerTimePicker = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);

			atHour = cal.getTimeInMillis();
			
			textViewTime.setText(formateHour(hourOfDay, minute));
		}
	};

	public void getCurrentDate() {
		Calendar c = Calendar.getInstance();
		startYear = c.get(Calendar.YEAR);
		startMonth = c.get(Calendar.MONTH);
		startDay = c.get(Calendar.DAY_OF_MONTH);

		startHour = c.get(Calendar.HOUR_OF_DAY);
		startMin = c.get(Calendar.MINUTE);

		long hour = c.getTimeInMillis();
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
		currenthour = dateFormat.format(hour);

	}

	public String formatData(int startDay, int startMonth, int startYear) {

		String data = String.valueOf(startDay) + "/"
				+ String.valueOf(startMonth + 1) + "/"
				+ String.valueOf(startYear);

		return data;
	}

	private CharSequence formateHour(int hourOfDay, int minute) {
		String hour = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);

		return hour;
	}
	

	

}
