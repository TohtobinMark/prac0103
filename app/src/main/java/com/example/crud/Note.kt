package com.example.crud

data class Note (
    var id: Int = 0,
    var title: String = "",
    var content: String = "",
    var date: String = ""
)
    {
    fun getPreviewText(): String {
        val words = content.trim().split("\\s+".toRegex()) // Разбиваем по пробелам
        return if (words.size > 3) {
            words.take(3).joinToString(" ") + "..." // Берем первые 3 слова
        } else {
            content
        }
    }
}