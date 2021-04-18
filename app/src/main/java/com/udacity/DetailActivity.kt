package com.udacity

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.MainActivity.Companion.DownloadType
import com.udacity.MainActivity.Companion.EXTRA_DOWNLOAD_TYPE
import com.udacity.MainActivity.Companion.EXTRA_STATUS
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val downloadType: DownloadType = intent.getSerializableExtra(EXTRA_DOWNLOAD_TYPE) as DownloadType
        val status: Int = intent.getIntExtra(EXTRA_STATUS, -1)

        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val notification: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notification.cancelAll()
    }

}
