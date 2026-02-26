package com.example.crud

import android.content.Context
import android.*
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.datetime.Instant
import java.util.Date

const val TABLE_NAME = "Note"
const val COLUMN_ID = "id"
const val COLUMN_TITLE = "title"
const val COLUMN_CONTENT = "content"
const val COLUMN_DATE = "date"

class Dbhelp(context: Context): SQLiteOpenHelper(context, "mark.db", null, 3) {
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

    fun insertNote(title: String, content: String, date: String): Long {
        val db = this.writableDatabase // Получение дб в режими записи
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
            put(COLUMN_DATE, date)
        }
        val newRowId = db.insert(TABLE_NAME, null, values)
        db.close()
        return newRowId
    }

    fun getAllNotes(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun getAllNotesAsList(): List<Note> {
        val productList = mutableListOf<Note>()
        val db = this.readableDatabase // Get the database in read mode
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
                productList.add(note)
            }
        }
        cursor.close()
        db.close()
        return productList
    }
}