package com.example.fundoos

class CreatingNewNotes(val id: Int,  val title: String,  val notes: String, val dateTime: String) {
    override fun toString(): String {
        return "CreatingNewNote(title=$title, notes=$notes, datetime=$dateTime)"

    }

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) {
            return false
        }
        other as CreatingNewNotes
        if(id != other.id) {
            return false
        }
        if(title != other.title) {
            return false
        }
        if(notes != other.notes) {
            return false
        }
        if(dateTime != other.dateTime) {
            return false
        }
        return true
    }
}