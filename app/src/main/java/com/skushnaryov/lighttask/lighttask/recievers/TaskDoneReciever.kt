package com.skushnaryov.lighttask.lighttask.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skushnaryov.lighttask.lighttask.Constants
import com.skushnaryov.lighttask.lighttask.R
import com.skushnaryov.lighttask.lighttask.db.TaskRepository
import com.skushnaryov.lighttask.lighttask.toast

class TaskDoneReciever : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(Constants.EXTRAS_ID, -1)

        if (id != -1) {
            TaskRepository().deleteById(id)
        }

        context.toast(context.getString(R.string.taskCompleted))
    }
}