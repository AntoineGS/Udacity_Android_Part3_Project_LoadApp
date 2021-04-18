package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private var downloadID: Long = 0
    // only works since it is 1 d\l at a time
    private var lastDownloadType: DownloadType = DownloadType.GLIDE

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        custom_button.setLoadingButtonState(ButtonState.Completed)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            custom_button.setLoadingButtonState(ButtonState.Clicked)
            when (binding.content.optionsRadioGroup.checkedRadioButtonId) {
                View.NO_ID -> {
                    custom_button.setLoadingButtonState(ButtonState.Completed)
                    Toast.makeText(
                        this,
                        getString(R.string.select_download),
                        Toast.LENGTH_LONG
                    ).show()
                }
                R.id.glide_radio -> download(DownloadType.GLIDE)
                R.id.udacity_radio -> download(DownloadType.PROJECT)
                R.id.retrofit_radio -> download(DownloadType.RETROFIT)
            }
        }

        createDownloadChannel()
    }

    private fun createDownloadChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.download),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download_completed)

            notificationManager = getSystemService(NotificationManager::class.java)
                    as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                custom_button.setLoadingButtonState(ButtonState.Completed)
                Toast.makeText(
                    context,
                    getString(R.string.download_completed),
                    Toast.LENGTH_LONG
                ).show()

                val downloadManager =
                    getSystemService(DownloadManager::class.java) as DownloadManager
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)

                if (cursor != null) {
                    if (cursor.moveToLast()) {
                        val status =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                        notificationManager = getSystemService(NotificationManager::class.java)
                                as NotificationManager

                        notificationManager.sendNotification(
                            lastDownloadType, status, applicationContext
                        )
                    }
                }
            }
        }
    }

    private fun download(downloadType: DownloadType) {
        lastDownloadType = downloadType
        custom_button.setLoadingButtonState(ButtonState.Loading)
        val url = (
                when (downloadType) {
                    DownloadType.GLIDE -> GLIDE_URL
                    DownloadType.PROJECT -> PROJECT_URL
                    DownloadType.RETROFIT -> RETROFIT_URL
                }
                )

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val PROJECT_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        const val CHANNEL_ID = "channelId"
        const val EXTRA_DOWNLOAD_TYPE = "downloadType"
        const val EXTRA_STATUS = "status"

        enum class DownloadType { GLIDE, PROJECT, RETROFIT }
    }

}
