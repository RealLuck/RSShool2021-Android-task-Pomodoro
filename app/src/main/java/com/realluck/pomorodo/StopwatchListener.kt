package com.realluck.pomorodo

interface StopwatchListener {

    fun start(id: Int)

    fun stop(id: Int, currentMS: Long, isFinished: Boolean)

    fun delete(id: Int)


}