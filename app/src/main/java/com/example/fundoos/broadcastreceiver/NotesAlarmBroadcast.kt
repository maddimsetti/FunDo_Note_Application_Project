package com.example.fundoos.broadcastreceiver

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fundoos.R

const val CHANNEL_ID = "notify_001"
class NotesAlarmBroadcast: BroadcastReceiver() {


    @SuppressLint("RemoteViewLayout")
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        val titleText = bundle?.getString("title")
        val notesText = bundle?.getString("notes")
        val dateText = bundle?.getString("date") + " " +
                bundle?.getString("time")

        val pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_ONE_SHOT)

        val contentView = RemoteViews(context?.packageName, R.layout.fragment_create_new_note)
        val notification = context?.let { NotificationCompat.Builder(it, "notify_001") }
        contentView.setTextViewText(R.id.New_Note_Title, titleText)
        contentView.setTextViewText(R.id.New_Note_Content, notesText)
        contentView.setTextViewText(R.id.create_newNote_dateTime, dateText)
        notification?.setSmallIcon(R.drawable.ic_baseline_alarm_24)
        notification?.setAutoCancel(true)
        notification?.setOngoing(true)
        notification?.setPriority(Notification.PRIORITY_HIGH)
        notification?.setOnlyAlertOnce(true)
        notification?.build()?.flags = Notification.FLAG_NO_CLEAR
        notification?.setContent(contentView)
        notification?.setContentIntent(pendingIntent)

        notification?.build()?.let {
            context?.let {
                NotificationManagerCompat.from(
                    it
                )
            }?.notify(100, it)
        }
    }


    }

