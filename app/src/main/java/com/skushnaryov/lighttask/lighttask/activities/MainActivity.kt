package com.skushnaryov.lighttask.lighttask.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.skushnaryov.lighttask.lighttask.Constants
import com.skushnaryov.lighttask.lighttask.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    private lateinit var controller: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        controller = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(nav_bottom, controller)
        NavigationUI.setupActionBarWithNavController(this, controller)

        createChannels()

        fab.setOnClickListener {
            startActivity<AddActivity>()
        }

        Log.i("CURRDEST", "${findNavController(R.id.nav_host_fragment).currentDestination.label}")
    }

    override fun onBackPressed() {
        if (controller.currentDestination.label == "Tasks" && toolbar.title == "Tasks") {
            finish()
        } else {
            controller.popBackStack()
        }
    }

    override fun onSupportNavigateUp(): Boolean =
            controller.navigateUp()

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val taskChannel = NotificationChannel(Constants.TASKS_CHANNEL_ID,
                    Constants.TASKS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                    .apply {
                        enableLights(true)
                        enableVibration(true)
                        lightColor = R.color.channel_color
                    }
            notificationManager.createNotificationChannel(taskChannel)

            val reminderChannel = NotificationChannel(Constants.REMINDERS_CHANNEL_ID,
                    Constants.REMINDERS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                    .apply {
                        enableLights(true)
                        enableVibration(true)
                        lightColor = R.color.channel_color
                    }
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }
}
