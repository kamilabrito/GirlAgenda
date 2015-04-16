package com.girlagenda.girlagendaandroidapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author eduarda.menezes, jackeline.miranda, karina.pinheiro,
 *         bianca.nascimento
 * 
 */

public class BroadcastRestartAlarm extends BroadcastReceiver {

	private long atHour;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			SharedPreferences prefs = context.getSharedPreferences(
					Constants.GIRL_AGENDA, context.MODE_PRIVATE);
			atHour = prefs.getLong(Constants.TIME, 0);

			if (atHour != 0) {
				AlarmManager alarmMgr;
				PendingIntent alarmIntent;

				alarmMgr = (AlarmManager) context
						.getSystemService(context.ALARM_SERVICE);
				Intent myIntent = new Intent(context, AlarmReceiver.class);
				alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

				alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, atHour,
						AlarmManager.INTERVAL_DAY, alarmIntent);
			}
		}
	}

}
