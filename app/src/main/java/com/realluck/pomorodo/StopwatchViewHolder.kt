package com.realluck.pomorodo



import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.util.Log
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
        binding.progressView.setPeriod(stopwatch.totalMS)

        if (stopwatch.currentMS < stopwatch.totalMS)
        {
            binding.progressView.setCurrent(stopwatch.totalMS- stopwatch.currentMS)
        } else binding.progressView.setCurrent(0)

        if(stopwatch.isStarted) {
            setIsRecyclable(false)
            Log.d("TAG", "setIsRecyclable(false)")
        } else if(!isRecyclable) {
            setIsRecyclable(true)
            Log.d("TAG", "setIsRecyclable(true)")
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
                    listener.stop(stopwatch.id, stopwatch.currentMS, stopwatch.isFinished)
                }
                else ->
                {   if (stopwatch.isFinished) {
                    stopwatchOnElements(stopwatch)

                }
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
        Log.d("TAG", "startTimer()")

        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.blinkingIndicator.isVisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch)
    {
        binding.startPauseButton.text="Start"
        Log.d("TAG", "stopTimer")

        timer?.cancel()

        binding.blinkingIndicator.isVisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()

    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(stopwatch.currentMS, UNIT_HUNDRED_MS) {

            override fun onTick(millisUntilFinished: Long)
            {
                stopwatch.currentMS = millisUntilFinished
                binding.progressView.setCurrent(stopwatch.totalMS - stopwatch.currentMS)
                binding.stopwatchTimer.text = stopwatch.currentMS.displayTime()
            }


            override fun onFinish()
            {
                stopwatchOffElements(stopwatch)
            }

        }
    }


    private fun stopwatchOffElements(stopwatch: Stopwatch){
        Log.d("TAG", "offElements")

        stopwatch.isFinished = true
        setIsRecyclable(true)
        Log.d("TAG", "setIsRecyclable(true) from OffElements")
        stopwatch.isStarted = false
        with(binding) {
            root.setCardBackgroundColor(ResourcesCompat.getColor(resources, R.color.tomato, null))
            startPauseButton.setBackgroundColor((ResourcesCompat.getColor(resources, R.color.tomato_dark, null)))
            deleteButton.setColorFilter(Color.parseColor("#95353C"))
            deleteButton.setBackgroundColor((ResourcesCompat.getColor(resources, R.color.tomato, null)))
            blinkingIndicator.isVisible = false
            progressView.isVisible = false
            startPauseButton.text = "Reset"
            (blinkingIndicator.background as? AnimationDrawable)?.stop()
        }
    }

    private fun stopwatchOnElements(stopwatch: Stopwatch){
        Log.d("TAG", "onElements")
        stopwatch.isFinished = false
        stopwatch.currentMS = stopwatch.totalMS

        with(binding) {

            root.setCardBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparent, null))
            startPauseButton.setBackgroundColor((ResourcesCompat.getColor(resources, R.color.purple_500, null)))
            deleteButton.setColorFilter(Color.parseColor("#FF6200EE"))
            deleteButton.setBackgroundColor((ResourcesCompat.getColor(resources, R.color.transparent, null)))
            blinkingIndicator.isVisible = true
            progressView.isVisible = true
            progressView.setCurrent(0)

        }
    }

}