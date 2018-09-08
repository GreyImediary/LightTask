package com.skushnaryov.lighttask.lighttask.fragmnets

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skushnaryov.lighttask.lighttask.*
import com.skushnaryov.lighttask.lighttask.adapters.ReminderRecyclerView
import com.skushnaryov.lighttask.lighttask.db.Reminder
import com.skushnaryov.lighttask.lighttask.recievers.ReminderReciever
import com.skushnaryov.lighttask.lighttask.viewModels.ReminderViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_reminder_create.view.*
import kotlinx.android.synthetic.main.fragment_reminders.*
import java.util.*

class RemindersFragment : Fragment(), ReminderRecyclerView.OnReminderListener {
    lateinit var reminderViewModel: ReminderViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ReminderRecyclerView(context!!, this)
        rv_reminders.layoutManager = LinearLayoutManager(context)
        rv_reminders.adapter = adapter

        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
        reminderViewModel.allReminders.observe(this, Observer {
            adapter.reminderList = it

            if (it.isEmpty()) {
                rv_reminders.gone()
                sleep_img.visible()
                noRemindes_textView.visible()
                summary_textView.visible()
            } else {
                rv_reminders.visible()
                sleep_img.gone()
                noRemindes_textView.gone()
                summary_textView.gone()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab) ?: return

        rv_reminders.onScrollListener { dy ->
            if (dy > 0 && fab.visibility == View.VISIBLE) {
                activity?.fab?.hide()
            } else if (dy < 0 && fab.visibility != View.VISIBLE) {
                activity?.fab?.show()
            }
        }
    }

    override fun onSwitchChecked(isChecked: Boolean, reminder: Reminder) {
        val currentTime = Calendar.getInstance().let {
            it.set(Calendar.SECOND, 0)
            it.timeInMillis
        }
        val reminderTime = getAlarmTime(reminder.timeType, reminder.time)

        if (reminderTime == -1L) {
            return
        }

        val alarmIntent = Intent(context, ReminderReciever::class.java).apply {
            action = Constants.REMINDER_RECIEVER
            putExtra(Constants.EXTRAS_ID, reminder.id)
            putExtra(Constants.EXTRAS_NAME, reminder.name)
            putExtra(Constants.EXTRAS_TIME_REPEAT, reminderTime)
        }
        val alarmPending = PendingIntent.getBroadcast(context, reminder.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmMananger = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (isChecked) {
            alarmMananger.set(AlarmManager.RTC, currentTime + reminderTime, alarmPending)
            reminderViewModel.updateIsOnById(reminder.id, true)
        } else {
            alarmMananger.cancel(alarmPending)
            reminderViewModel.updateIsOnById(reminder.id, false)
        }
    }

    override fun onPopupItemClick(itemId: Int, reminder: Reminder) = when (itemId) {
        R.id.action_delete -> {
            deleteReminder(reminder)
            true
        }
        R.id.action_change -> {
            changeReminderDialog(reminder)
            true
        }
        else -> false
    }

    private fun deleteReminder(reminder: Reminder) {
        val reminderTime = getAlarmTime(reminder.timeType, reminder.time)

        if (reminderTime == -1L) {
            return
        }

        val alarmIntent = Intent(context, ReminderReciever::class.java).apply {
            action = Constants.REMINDER_RECIEVER
            putExtra(Constants.EXTRAS_ID, reminder.id)
            putExtra(Constants.EXTRAS_NAME, reminder.name)
            putExtra(Constants.EXTRAS_TIME_REPEAT, reminderTime)
        }
        val alarmPending = PendingIntent.getBroadcast(context, reminder.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmPending)

        reminderViewModel.delete(reminder)
    }

    private fun changeReminderDialog(reminder: Reminder) {
        val view = layoutInflater.inflate(R.layout.dialog_reminder_create, null)

        val spinnerAdapter = ArrayAdapter.createFromResource(context, R.array.timeArr,
                android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        view.reminderTimeType_spinner.adapter = spinnerAdapter
        view.reminderTimeType_spinner.setSelection(0)
        view.reminderName_edit_text.setText(reminder.name, TextView.BufferType.EDITABLE)
        view.reminderTime_edit_text.setText(reminder.time.toString(), TextView.BufferType.EDITABLE)
        when (reminder.timeType) {
            Constants.REMIND_MIN -> view.reminderTimeType_spinner.setSelection(0)
            Constants.REMIND_HOUR -> view.reminderTimeType_spinner.setSelection(1)
            Constants.REMIND_DAY -> view.reminderTimeType_spinner.setSelection(2)
        }

        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.changeRemidnerDialogTitle)
                .setView(view)
                .setPositiveButton(R.string.change) { _, _ ->  changeReminder(reminder, view)}
                .setNegativeButton(R.string.cancel) {dialogInterface, _ -> dialogInterface.cancel() }
        return builder.create().show()

    }

    private fun changeReminder(reminder: Reminder, view: View) {
        deleteAram(reminder)

        val remidnerName = view.reminderName_edit_text.text.toString()

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

        val id = reminder.id

        val changeReminder = Reminder(id, remidnerName, time, timeType)

        reminderViewModel.update(changeReminder)
        createAlarm(changeReminder)
    }

    private fun createAlarm(reminder: Reminder) {
        val currentTime = Calendar.getInstance().let {
            it.set(Calendar.SECOND, 0)
            it.timeInMillis
        }

        val reminderTime = getAlarmTime(reminder.timeType, reminder.time)

        if (reminderTime == -1L) {
            return
        }


        val alarmIntent = Intent(context, ReminderReciever::class.java).apply {
            action = Constants.REMINDER_RECIEVER
            putExtra(Constants.EXTRAS_ID, reminder.id)
            putExtra(Constants.EXTRAS_NAME, reminder.name)
            putExtra(Constants.EXTRAS_TIME_REPEAT, reminderTime)
        }
        val alarmPending = PendingIntent.getBroadcast(context, reminder.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmMananger = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMananger.set(AlarmManager.RTC, currentTime + reminderTime, alarmPending)
    }

    private fun deleteAram(reminder: Reminder) {
        val reminderTime = getAlarmTime(reminder.timeType, reminder.time)

        if (reminderTime == -1L) {
            return
        }

        val alarmIntent = Intent(context, ReminderReciever::class.java).apply {
            action = Constants.REMINDER_RECIEVER
            putExtra(Constants.EXTRAS_ID, reminder.id)
            putExtra(Constants.EXTRAS_NAME, reminder.name)
            putExtra(Constants.EXTRAS_TIME_REPEAT, reminderTime)
        }
        val alarmPending = PendingIntent.getBroadcast(context, reminder.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmMananger = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMananger.cancel(alarmPending)
    }

    private fun getAlarmTime(timeType: String, time: Int) = when (timeType) {
        Constants.REMIND_MIN -> time.minute
        Constants.REMIND_HOUR -> time.hour
        Constants.REMIND_DAY -> time.day
        else -> -1L
    }
}