package com.skushnaryov.lighttask.lighttask.recievers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import com.skushnaryov.lighttask.lighttask.Constants
import com.skushnaryov.lighttask.lighttask.db.ReminderRepository

class ReminderOffReciever : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(Constants.EXTRAS_ID, -1)
        val reminderName = intent.getStringExtra(Constants.EXTRAS_NAME)
        val repeatTime = intent.getLongExtra(Constants.EXTRAS_TIME_REPEAT, -1L)
        if (id == -1 || reminderName.isEmpty() || repeatTime == -1L) {
            return
        }

        val offIntent = Intent(context, ReminderReciever::class.java).apply {
            action = Constants.REMINDER_RECIEVER
            val bundle = bundleOf(
                    Constants.EXTRAS_ID to id,
                    Constants.EXTRAS_NAME to reminderName,
                    Constants.EXTRAS_TIME_REPEAT to repeatTime)
            putExtras(bundle)
        }
        val offPending = PendingIntent.getBroadcast(context, id, offIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(offPending)

        ReminderRepository().updateIsOnById(id, false)
        NotificationManagerCompat.from(context).cancel(id)
    }
}