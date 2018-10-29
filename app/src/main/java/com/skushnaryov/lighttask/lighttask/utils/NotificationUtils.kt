package com.skushnaryov.lighttask.lighttask.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.skushnaryov.lighttask.lighttask.recievers.ReminderReciever
import com.skushnaryov.lighttask.lighttask.recievers.TaskReciever
import com.skushnaryov.lighttask.lighttask.recievers.TaskRemindReceiver
import org.jetbrains.anko.bundleOf
import java.util.*

object NotificationUtils {

    fun crtOrRmvTaskNotification(context: Context,
                                 id: Int,
                                 name: String,
                                 date: Long = 0,
                                 isRemoving: Boolean = false) {

        val intent = Intent(context, TaskReciever::class.java).apply {
            action = Constants.TASK_RECIEVER
            putExtras(bundleOf(
                    Constants.EXTRAS_ID to id,
                    Constants.EXTRAS_NAME to name
            ))
        }

        val pending = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (isRemoving) {
            alarmManager.cancel(pending)
        } else {
            alarmManager.set(AlarmManager.RTC, date, pending)
        }
    }

    fun crtOrRmvTaskRemindNotification(context: Context,
                                 id: Int,
                                 text: String,
                                 date: Long = 0,
                                 isRemoving: Boolean = false) {
        val intent = Intent(context, TaskRemindReceiver::class.java).apply {
            action = Constants.TASK_REMIND_RECIEVER
            putExtras(bundleOf(
                    Constants.EXTRAS_ID to id,
                    Constants.EXTRAS_REMIND_TEXT to text
            ))
        }
        val pending = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (isRemoving) {
            alarmManager.cancel(pending)
        } else {
            alarmManager.set(AlarmManager.RTC, date, pending)
        }
    }

    fun crtOrRmvReminderNotification(context: Context,
                                     id: Int,
                                     name: String,
                                     time: Long,
                                     isRemoving: Boolean = false) {
        val currentTime = Calendar.getInstance().let {
            it.set(Calendar.SECOND, 0)
            it.timeInMillis
        }

        val intent = Intent(context, ReminderReciever::class.java).apply {
            action = Constants.REMINDER_RECIEVER
            putExtras(bundleOf(
                    Constants.EXTRAS_ID to id,
                    Constants.EXTRAS_NAME to name,
                    Constants.EXTRAS_TIME_REPEAT to time
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