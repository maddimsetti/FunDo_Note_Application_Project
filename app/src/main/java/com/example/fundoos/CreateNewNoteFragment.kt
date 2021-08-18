package com.example.fundoos

import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.fundoos.broadcastreceiver.CHANNEL_ID
import com.example.fundoos.broadcastreceiver.NotesAlarmBroadcast
import com.example.fundoos.firebase.PushNotification
import com.example.fundoos.firebase.RetrofitInstance
import com.example.fundoos.service.NotesAlarmService
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.custom_create_new_note_toolbar.*
import kotlinx.android.synthetic.main.fragment_create_new_note.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val TOPIC = "/topics/myTopic"
class CreateNewNoteFragment() : Fragment(), View.OnClickListener{

    private var timeToNotify: String? = null
    private var currentDate: String? = null
    private  lateinit var alarmService: NotesAlarmService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_note, container, false)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        create_new_note.visibility = View.GONE
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateAndTime = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        currentDate = dateAndTime.format(java.util.Date())

        create_newNote_dateTime.text = currentDate

        backButton_new_note.setOnClickListener(View.OnClickListener {
            activity?.onBackPressed()
        })

        content_notes_date.setOnClickListener(this)
        content_notes_time.setOnClickListener(this)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        save_new_note.setOnClickListener() {
            saveNote()
        }

    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.content_notes_date -> {
                selectDate()
            }

            R.id.content_notes_time -> {
                selectTime()
            }
        }
    }

    private fun selectTime() {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(context,
            OnTimeSetListener { _, hour, minute ->
                timeToNotify = "$hour:$minute"
                content_notes_time.setText(FormatTime(hour, minute))
            }, hour, minute, false
        )
        timePickerDialog.show()
    }

    private fun FormatTime(hour: Int, minute: Int): String? {
        var time: String = ""
        val formattedMinute: String = if (minute / 10 == 0) {
            "0$minute"
        } else {
            "" + minute
        }
        time = if (hour == 0) {
            "12:$formattedMinute AM"
        } else if (hour < 12) {
            "$hour:$formattedMinute AM"
        } else if (hour == 12) {
            "12:$formattedMinute PM"
        } else {
            val temp = hour - 12
            "$temp:$formattedMinute PM"
        }
        return time
    }

    private fun selectDate() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                OnDateSetListener { datePicker, year, month, day -> content_notes_date.setText(day.toString() + "-" + (month + 1) + "-" + year) },
                year,
                month,
                day
            )
        }
        datePickerDialog?.show()
    }


    private fun setRemainderForNotes(title: String, description: String, date: String, time: String) {
        val alarmManager: AlarmManager?=
            context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

        val intent: Intent = Intent(activity, NotesAlarmBroadcast::class.java)
        intent.putExtra("title", title)
        intent.putExtra("notes", description)
        intent.putExtra("date", date)
        intent.putExtra("time", time)

        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val dateAndTime = date + "" + timeToNotify
        val format: DateFormat = SimpleDateFormat("dd:MM:yyyy   hh:mm")
        try {
            val date: Date? = format.parse(dateAndTime)
            date?.time?.let {
                return@let alarmManager?.set(AlarmManager.RTC_WAKEUP,
                    it, pendingIntent)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun saveNote() {
        var isScheduled = true
        var dateTime: String = ""
        val title = New_Note_Title.text.toString()
        val notes = New_Note_Content.text.toString()
        val date = content_notes_date.text.toString()
        val time = content_notes_time.text.toString()



        val dataBaseHandler: DataBaseHandler? = activity?.let { DataBaseHandler(it.applicationContext) }
        if(!title.isNullOrEmpty() && !notes.isNullOrEmpty()) {
            val status = dataBaseHandler?.addNewNotes(Notes(id, title, notes, create_newNote_dateTime.text.toString()))

            if(isScheduled) {
                dateTime = date + "" + timeToNotify
            }

            PushNotification(
                Notes(id, title, notes, dateTime), TOPIC
            ).also {
                sendNotification(it)
            }

            if (status != null) {
                if(status > -1) {
                    Toast.makeText(context, "Record Saved", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Title or Notes Cannot be Empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("Notification", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("Notification", response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}