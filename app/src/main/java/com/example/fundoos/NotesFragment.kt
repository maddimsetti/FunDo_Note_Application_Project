package com.example.fundoos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_notes.*

class NotesFragment : Fragment() {

//    private lateinit var linearLayoutManager: LinearLayoutManager
//    private lateinit var notesAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false)
//        setupListOfDataIntiRecyclerView()
    }

//    fun setupListOfDataIntiRecyclerView() {
//
//        if(getNotesList()?.size!! > 0) {
//            recycler_view.visibility = View.VISIBLE
//            backgroundImage.visibility = View.GONE
//            notes.visibility = View.GONE
//
//            recycler_view.apply {
//                linearLayoutManager = LinearLayoutManager(fragment_Container.context)
//                notesAdapter = getNotesList()?.let { RecyclerAdapter(it) }!!
//                adapter = notesAdapter
//            }
//        } else {
//            recycler_view.visibility = View.GONE
//            backgroundImage.visibility = View.VISIBLE
//            notes.visibility = View.VISIBLE
//
//        }
//
//    }
//
//    private fun getNotesList(): MutableList<CreatingNewNotes>? {
//        //creating the instance of DatabaseHandler class
//        var output: MutableList<CreatingNewNotes>? = null
//        val dataBaseHandler: DataBaseHandler? = activity?.let { DataBaseHandler(it.applicationContext) }
//        //calling the viewNotes method of DataBaseHandler
//
//        if (dataBaseHandler != null) {
//            output = dataBaseHandler.viewNotes()
//        }
//        return output
//    }
}