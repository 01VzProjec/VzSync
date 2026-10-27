package com.vztechcore.vzsync.data.git

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CloneService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var collectJob: Job? = null
    private var lastRepoName: String = ""

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                lastRepoName = intent.getStringExtra(EXTRA_REPO_NAME).orEmpty()
                val initial = CloneNotificationHelper.build(
                    context = this,
                    repoName = lastRepoName,
                    percentage = 0,
                    phase = "Iniciando",
                    isIndeterminate = true,
                    cancelIntent = buildCancelIntent(),
                )
                startForeground(NOTIFICATION_ID, initial)
                observeProgress()
            }
            ACTION_STOP -> stopSelfSafely()
        }
        return START_NOT_STICKY
    }

    private fun observeProgress() {
        collectJob?.cancel()
        collectJob = CloneProgressBus.progress
            .onEach { progress ->
                if (progress == null) return@onEach
                lastRepoName = progress.repoName
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                if (progress.percentage >= 100) {
                    manager.notify(
                        NOTIFICATION_ID,
                        CloneNotificationHelper.buildSuccess(this, progress.repoName),
                    )
                } else {
                    manager.notify(
                        NOTIFICATION_ID,
                        CloneNotificationHelper.build(
                            context = this,
                            repoName = progress.repoName,
                            percentage = progress.percentage,
                            phase = progress.phase,
                            isIndeterminate = progress.isIndeterminate,
                            cancelIntent = buildCancelIntent(),
                        ),
                    )
                }
            }
            .launchIn(scope)

        CloneProgressBus.events
            .onEach { event ->
                if (event is CloneServiceEvent.Stop) stopSelfSafely()
            }
            .launchIn(scope)
    }

    private fun buildCancelIntent(): PendingIntent {
        return PendingIntent.getBroadcast(
            this,
            1,
            Intent(this, CloneCancelReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Clonagem de repositório",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Progresso da clonagem de repositórios"
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun stopSelfSafely() {
        collectJob?.cancel()
        collectJob = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    override fun onDestroy() {
        collectJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "vz_clone_progress"
        const val NOTIFICATION_ID = 4242
        const val ACTION_START = "com.vztechcore.vzsync.CLONE_START"
        const val ACTION_STOP = "com.vztechcore.vzsync.CLONE_STOP"
        const val EXTRA_REPO_NAME = "repo_name"

        fun start(context: android.content.Context, repoName: String) {
            val intent = Intent(context, CloneService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_REPO_NAME, repoName)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: android.content.Context) {
            val intent = Intent(context, CloneService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}