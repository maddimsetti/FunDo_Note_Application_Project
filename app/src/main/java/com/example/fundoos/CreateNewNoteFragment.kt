package com.example.fundoos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.custom_create_new_note_toolbar.*
import kotlinx.android.synthetic.main.fragment_create_new_note.*
import java.text.SimpleDateFormat


class CreateNewNoteFragment: Fragment(){

    private var currentDate: String? = null

    lateinit var homeActivity: HomeActivity

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateAndTime = SimpleDateFormat("dd/mm/yyyy hh:mm:ss")
        currentDate = dateAndTime.format(java.util.Date())

        create_newNote_dateTime.text = currentDate

        homeActivity = HomeActivity()

        backButton_new_note.setOnClickListener(View.OnClickListener {
            activity?.onBackPressed()
        })

        save_new_note.setOnClickListener() {
            saveNote()
        }

    }

    private fun saveNote() {
        val title = New_Note_Title.text.toString()
        val notes = New_Note_Content.text.toString()
        val dateTime = create_newNote_dateTime.text.toString()

        val dataBaseHandler: DataBaseHandler? = activity?.let { DataBaseHandler(it.applicationContext) }
        if(!title.isNullOrEmpty() && !notes.isNullOrEmpty()) {
            val status = dataBaseHandler?.addNewNotes(CreatingNewNotes(0, title, notes, dateTime))

            //Storing the Data in Firebase Database
            val newNotes = CreatingNewNotes(id = id, title = title, notes = notes, dateTime = dateTime)
            val notesList = mutableListOf<CreatingNewNotes>()
            notesList.add(newNotes)

            //Storing the data to firebase authentication
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val firebaseReference = firebaseDatabase.reference
            firebaseReference.child("FunDo Note Data").push().setValue(notesList)

            if (status != null) {
                if(status > -1) {
                    Toast.makeText(context, "Record Saved", Toast.LENGTH_SHORT).show()
                    New_Note_Title.text.clear()
                    New_Note_Content.text.clear()
                }
            }
        } else {
            Toast.makeText(context, "Title or Notes Cannot be Empty", Toast.LENGTH_SHORT).show()
        }

    }
}