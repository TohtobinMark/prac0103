package com.example.crud

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: Dbhelp
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        dbHelper = Dbhelp(this)

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Добавляем тестовые данные если нужно
        addSampleData()

        // Загружаем и отображаем заметки
        loadNotes()
    }

    private fun addSampleData() {
        // Проверяем есть ли уже данные
        val cursor = dbHelper.getAllNotes()
        if (cursor != null) {
            if (cursor.count == 0) {
                // Добавляем тестовые заметки
                dbHelper.insertNote( "Главное приложение", "Сделать RecyclerView для вывода списка заметок", "26.02.2026")
                dbHelper.insertNote("Метод ", "Реализовать метод loadNotes() для загрузки заметок из базы данных", "26.02.2026")
                dbHelper.insertNote( "База данных", "Настроить SQLiteOpenHelper для создания таблицы notes с полями:\n" +
                        "id, title, content, date.", "26.02.2026")
            }
            cursor.close()
        }
    }

    private fun loadNotes() {
        val products = dbHelper.getAllNotesAsList()
        noteAdapter = NoteAdapter(products) { note ->
            Toast.makeText(this, "Выбрано: ${note.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = noteAdapter
    }
}