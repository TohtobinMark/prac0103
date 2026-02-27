package com.example.crud

import android.content.Context
import android.*
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val TABLE_NAME = "Note"
const val COLUMN_ID = "id"
const val COLUMN_TITLE = "title"
const val COLUMN_CONTENT = "content"
const val COLUMN_DATE = "date"

class Dbhelp(context: Context): SQLiteOpenHelper(context, "mark.db", null, 4) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE " +
                "$TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_CONTENT TEXT," +
                "$COLUMN_DATE INSTANT)"
        db?.execSQL(createTableQuery)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getAllNotesAsList(): List<Note> {
        val noteList = mutableListOf<Note>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)


        cursor.use {
            val idColumn = it.getColumnIndex(COLUMN_ID)
            val titleColumn = it.getColumnIndex(COLUMN_TITLE)
            val contentColumn = it.getColumnIndex(COLUMN_CONTENT)
            val dateColumn = it.getColumnIndex(COLUMN_DATE)

            while (it.moveToNext()) {
                val note = Note(
                    id = it.getInt(idColumn),
                    title = it.getString(titleColumn),
                    content = it.getString(contentColumn),
                    date = it.getString(dateColumn)
                )
                noteList.add(note)
            }
        }
        cursor.close()
        db.close()
        return noteList
    }

    fun insertNote(note: Note): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_DATE, note.date)
        }
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun deleteNote(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun updateNote(note: Note): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_DATE, note.date)
        }
        val rows = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(note.id.toString()))
        db.close()
        return rows
    }

    fun searchNotes(query: String): List<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_TITLE LIKE ? OR $COLUMN_CONTENT LIKE ?",
            arrayOf("%$query%", "%$query%"),
            null, null, "$COLUMN_DATE DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val note = Note(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    content = it.getString(it.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE))
                )
                notes.add(note)
            }
        }
        db.close()
        return notes
    }
}