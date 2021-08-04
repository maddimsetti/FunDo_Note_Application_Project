package com.example.fundoos.broadcastreceiver

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.fundoos.utils.Constants

class NotesAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val timeInMillis = intent?.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        when(intent?.action) {
            Constants.ACTION_SET_EXACT -> {
                if (context != null) {
//                    buildNotification(context, "Set Exact Time", convertDate(timeInMillis))
                }
            }
        }
    }

    private fun buildNotification(context: Context, title: String, content: String) {
//        Notify
//            .with(context)
//            .content {
//                this.title = title
//                text = "I got triggered at - $message"
//            }
//            .show()
    }

}