package com.vztechcore.vzsync.data.git

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.vztechcore.vzsync.MainActivity
import com.vztechcore.vzsync.R

object CloneNotificationHelper {

    fun build(
        context: Context,
        repoName: String,
        percentage: Int,
        phase: String,
        isIndeterminate: Boolean,
        cancelIntent: PendingIntent?,
    ): Notification {
        val contentIntent = buildContentIntent(context)

        val builder = NotificationCompat.Builder(context, CloneService.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setContentIntent(contentIntent)
            .setCustomContentView(
                buildRemoteViews(context, repoName, percentage, phase, isIndeterminate)
            )
            .setCustomBigContentView(
                buildRemoteViews(context, repoName, percentage, phase, isIndeterminate)
            )

        if (cancelIntent != null) {
            builder.addAction(0, "Cancelar", cancelIntent)
        }

        return builder.build()
    }

    fun buildSuccess(context: Context, repoName: String): Notification {
        val contentIntent = buildContentIntent(context)

        return NotificationCompat.Builder(context, CloneService.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setOngoing(false)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(contentIntent)
            .setCustomContentView(buildSuccessRemoteViews(context, repoName))
            .setCustomBigContentView(buildSuccessRemoteViews(context, repoName))
            .build()
    }

    private fun buildContentIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_CLONE_SHEET, true)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun buildRemoteViews(
        context: Context,
        repoName: String,
        percentage: Int,
        phase: String,
        isIndeterminate: Boolean,
    ): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.notification_clone_progress)
        rv.setTextViewText(R.id.nc_title, "VzSync")
        rv.setTextViewText(R.id.nc_subtitle, "VzSync | Vtzin x • VzTechCore")
        rv.setTextViewText(
            R.id.nc_percentage,
            if (isIndeterminate) phase else "$percentage% · $phase",
        )
        // setProgressBar é mais confiável que o atributo XML em RemoteViews.
        if (isIndeterminate) {
            rv.setProgressBar(R.id.nc_progress, 100, 0, true)
        } else {
            rv.setProgressBar(R.id.nc_progress, 100, percentage, false)
        }
        return rv
    }

    private fun buildSuccessRemoteViews(
        context: Context,
        repoName: String,
    ): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.notification_clone_progress)
        rv.setTextViewText(R.id.nc_title, "SUCCESSFULL by VzSync")
        rv.setTextViewText(R.id.nc_subtitle, repoName)
        rv.setTextViewText(R.id.nc_percentage, "100%")
        rv.setProgressBar(R.id.nc_progress, 100, 100, false)
        return rv
    }
}