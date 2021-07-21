package com.realluck.pomorodo


import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.realluck.pomorodo.databinding.StopwatchItemBinding

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources,
): RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMS.displayTime()
        if(stopwatch.isStarted) {
            setIsRecyclable(false)
        } else if(!isRecyclable) {
            setIsRecyclable(true)
        }
        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        } else {
            stopTimer(stopwatch)
        }

        initButonsListeners(stopwatch)
    }

    private fun initButonsListeners(stopwatch: Stopwatch)
    {
        binding.startPauseButton.setOnClickListener {
            when
            {
                stopwatch.isStarted ->
                {
                    listener.stop(stopwatch.id, stopwatch.currentMS)
                }
                else ->
                {
                    listener.start((stopwatch.id))
                }
            }
        }


        binding.deleteButton.setOnClickListener {
            if(!isRecyclable) setIsRecyclable(true)
            listener.delete(stopwatch.id)
        }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        binding.startPauseButton.text="Stop"


        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.blinkingIndicator.isVisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch)
    {
        binding.startPauseButton.text="Start"


        timer?.cancel()

        binding.blinkingIndicator.isVisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()

    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(stopwatch.currentMS, UNIT_HUNDRED_MS) {

            override fun onTick(millisUntilFinished: Long)
            {
                stopwatch.currentMS = millisUntilFinished
                binding.stopwatchTimer.text = stopwatch.currentMS.displayTime()
            }


            override fun onFinish()
            {
                with(binding) {
                    root.setCardBackgroundColor(ResourcesCompat.getColor(resources, R.color.tomato, null))
                    startPauseButton.setBackgroundColor((ResourcesCompat.getColor(resources, R.color.tomato_dark, null)))
                    deleteButton.setColorFilter(Color.parseColor("#95353C"))
                    deleteButton.setBackgroundColor((ResourcesCompat.getColor(resources, R.color.tomato, null)))
                    blinkingIndicator.isVisible = false
                    startPauseButton.isClickable = false
                    startPauseButton.text = "Finished"
                    setIsRecyclable(true)
                    (blinkingIndicator.background as? AnimationDrawable)?.stop()
                }

            }

        }
    }

    private fun Long.displayTime(): String {
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60


        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0)
        {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {
        private const val START_TIME = "00:00:00"
        private const val UNIT_HUNDRED_MS = 100L
    }
}