package com.realluck.pomorodo

data class Stopwatch(
    val id: Int,
    var currentMS: Long,
    var isStarted: Boolean,
    val totalMS: Long
)




