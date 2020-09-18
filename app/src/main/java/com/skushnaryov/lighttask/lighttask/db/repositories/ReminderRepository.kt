package com.skushnaryov.lighttask.lighttask.db.repositories

import com.skushnaryov.lighttask.lighttask.LightTask
import com.skushnaryov.lighttask.lighttask.db.entities.Reminder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ReminderRepository {
    private val reminderDao = LightTask.database.reminderDao()

    fun insert(reminder: Reminder) = GlobalScope.launch { reminderDao.insert(reminder) }

    fun delete(reminder: Reminder) = GlobalScope.launch { reminderDao.delete(reminder) }

    fun update(reminder: Reminder) = GlobalScope.launch { reminderDao.update(reminder) }

    fun updateIsOnById(id: Int, isOn: Boolean) = GlobalScope.launch { reminderDao.updateIsOnById(id, isOn) }

    fun getAllReminders() = reminderDao.getAllReminders()
}