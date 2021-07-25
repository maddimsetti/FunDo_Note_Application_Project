package com.example.fundoos

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*

class RecyclerAdapter(private var notes: MutableList<CreatingNewNotes> = mutableListOf()):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return NotesViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is NotesViewHolder -> {
                holder.bind(notes.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun submitList(notesList: MutableList<CreatingNewNotes>) {
        notes = notesList
    }

    class NotesViewHolder(notesView: View): RecyclerView.ViewHolder(notesView), View.OnClickListener {
        private val notesTitle: TextView = notesView.recycler_title
        private val notesDate: TextView = notesView.recycler_date
        private val notesContent: TextView = notesView.recycler_notes

        fun bind(notesPost: CreatingNewNotes) {
            notesTitle.text = notesPost.title
            notesDate.text = notesPost.dateTime
            notesContent.text = notesPost.notes
        }

        override fun onClick(v: View?) {

        }
    }

}





