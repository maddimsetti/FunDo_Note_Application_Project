package com.example.fundoos.firebaseservce

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.fundoos.CreateNewNoteFragment
import com.example.fundoos.R
import com.example.fundoos.broadcastreceiver.CHANNEL_ID
import com.example.fundoos.broadcastreceiver.NotificationBroadcastReceiver
import com.example.fundoos.firebaseservce.ScheduledWorker.Companion.NOTIFICATION_MESSAGE
import com.example.fundoos.firebaseservce.ScheduledWorker.Companion.NOTIFICATION_TITLE
import com.example.fundoos.notification.NotificationUtil
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, CreateNewNoteFragment::class.java)

        // Check if message contains a data payload.


        message.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${message.data}")

            // Get Message details
            val title = message.data["title"]
            val content = message.data["notes"]

            // Check whether notification is scheduled or not
            val isScheduled = message.data["isScheduled"]?.toBoolean()
            isScheduled?.let {
                if (it) {
                    // This is Scheduled Notification, Schedule it
                    val scheduledTime = message.data["dateTime"]
                    scheduleAlarm(scheduledTime, title, content)
                } else {
                    // This is not scheduled notification, show it now
                    showNotification(title!!, content!!)
                }
            }

        }

    }

    private fun showNotification(title: String, message: String) {
        NotificationUtil(applicationContext).showNotification(title, message)
    }

    private fun scheduleAlarm(scheduledTimeString: String?, title: String?, message: String?) {
        val alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent =
            Intent(applicationContext, NotificationBroadcastReceiver::class.java).let { intent ->
                intent.putExtra(NOTIFICATION_TITLE, title)
                intent.putExtra(NOTIFICATION_MESSAGE, message)
                PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
            }

        // Parse Schedule time
        val scheduledTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .parse(scheduledTimeString!!)

        scheduledTime?.let {
            // With set(), it'll set non repeating one time alarm.
            alarmMgr.set(
                AlarmManager.RTC_WAKEUP,
                it.time,
                alarmIntent
            )
        }

    }
}

