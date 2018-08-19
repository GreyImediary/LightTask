package com.skushnaryov.lighttask.lighttask.viewModels

import androidx.lifecycle.ViewModel
import com.skushnaryov.lighttask.lighttask.db.Reminder
import com.skushnaryov.lighttask.lighttask.db.ReminderRepository

class ReminderViewModel : ViewModel() {
    private val repository = ReminderRepository()
    val allReminders = repository.getAllReminders()

    fun insert(reminder: Reminder) = repository.insert(reminder)

    fun update(reminder: Reminder) = repository.update(reminder)

    fun delete(reminder: Reminder) = repository.delete(reminder)

    fun updateIsOnById(id: Int, isOn: Boolean) = repository.updateIsOnById(id, isOn)
}