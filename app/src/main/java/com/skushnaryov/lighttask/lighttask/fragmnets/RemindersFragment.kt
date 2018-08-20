package com.skushnaryov.lighttask.lighttask.fragmnets

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.skushnaryov.lighttask.lighttask.*
import com.skushnaryov.lighttask.lighttask.adapters.ReminderRecyclerView
import com.skushnaryov.lighttask.lighttask.db.Reminder
import com.skushnaryov.lighttask.lighttask.recievers.ReminderReciever
import com.skushnaryov.lighttask.lighttask.viewModels.ReminderViewModel
import kotlinx.android.synthetic.main.fragment_reminders.*
import java.util.*

class RemindersFragment : Fragment(), ReminderRecyclerView.OnReminderSwitchChange {
    lateinit var reminderViewModel: ReminderViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ReminderRecyclerView(this)
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

    override fun onPopupDeleteClick(reminder: Reminder, context: Context) {
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

        val alarmMananger = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMananger.cancel(alarmPending)

        reminderViewModel.delete(reminder)
    }

    private fun getAlarmTime(timeType: String, time: Int) = when (timeType) {
        Constants.REMIND_MIN -> time.minute
        Constants.REMIND_HOUR -> time.hour
        Constants.REMIND_DAY -> time.day
        else -> -1L
    }
}