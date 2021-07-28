package com.example.fundoos

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*

class RecyclerAdapter(
    val context: Context,
    private var notes: MutableList<CreatingNewNotes> = mutableListOf()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class NotesViewHolder(notesView: View) : RecyclerView.ViewHolder(notesView),
        View.OnClickListener {
        private var notesTitle: TextView = notesView.recycler_title
        private var notesDate: TextView = notesView.recycler_date
        private var notesContent: TextView = notesView.recycler_notes
        var menuItem: ImageView = notesView.recycler_Button

        init {
            menuItem.setOnClickListener() { popupMenu(it) }
        }

        private fun popupMenu(view: View?) {
            val popupMenu = PopupMenu(context.applicationContext, view)
            popupMenu.inflate(R.menu.recycler_menu_items)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.recycler_delete -> {
//                        deleteRecordAlertDialog(n)
                        Toast.makeText(context, "The Selected Notes Deleted", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }
                    else -> true
                }
                popupMenu.show()
                val popup = PopupMenu::class.java.getDeclaredField("popupMenu")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true) as Boolean

            }
        }

        fun bind(notesPost: CreatingNewNotes) {
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
                holder.bind(notes.get(position))
            }
        }
        holder.itemView.setOnClickListener() {

        }

    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun submitList(notesList: MutableList<CreatingNewNotes>) {
        notes = notesList
    }

    fun deleteRecordAlertDialog(notes: CreatingNewNotes) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Delete Note Record")
        builder.setMessage("Are you sure You wants to delete ${notes.id}")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, which ->
            val dataBaseHandler: DataBaseHandler? = context?.let { DataBaseHandler(it) }
            val status = dataBaseHandler?.deleteNotes(CreatingNewNotes(notes.id, "", "", ""))
            if (status != null) {
                if (status > -1) {
                    Toast.makeText(context, "Record Delete Successfully", Toast.LENGTH_SHORT).show()
                }

                dialogInterface.dismiss()
            }
            builder.setNegativeButton("No") { dialogInterface, which ->
                dialogInterface.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()

            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    class NotesItemDiffCallback(
        var oldNotesList: MutableList<CreatingNewNotes>,
        var newNotesList: MutableList<CreatingNewNotes>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldNotesList.size
        }

        override fun getNewListSize(): Int {
            return newNotesList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return (oldNotesList.get(oldItemPosition).id == newNotesList.get(newItemPosition).id)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldNotesList.get(oldItemPosition).equals(newNotesList.get(newItemPosition))
        }

    }


}





