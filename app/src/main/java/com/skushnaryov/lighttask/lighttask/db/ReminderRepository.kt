package com.skushnaryov.lighttask.lighttask.db

import com.skushnaryov.lighttask.lighttask.LightTask
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class ReminderRepository {
    private val reminderDao = LightTask.database.reminderDao()

    fun insert(reminder: Reminder) = launch(CommonPool) { reminderDao.insert(reminder) }

    fun delete(reminder: Reminder) = launch(CommonPool) { reminderDao.delete(reminder) }

    fun update(reminder: Reminder) = launch(CommonPool) { reminderDao.update(reminder) }

    fun updateIsOnById(id: Int, isOn: Boolean) = launch(CommonPool) { reminderDao.updateIsOnById(id, isOn) }

    fun getAllReminders() = reminderDao.getAllReminders()
}