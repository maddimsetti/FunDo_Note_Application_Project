package com.example.fundoos.broadcastreceiver

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat.format
import androidx.core.app.NotificationCompat
import com.example.fundoos.service.NotesAlarmService
import com.example.fundoos.utils.Constants
import io.karn.notify.Notify
import okhttp3.internal.Util.format
import okhttp3.internal.http.HttpDate.format
import timber.log.Timber
import java.lang.String.format
import java.text.DateFormat
import java.text.MessageFormat.format
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotesAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val timeInMillis = intent?.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        when(intent?.action) {
            Constants.ACTION_SET_EXACT -> {
                if (context != null) {
                    timeInMillis?.let { convertDate(it) }?.let {
                        buildNotification(context, "Set Exact Time",
                            it
                        )
                    }
                }
            }
        }
    }

    private fun buildNotification(context: Context, title: String, content: String) {
        Notify
            .with(context)
            .content {
                this.title = title
                text = "I got triggered at - $content"
            }
            .show()
    }

    private fun setRepetitiveAlarm(alarmService: NotesAlarmService) {
        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(7)
            Timber.d("Set alarm for next week same time - ${convertDate(this.timeInMillis)}")
        }

        alarmService.setRepetitiveAlarm((calendar.timeInMillis))
    }

    private fun convertDate(timeInMillis: Long): String =
        android.text.format.DateFormat.format("dd/MM/yyy hh:mm:ss", timeInMillis).toString()

}