package com.example.fundoos

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_create_new_note.*
import kotlinx.android.synthetic.main.fragment_create_new_note.view.*
import kotlinx.android.synthetic.main.fragment_update_note.*
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import java.text.SimpleDateFormat

class RecyclerAdapter(
    val context: Context,
    private var notes: MutableList<Notes> = mutableListOf()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class NotesViewHolder(notesView: View) : RecyclerView.ViewHolder(notesView),
        View.OnClickListener {
        private var notesTitle: TextView = notesView.recycler_title
        private var notesDate: TextView = notesView.recycler_date
        private var notesContent: TextView = notesView.recycler_notes
        var menuItem: ImageButton = notesView.recycler_Button

        fun bind(notesPost: Notes) {
            notesTitle.text = notesPost.title
            notesDate.text = notesPost.dateTime
            notesContent.text = notesPost.notes
        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return NotesViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NotesViewHolder -> {
                holder.bind(notes[position])
                holder.menuItem.setOnClickListener() {
                    popupMenu(it, notes[position])
                }
//                holder.itemView.setOnClickListener() {
//                    val updateNoteFragment: UpdateNoteFragment ?= null
//                    var args: Bundle ?= null
//                    args?.putString("Your key", "Your value")
//                    updateNoteFragment?.arguments = args
//
//                }
            }
        }
    }

    private fun popupMenu(view: View?, notes: Notes) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.recycler_menu_items)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.recycler_edit -> {
                    editRecordAlertDialog(notes)
                    Toast.makeText(context, "The Selected Notes Edited", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.recycler_delete -> {
                    deleteRecordAlertDialog(notes)
                    Toast.makeText(context, "The Selected Notes Deleted", Toast.LENGTH_SHORT)
                        .show()
                    notifyDataSetChanged()
                    true
                }
                else -> false
            }

        }
        popupMenu.show()
    }

    private fun editRecordAlertDialog(notes: Notes) {
        var currentDate: String? = null
        val inflater = LayoutInflater.from(context)
        val subView = inflater.inflate(R.layout.fragment_create_new_note, null)
        val titleField: EditText = subView.New_Note_Title
        val contentField: EditText = subView.New_Note_Content
        val dateTimeField: TextView = subView.create_newNote_dateTime
        val dateAndTime = SimpleDateFormat("dd/mm/yyyy hh:mm:ss")
        currentDate = dateAndTime.format(java.util.Date())
        dateTimeField.text = currentDate
        titleField.setText(notes.title)
        contentField.setText(notes.notes)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Edit Notes")
        builder.setView(subView)
        builder.create()
        builder.setPositiveButton("Update") { _, _ ->

            val dataBaseHandler: DataBaseHandler? = context?.let { DataBaseHandler(it) }
            var title = titleField.text.toString()
            var content = contentField.text.toString()
            var dateTime = dateTimeField.text.toString()

            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
                Toast.makeText(context, "Please Enter the Fields", Toast.LENGTH_SHORT).show()
            } else {
                val status = dataBaseHandler?.updateNotes(notes.id, title, content, dateTime)
                if (status != null) {
                    if (status > -1) {
                        Toast.makeText(context, "Data Successfully submitted", Toast.LENGTH_SHORT)
                            .show()
                        titleField.text.clear()
                        contentField.text.clear()
                    }
                }

                (context as HomeActivity).finish()
                context.startActivity(context.intent)
            }

        }
        builder.setNegativeButton("Cancel") { _, _ ->
            Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()

        }
        builder.show()
    }

    private fun deleteRecordAlertDialog(notes: Notes) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Delete Note Record")
        builder.setMessage("Are you sure You wants to delete ${notes.id}")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Ok") { dialogInterface, which ->
            val dataBaseHandler: DataBaseHandler? = context?.let { DataBaseHandler(it) }
            val status = dataBaseHandler?.deleteNotes(Notes(notes.id, "", "", ""))
            if (status != null) {
                if (status > -1) {
                    Toast.makeText(context, "Record Delete Successfully", Toast.LENGTH_SHORT).show()
                }

                dialogInterface.dismiss()
            }
        }
        builder.setNegativeButton("Cancel") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun submitList(notesList: MutableList<Notes>) {
        notes = notesList
    }
}





