package com.skushnaryov.lighttask.lighttask

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.skushnaryov.lighttask.lighttask.db.Reminder
import com.skushnaryov.lighttask.lighttask.recievers.ReminderReciever
import org.jetbrains.anko.bundleOf
import java.util.*

object Constants {
    const val TASKS_CHANNEL_ID = "1"
    const val TASK_REMIND_CHANNEL_ID = "2"
    const val REMINDERS_CHANNELS_ID = "3"

    const val TASKS_CHANNEL_NAME = "Tasks"
    const val TASK_REMIND_CHANNEL_NAME = "Task reminders"
    const val REMINDERS_CHANNEL_NAME = "Reminders"

    const val TASK_RECIEVER = "com.skushnaryov.lighttask.TASK"
    const val TASK_DONE_RECIEVER = "com.skushnaryov.lighttask.TASK_DONE"
    const val TASK_REMIND_RECIEVER = "com.skushnaryov.lighttask.TASK_REMINDER"
    const val REMINDER_RECIEVER = "com.skushnaryov.lighttask.REMINDER"
    const val REMINDER_OFF_RECIEVER = "com.skushnaryov.lighttask.REMINDER_OFF"

    const val EXTRAS_ID = "id"
    const val EXTRAS_NAME = "name"
    const val EXTRAS_REMIND_TEXT = "text"
    const val EXTRAS_TIME_REPEAT = "repeat"

    const val CHANGE_ID = "task id"
    const val CHANGE_REMIND_ID = "task remind id"
    const val CHANGE_NAME = "task name"
    const val CHANGE_DATE = "task date"
    const val CHANGE_REMIND_DATE = "task remind date"
    const val CHANGE_CURRENT_DAY = "task current day"
    const val CHANGE_SUBTASKS = "task subtasks"
    const val CHANGE_GROUP = "task group"

    const val REMIND_MIN = "min"
    const val REMIND_HOUR = "hour"
    const val REMIND_DAY = "day"

    fun crtOrrmvRemindeNotification(context: Context,
                                    id: Int,
                                    name: String,
                                    time: Long,
                                    isRemoving: Boolean = false) {
        val currentTime = Calendar.getInstance().let {
            it.set(Calendar.SECOND, 0)
            it.timeInMillis
        }

        val intent = Intent(context, ReminderReciever::class.java).apply {
            action = REMINDER_RECIEVER
            putExtras(bundleOf(
                    EXTRAS_ID to id,
                    EXTRAS_NAME to name,
                    EXTRAS_TIME_REPEAT to time
            ))
        }
        val pending = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (isRemoving) {
            alarmManager.cancel(pending)
        } else {
            alarmManager.set(AlarmManager.RTC, currentTime + time, pending)
        }
    }
}