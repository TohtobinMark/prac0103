package com.example.crud

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crud.adapter.NoteAdapter
import com.example.crud.Dbhelp
import com.example.crud.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: Dbhelp
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = Dbhelp(this)

        initViews()
        setupRecyclerView()
        setupSearch()
        loadNotes()

        fab.setOnClickListener {
            showNoteDialog(null)
        }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter(
            notes = listOf(),
            onItemClick = { note -> showNoteDialog(note) },
            onItemLongClick = { note ->
                showDeleteDialog(note)
                true
            },
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    loadNotes()
                } else {
                    searchNotes(newText)
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            loadNotes()
            false
        }
    }

    private fun loadNotes() {
        val notes = dbHelper.getAllNotesAsList()
        adapter.updateList(notes)
    }

    private fun searchNotes(query: String) {
        val notes = dbHelper.searchNotes(query)
        adapter.updateList(notes)
    }

    private fun showNoteDialog(note: Note?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_note, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etContent = dialogView.findViewById<EditText>(R.id.etContent)
        val etDate = dialogView.findViewById<EditText>(R.id.etDate)

        note?.let {
            etTitle.setText(it.title)
            etContent.setText(it.content)
            etDate.setText(it.date)
        }

        AlertDialog.Builder(this)
            .setTitle(if (note == null) R.string.new_note else R.string.edit_note)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val title = etTitle.text.toString().trim()
                val content = etContent.text.toString().trim()
                val date = etDate.text.toString().trim()

                if (title.isEmpty() && content.isEmpty()) {
                    Toast.makeText(this, "Заметка не может быть пустой", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (note == null) {
                    // Создание новой заметки
                    val newNote = Note(title = title, content = content, date = date)
                    dbHelper.insertNote(newNote)
                    Toast.makeText(this, "Заметка создана", Toast.LENGTH_SHORT).show()
                } else {
                    // Обновление существующей
                    note.title = title
                    note.content = content
                    note.date = date
                    dbHelper.updateNote(note)
                    Toast.makeText(this, "Заметка обновлена", Toast.LENGTH_SHORT).show()
                }
                loadNotes()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteDialog(note: Note) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, note.title.ifEmpty { "Без заголовка" }))
            .setPositiveButton(R.string.delete) { _, _ ->
                dbHelper.deleteNote(note.id)
                loadNotes()
                Toast.makeText(this, "Заметка удалена", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
