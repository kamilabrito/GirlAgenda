package com.girlagenda.girlagendaandroidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author eduarda.menezes, jackeline.miranda, karina.pinheiro,
 *         bianca.nascimento
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent intentActivity = new Intent();
		intentActivity.setClass(context, ActivityShowAlert.class);
		intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intentActivity.putExtra(Constants.KEY_INTENT, Constants.INTENT_FROM_ALARM);
		context.startActivity(intentActivity);

		WakeLocker.acquire(context);
	}
}
