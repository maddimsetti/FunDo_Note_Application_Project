package com.example.fundoos.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.fundoos.CreateNewNoteFragment
import com.example.fundoos.broadcastreceiver.NotesAlarmReceiver
import com.example.fundoos.utils.RandomUtils

class NotesAlarmService(private val context: Context) {
    private val alarmManager: AlarmManager?=
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    fun setExactAlarm(timeInMillis: Long) {
        setAlarm(timeInMillis, getPendingIntent(getIntent().apply{
            action = com.example.fundoos.utils.Constants.ACTION_SET_EXACT
            putExtra(com.example.fundoos.utils.Constants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
        }))
    }

    fun setRepetitiveAlarm(timeInMillis: Long) {
        setAlarm(timeInMillis, getPendingIntent(getIntent().apply {
            action = com.example.fundoos.utils.Constants.ACTION_SET_EXACT
            putExtra(com.example.fundoos.utils.Constants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
        }))
    }

    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager?.let {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent
                )
            }
        }

    }

    private fun getPendingIntent(intent: Intent) =
        PendingIntent.getBroadcast(context, getRandomRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

    private fun getIntent() = Intent(context, NotesAlarmReceiver::class.java)

    private fun getRandomRequestCode() = RandomUtils.getRandomInt()
}


