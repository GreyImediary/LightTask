package com.skushnaryov.lighttask.lighttask.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.skushnaryov.lighttask.lighttask.*
import com.skushnaryov.lighttask.lighttask.db.Reminder
import com.skushnaryov.lighttask.lighttask.recievers.ReminderReciever
import com.skushnaryov.lighttask.lighttask.viewModels.ReminderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_reminder_create.view.*
import org.jetbrains.anko.notificationManager
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var controller: NavController
    private lateinit var reminderViewModel: ReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        controller = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(nav_bottom, controller)
        NavigationUI.setupActionBarWithNavController(this, controller)

        createChannels()

        fab.setOnClickListener {

            showReminderCreateDialog()
        }

        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
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

    private fun showReminderCreateDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_reminder_create, null)

        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.timeArr,
                android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        view.reminderTimeType_spinner.adapter = spinnerAdapter
        view.reminderTimeType_spinner.setSelection(0)

        val builder = AlertDialog.Builder(this)
                .setTitle(R.string.reminderDialogTitle)
                .setView(view)
                .setPositiveButton(R.string.create) { _, _ ->  onPositiveButtonClick(view)}
                .setNegativeButton(R.string.cancel) {dialogInterface, _ -> dialogInterface.cancel() }
        return builder.create().show()
    }

    private fun onPositiveButtonClick(view: View) {
        val remidnerName = view.remindeName_edit_text.text.toString()

        if (remidnerName.trim().isEmpty()) {
            view.reminderName_text_input.error = getString(R.string.reminderDialogError)
            return
        } else {
            view.reminderName_text_input.error = null
        }

        val time = view.reminderTime_edit_text.text.toString().toInt()
        val timeType = when (view.reminderTimeType_spinner.selectedItemId) {
            0L -> Constants.REMIND_MIN
            1L -> Constants.REMIND_HOUR
            2L -> Constants.REMIND_DAY
            else -> ""
        }

        val id = Random().nextInt(Int.MAX_VALUE)

        val reminder = Reminder(id, remidnerName, time, timeType)

        reminderViewModel.insert(reminder)

        createAlarmNotification(reminder)
    }

    private fun createAlarmNotification(reminder: Reminder) {
        val currentTime = Calendar.getInstance().let {
            it.set(Calendar.SECOND, 0)
            it.timeInMillis
        }
        val reminderTime = getAlarmTime(reminder.timeType, reminder.time)

        if (reminderTime == -1L) {
            return
        }


        val alarmIntent = Intent(this, ReminderReciever::class.java).apply {
            action = Constants.REMINDER_RECIEVER
            putExtra(Constants.EXTRAS_ID, reminder.id)
            putExtra(Constants.EXTRAS_NAME, reminder.name)
            putExtra(Constants.EXTRAS_TIME_REPEAT, reminderTime)
        }
        val alarmPending = PendingIntent.getBroadcast(this, reminder.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmMananger = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMananger.set(AlarmManager.RTC, currentTime + reminderTime, alarmPending)
    }

    private fun getAlarmTime(timeType: String, time: Int) = when (timeType) {
        Constants.REMIND_MIN -> time.minute
        Constants.REMIND_HOUR -> time.hour
        Constants.REMIND_DAY -> time.day
        else -> -1L
    }

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

            val taskRemindChannel = NotificationChannel(Constants.TASK_REMIND_CHANNEL_ID,
                    Constants.TASK_REMIND_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                    .apply {
                        enableLights(true)
                        enableVibration(true)
                        lightColor = R.color.channel_color
                    }
            notificationManager.createNotificationChannel(taskRemindChannel)

            val remindersChannel = NotificationChannel(Constants.REMINDERS_CHANNELS_ID,
                    Constants.REMINDERS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                    .apply {
                        enableLights(true)
                        enableVibration(true)
                        lightColor = R.color.channel_color
                    }
            notificationManager.createNotificationChannel(remindersChannel)
        }
    }
}
