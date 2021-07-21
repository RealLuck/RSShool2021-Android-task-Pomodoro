package com.realluck.pomorodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.realluck.pomorodo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener
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
        changeStopwatch(id, null , true)
    }

    override fun stop(id: Int, currentMS: Long)
    {
        if (id == currentId) currentId = -1
        changeStopwatch(id, currentMS, false)
    }


    override fun delete(id: Int)
    {
        if (id == currentId) currentId = -1
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMS: Long?, isStarted: Boolean) {
        stopwatches.replaceAll {
            when {
                it.id == id -> Stopwatch(it.id, currentMS?: it.currentMS, isStarted, it.totalMS)
                it.isStarted -> Stopwatch(it.id, currentMS?: it.currentMS, false, it.totalMS)
                else -> {it}
            }
        }
        stopwatchAdapter.submitList(stopwatches.toList())
    }
}

