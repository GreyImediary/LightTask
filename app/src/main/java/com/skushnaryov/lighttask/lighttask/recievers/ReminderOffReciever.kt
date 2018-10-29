package com.skushnaryov.lighttask.lighttask.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.skushnaryov.lighttask.lighttask.utils.Constants
import com.skushnaryov.lighttask.lighttask.db.repositories.ReminderRepository
import com.skushnaryov.lighttask.lighttask.utils.NotificationUtils

class ReminderOffReciever : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(Constants.EXTRAS_ID, -1)
        val reminderName = intent.getStringExtra(Constants.EXTRAS_NAME)
        val reminderTime = intent.getLongExtra(Constants.EXTRAS_TIME_REPEAT, -1L)
        if (id == -1 || reminderName.isEmpty() || reminderTime == -1L) {
            return
        }

        NotificationUtils.crtOrRmvReminderNotification(context, id, reminderName, reminderTime, true)

        ReminderRepository().updateIsOnById(id, false)
        NotificationManagerCompat.from(context).cancel(id)
    }
}