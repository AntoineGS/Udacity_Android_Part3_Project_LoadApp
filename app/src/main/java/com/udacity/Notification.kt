package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.MainActivity.Companion.EXTRA_DOWNLOAD_TYPE
import com.udacity.MainActivity.Companion.EXTRA_STATUS
import com.udacity.MainActivity.Companion.DownloadType

private const val ID_NOTIFICATION = 0

fun NotificationManager.sendNotification(
    downloadType: DownloadType,
    status: Int,
    applicationContext: Context
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(EXTRA_DOWNLOAD_TYPE, downloadType)
    contentIntent.putExtra(EXTRA_STATUS, status)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        ID_NOTIFICATION,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        MainActivity.CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.download_completed))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(false)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.resources.getString(R.string.open_detail),
            contentPendingIntent
        )
        .setStyle(NotificationCompat.BigTextStyle())
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(ID_NOTIFICATION, builder.build())
}