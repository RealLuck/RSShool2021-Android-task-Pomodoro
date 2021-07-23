package com.realluck.pomorodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.realluck.pomorodo.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity(), LifecycleObserver, StopwatchListener
{

    private lateinit var binding: ActivityMainBinding
    private val stopwatches = mutableListOf<Stopwatch>()
    private var nextId = 0
    private var currentId = -1
    private val stopwatchAdapter = StopwatchAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.addNewStopwatchButton.setOnClickListener {
            val minutes: Int? = binding.addMinutes.text.toString().toIntOrNull()
            if (minutes != null && minutes != 0)
            {
                val totalMS: Long = (minutes * 60) * 1000L
                stopwatches.add(Stopwatch(nextId++, totalMS, false, totalMS))
                stopwatchAdapter.submitList(stopwatches.toList())
            }
        }
    }

    override fun start(id: Int)
    {
        currentId = id
        changeStopwatch(id, null , isStarted = true, isFinished = false)
    }

    override fun stop(id: Int, currentMS: Long, isFinished: Boolean)
    {
        if (id == currentId) currentId = -1
        changeStopwatch(id, currentMS, false, isFinished)
    }


    override fun delete(id: Int)
    {
        if (id == currentId) currentId = -1
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMS: Long?, isStarted: Boolean, isFinished: Boolean) {
        stopwatches.replaceAll {
            when {
                it.id == id                 -> Stopwatch(it.id, currentMS?: it.currentMS, isStarted, it.totalMS, it.isFinished)
                it.isStarted && !isFinished -> Stopwatch(it.id, currentMS?: it.currentMS, false, it.totalMS, it.isFinished)
                else                        -> {it}
            }
        }
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("TAG", "onAppBackground()")
        var currentMs = -10L
        stopwatches.forEach {
            if (it.isStarted) {
                currentMs = it.currentMS
            }
        }
        if (currentMs == -10L) return

        val startTime = System.currentTimeMillis()
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
        startIntent.putExtra(CURRENT_MS, currentMs)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d("TAG", "onAppForeground()")
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }
}

