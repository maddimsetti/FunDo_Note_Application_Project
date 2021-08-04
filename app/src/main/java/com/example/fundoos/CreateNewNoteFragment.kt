package com.example.fundoos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.fundoos.service.NotesAlarmService
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.custom_create_new_note_toolbar.*
import kotlinx.android.synthetic.main.fragment_create_new_note.*
import java.text.SimpleDateFormat
import java.util.*


class CreateNewNoteFragment() : Fragment(){

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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateAndTime = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        currentDate = dateAndTime.format(java.util.Date())

        create_newNote_dateTime.text = currentDate

        backButton_new_note.setOnClickListener(View.OnClickListener {
            activity?.onBackPressed()
        })

        save_new_note.setOnClickListener() {
            saveNote()
        }

        alarmService = activity?.applicationContext?.let { NotesAlarmService(it) }!!

        remainder_new_note.setOnClickListener() {
            setAlarm { alarmService.setExactAlarm(it) }
        }

    }

    private fun saveNote() {
        val title = New_Note_Title.text.toString()
        val notes = New_Note_Content.text.toString()
        val dateTime = create_newNote_dateTime.text.toString()

        val dataBaseHandler: DataBaseHandler? = activity?.let { DataBaseHandler(it.applicationContext) }
        if(!title.isNullOrEmpty() && !notes.isNullOrEmpty()) {
            val status = dataBaseHandler?.addNewNotes(Notes(id, title, notes, dateTime))

            //Storing the Data in Firebase Database
//            val newNotes = Notes(id, title, notes, dateTime)
//            val notesList = mutableListOf<Notes>()
//            notesList.add(newNotes)
//
//            //Storing the data to firebase authentication
//            val firebaseDatabase = FirebaseDatabase.getInstance()
//            val firebaseReference = firebaseDatabase.reference
//            firebaseReference.child("FunDo Note Data").push().setValue(notesList)

            if (status != null) {
                if(status > -1) {
                    Toast.makeText(context, "Record Saved", Toast.LENGTH_SHORT).show()
//                    New_Note_Title.text.clear()
//                    New_Note_Content.text.clear()
                }
            }
        } else {
            Toast.makeText(context, "Title or Notes Cannot be Empty", Toast.LENGTH_SHORT).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun setAlarm(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {

            activity?.applicationContext?.let {
                DatePickerDialog(
                    it,
                    0,
                    { _, year, month, day ->
                        this.set(Calendar.YEAR, year)
                        this.set(Calendar.MONTH, month)
                        this.set(Calendar.DAY_OF_MONTH, day)
                        TimePickerDialog(
                            activity?.applicationContext,
                            0,
                            { _, hour, minute ->
                                this.set(Calendar.HOUR_OF_DAY, hour)
                                this.set(Calendar.MINUTE, minute)
                                callback(this.timeInMillis)
                            },
                            this.get(Calendar.HOUR_OF_DAY),
                            this.get(Calendar.MINUTE),
                            false
                        ).show()
                    },
                    this.get(Calendar.YEAR),
                    this.get(Calendar.MONTH),
                    this.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }
}