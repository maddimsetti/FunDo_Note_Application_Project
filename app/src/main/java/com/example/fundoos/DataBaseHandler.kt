package com.example.fundoos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHandler(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION ) {

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "FunDo Notes DataBase"
        const val TABLE_NAME = "NotesTable"

        const val Notes_ID = "id"
        const val Notes_Title = "title"
        const val Notes_note = "notes"
        const val Notes_dateTime = "dateTime"
    }
    override fun onCreate(database: SQLiteDatabase) {
        //creating table with fields
        val notesTableQuery = ("CREATE TABLE " + TABLE_NAME + " ("
                + Notes_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Notes_Title + " TEXT,"
                + Notes_note + " TEXT,"
                + Notes_dateTime + " TEXT)")

        database?.execSQL(notesTableQuery)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(database)
    }

    fun addNewNotes(notes: CreatingNewNotes): Long {
        val database = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(Notes_Title, notes.title)
        contentValues.put(Notes_note, notes.notes)
        contentValues.put(Notes_dateTime,notes.dateTime)

        //Inserting Row
        val success = database.insert(TABLE_NAME, null, contentValues)
        //2nd argument is String containing nullColumnHack

        database.close()    //closing database connection
        return success
    }

    //method to read data
    fun viewNotes(): MutableList<CreatingNewNotes> {

        val notesList = mutableListOf<CreatingNewNotes>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"

        val database = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = database.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            database.execSQL(selectQuery)
            return mutableListOf()
        }

        var id: Int
        var title: String
        var notes: String
        var dateTime: String

        if(cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(Notes_ID))
                title = cursor.getString(cursor.getColumnIndex(Notes_Title))
                notes = cursor.getString(cursor.getColumnIndex(Notes_note))
                dateTime = cursor.getString(cursor.getColumnIndex(Notes_dateTime))

                val newNotes = CreatingNewNotes(id = id, title = title, notes = notes, dateTime = dateTime)
                notesList.add(newNotes)
            } while (cursor.moveToNext())
        }
        return notesList
    }

    //Update the notes
    fun updateNotes(notes: CreatingNewNotes): Int {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Notes_Title, notes.title)
        contentValues.put(Notes_note, notes.notes)
        contentValues.put(Notes_dateTime,notes.dateTime)

        //Updating Row
        val success = database.update(TABLE_NAME, contentValues, Notes_ID + "=" +notes.id, null)

        database.close()
        return success
    }

    fun deleteNotes(notes: CreatingNewNotes): Int {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Notes_ID, notes.id)
        //Deleting the Row
        val success = database.delete(TABLE_NAME, Notes_ID + "=" +notes.id, null)
        database.close()
        return success
    }
}