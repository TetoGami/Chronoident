package com.tetogami.chronoident

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tetogami.chronoident.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isRunning = false
    private var customStartTimeMs = 0L

    private val timeUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val timeString = intent?.getStringExtra(StopwatchService.EXTRA_TIME) ?: "00:00:00"
            binding.tvStopwatchTime.text = timeString
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        registerTimeUpdateReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeUpdateReceiver)
    }

    private fun setupClickListeners() {
        binding.btnStart.setOnClickListener {
            startStopwatch()
        }

        binding.btnStop.setOnClickListener {
            stopStopwatch()
        }

        binding.btnReset.setOnClickListener {
            resetStopwatch()
        }

        binding.btnSetCustomTime.setOnClickListener {
            setCustomStartTime()
        }
    }

    private fun registerTimeUpdateReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            timeUpdateReceiver,
            IntentFilter(StopwatchService.ACTION_TIME_UPDATE)
        )
    }

    private fun startStopwatch() {
        if (!isRunning) {
            val serviceIntent = Intent(this, StopwatchService::class.java).apply {
                action = StopwatchService.ACTION_START
                putExtra(StopwatchService.EXTRA_CUSTOM_START_TIME, customStartTimeMs)
            }
            ContextCompat.startForegroundService(this, serviceIntent)
            
            isRunning = true
            updateButtonStates()
        }
    }

    private fun stopStopwatch() {
        if (isRunning) {
            val serviceIntent = Intent(this, StopwatchService::class.java).apply {
                action = StopwatchService.ACTION_STOP
            }
            startService(serviceIntent)
            
            isRunning = false
            updateButtonStates()
        }
    }

    private fun resetStopwatch() {
        val serviceIntent = Intent(this, StopwatchService::class.java).apply {
            action = StopwatchService.ACTION_RESET
        }
        startService(serviceIntent)
        
        isRunning = false
        binding.tvStopwatchTime.text = formatTime(customStartTimeMs)
        updateButtonStates()
    }

    private fun setCustomStartTime() {
        try {
            val hours = binding.etHours.text.toString().toLongOrNull() ?: 0L
            val minutes = binding.etMinutes.text.toString().toLongOrNull() ?: 0L
            val seconds = binding.etSeconds.text.toString().toLongOrNull() ?: 0L

            if (hours > 23 || minutes > 59 || seconds > 59) {
                Toast.makeText(this, "Invalid time values", Toast.LENGTH_SHORT).show()
                return
            }

            customStartTimeMs = (hours * 3600 + minutes * 60 + seconds) * 1000
            binding.tvStopwatchTime.text = formatTime(customStartTimeMs)
            
            Toast.makeText(this, "Custom start time set", Toast.LENGTH_SHORT).show()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateButtonStates() {
        binding.btnStart.isEnabled = !isRunning
        binding.btnStop.isEnabled = isRunning
    }

    private fun formatTime(timeInMs: Long): String {
        val seconds = (timeInMs / 1000) % 60
        val minutes = (timeInMs / (1000 * 60)) % 60
        val hours = (timeInMs / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}