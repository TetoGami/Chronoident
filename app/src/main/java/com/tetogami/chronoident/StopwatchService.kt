package com.tetogami.chronoident

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class StopwatchService : Service() {

    companion object {
        const val ACTION_START = "com.tetogami.chronoident.ACTION_START"
        const val ACTION_STOP = "com.tetogami.chronoident.ACTION_STOP"
        const val ACTION_RESET = "com.tetogami.chronoident.ACTION_RESET"
        const val ACTION_TIME_UPDATE = "com.tetogami.chronoident.ACTION_TIME_UPDATE"
        const val EXTRA_TIME = "extra_time"
        const val EXTRA_CUSTOM_START_TIME = "extra_custom_start_time"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "stopwatch_channel"
    }

    private var startTime = 0L
    private var elapsedTime = 0L
    private var customStartTime = 0L
    private var isRunning = false
    
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var notificationManager: NotificationManager
    
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime + customStartTime
                updateNotification()
                broadcastTimeUpdate()
                handler.postDelayed(this, 100)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                customStartTime = intent.getLongExtra(EXTRA_CUSTOM_START_TIME, 0L)
                startStopwatch()
            }
            ACTION_STOP -> {
                stopStopwatch()
            }
            ACTION_RESET -> {
                resetStopwatch()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startStopwatch() {
        if (!isRunning) {
            startTime = System.currentTimeMillis()
            isRunning = true
            startForeground(NOTIFICATION_ID, createNotification())
            handler.post(updateRunnable)
        }
    }

    private fun stopStopwatch() {
        isRunning = false
        handler.removeCallbacks(updateRunnable)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun resetStopwatch() {
        isRunning = false
        elapsedTime = customStartTime
        handler.removeCallbacks(updateRunnable)
        broadcastTimeUpdate()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, StopwatchService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val resetIntent = Intent(this, StopwatchService::class.java).apply {
            action = ACTION_RESET
        }
        val resetPendingIntent = PendingIntent.getService(
            this,
            2,
            resetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.stopwatch_notification))
            .setContentText(formatTime(elapsedTime))
            .setSmallIcon(android.R.drawable.ic_device_access_time)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_pause, getString(R.string.stop), stopPendingIntent)
            .addAction(android.R.drawable.ic_menu_revert, getString(R.string.reset), resetPendingIntent)
            .build()
    }

    private fun updateNotification() {
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun broadcastTimeUpdate() {
        val intent = Intent(ACTION_TIME_UPDATE).apply {
            putExtra(EXTRA_TIME, formatTime(elapsedTime))
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun formatTime(timeInMs: Long): String {
        val seconds = (timeInMs / 1000) % 60
        val minutes = (timeInMs / (1000 * 60)) % 60
        val hours = (timeInMs / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}