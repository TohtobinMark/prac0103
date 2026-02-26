package com.example.crud

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


data class Note (
    var id: Int = 0,
    var title: String = "",
    var content: String = "",
    var date: String = ""
)