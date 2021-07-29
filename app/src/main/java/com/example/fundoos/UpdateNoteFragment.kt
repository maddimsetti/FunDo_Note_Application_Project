package com.example.fundoos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.custom_create_new_note_toolbar.*
import kotlinx.android.synthetic.main.fragment_create_new_note.create_newNote_dateTime
import kotlinx.android.synthetic.main.fragment_update_note.*
import java.text.SimpleDateFormat


class UpdateNoteFragment : Fragment() {

    private var currentDate: String? = null

    private var titleText: String? = null
    private var contentText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateAndTime = SimpleDateFormat("dd/mm/yyyy hh:mm:ss")
        currentDate = dateAndTime.format(java.util.Date())

        create_newNote_dateTime.text = currentDate

        backButton_new_note.setOnClickListener(View.OnClickListener {
            activity?.onBackPressed()
        })

        getAndSetIntentData()
        save_new_note.setOnClickListener() {
            val dataBaseHandler: DataBaseHandler? =
                activity?.let { DataBaseHandler(it.applicationContext) }
            var title = Update_Note_Title.text.toString()
            var notes = Update_Note_Content.text.toString()
            var dateTime = create_newNote_dateTime.text.toString()

            val status = dataBaseHandler?.updateNotes(id, title, notes, dateTime)
            if (status != null) {
                if (status > -1) {
                    Toast.makeText(context, "Data Successfully submitted", Toast.LENGTH_SHORT)
                        .show()
                    Update_Note_Title.text.clear()
                    Update_Note_Content.text.clear()
                }
            }
        }
    }

    fun getAndSetIntentData() {
        var title = Update_Note_Title.text.toString()
        var notes = Update_Note_Content.text.toString()

        if (activity?.intent!!.hasExtra("title") && activity?.intent!!
                .hasExtra("contentNotes")
        ) {

            titleText = activity?.intent?.getStringExtra("title").toString()
            contentText = activity?.intent?.getStringExtra("contentNotes").toString()

            //setting Intent Data
            Update_Note_Title.setText(titleText)
            Update_Note_Content.setText(contentText)

        } else {
            Toast.makeText(context, "No Data available", Toast.LENGTH_SHORT).show()
        }
    }

}