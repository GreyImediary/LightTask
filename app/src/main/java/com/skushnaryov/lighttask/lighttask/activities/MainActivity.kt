package com.skushnaryov.lighttask.lighttask.activities

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.skushnaryov.lighttask.lighttask.*
import com.skushnaryov.lighttask.lighttask.db.entities.Reminder
import com.skushnaryov.lighttask.lighttask.dialogs.FabDialog
import com.skushnaryov.lighttask.lighttask.utils.Constants
import com.skushnaryov.lighttask.lighttask.utils.NotificationUtils
import com.skushnaryov.lighttask.lighttask.viewModels.ReminderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_reminder_create.view.*
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.startActivity
import java.util.*

class MainActivity : AppCompatActivity(), FabDialog.OnFabDialogItemListener {

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
            val dialog = FabDialog()
            dialog.listener = this
            dialog.show(supportFragmentManager, "fab dialog")
        }

        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu) = inflateMenu(R.menu.about_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_about -> {
            startActivity<AboutActivity>()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (controller.currentDestination?.label == getString(R.string.tasks)
                && toolbar.title == getString(R.string.tasks)) {
            finish()
        } else {
            controller.popBackStack()
        }
    }

    override fun onFabDialogItemClick(position: Int) {
        when (position) {
            0 -> startActivity<AddActivity>()
            1 -> showReminderCreateDialog()
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
                .setPositiveButton(R.string.create) { _, _ -> onPositiveButtonClick(view) }
                .setNegativeButton(R.string.cancel) { dialogInterface, _ -> dialogInterface.cancel() }
        return builder.create().show()
    }

    private fun onPositiveButtonClick(view: View) {
        val remidnerName = view.reminderName_edit_text.text.toString()
        val time = view.reminderTime_edit_text.text.toString()

        if (remidnerName.trim().isEmpty()) {
            toast(getString(R.string.wrongReminderName))
            return
        }

        if (time.isEmpty()) {
            toast(getString(R.string.wrongReminderTime))
            return
        }

        val timeType = when (view.reminderTimeType_spinner.selectedItemId) {
            0L -> Constants.REMIND_MIN
            1L -> Constants.REMIND_HOUR
            2L -> Constants.REMIND_DAY
            else -> ""
        }

        val id = Random().nextInt(Int.MAX_VALUE)

        val reminder = Reminder(id, remidnerName, time.toInt(), timeType)

        reminderViewModel.insert(reminder)

        createAlarmNotification(reminder)
    }

    private fun createAlarmNotification(reminder: Reminder) {
        val reminderTime = reminder.time.getAlarmTime(reminder.timeType)

        if (reminderTime == -1L) {
            return
        }

        NotificationUtils.crtOrRmvReminderNotification(this, reminder.id, reminder.name, reminderTime)
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
