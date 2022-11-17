package com.example.workmanagerexpo

import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.workmanagerexpo.databinding.ActivityMainBinding
import com.example.workmanagerexpo.work.NotifyWork
import com.example.workmanagerexpo.work.NotifyWork.Companion.NOTIFICATION_ID
import com.example.workmanagerexpo.work.NotifyWork.Companion.NOTIFICATION_WORK
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar.make
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userInterface()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun userInterface() {
        setSupportActionBar(binding.toolBar)
        val titleNotification = "Remainder"
        binding.collapsingToolbarL.title = titleNotification
        binding.floatingActionButton.setOnClickListener {
            val customCalender = Calendar.getInstance()
            customCalender.set(
                binding.datePicker.year,
                binding.datePicker.month,
                binding.datePicker.dayOfMonth,
                binding.timePicker.hour,
                binding.timePicker.minute,
                0
            )
            val customTime = customCalender.timeInMillis
            val currentTime = currentTimeMillis()
            if(customTime > currentTime){
                val data = Data.Builder().putInt(NOTIFICATION_ID,0).build()
                val delay = customTime - currentTime
                scheduleNotification(delay,data)
                val titleNotificationSchedule = getString(R.string.notification_schedule_title)
                val patternNotificationSchedule = getString(R.string.notification_schedule_pattern)
                make(
                    binding.coordinatorLayout,
                    titleNotificationSchedule + SimpleDateFormat(
                        patternNotificationSchedule, Locale.getDefault()
                    ).format(customCalender.time).toString(),
                    LENGTH_LONG
                ).show()
            }else{
                val errorNotificationSchedule = getString(R.string.notification_schedule_error)
                make(
                    binding.coordinatorLayout,
                    errorNotificationSchedule,
                    LENGTH_LONG
                ).show()
            }
        }
    }

    private fun scheduleNotification(delay: Long, data: Data) {
        val notificationWork = OneTimeWorkRequest.Builder(NotifyWork::class.java)
            .setInitialDelay(delay,TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(this)
        instanceWorkManager.beginUniqueWork(
            NOTIFICATION_WORK,
            ExistingWorkPolicy.REPLACE,
            notificationWork
        ).enqueue()
    }
}