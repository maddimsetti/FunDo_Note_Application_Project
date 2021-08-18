package com.example.fundoos.firebaseservce

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fundoos.notification.NotificationUtil

class ScheduledWorker(appContext: Context,
                      workParams: WorkerParameters): Worker(appContext, workParams){
    override fun doWork(): Result {
        Log.d("notification", "doWork: Work Start")

        // Get Notification Data
        val title = inputData.getString(NOTIFICATION_TITLE)
        val message = inputData.getString(NOTIFICATION_MESSAGE)

        // Show Notification
        NotificationUtil(applicationContext).showNotification(title!!, message!!)

        // TODO Do your other Background Processing

        Log.d(TAG, "Work DONE")
        // Return result

        return Result.success()
    }

    companion object {
        private const val TAG = "ScheduledWorker"
        const val NOTIFICATION_TITLE = "notification_title"
        const val NOTIFICATION_MESSAGE = "notification_message"
    }

}