package com.skushnaryov.lighttask.lighttask.viewModels

import androidx.lifecycle.ViewModel
import com.skushnaryov.lighttask.lighttask.db.Task
import com.skushnaryov.lighttask.lighttask.db.TaskRepository

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()
    val allTasks = repository.getAllTasks()

    fun insert(task: Task) = repository.insert(task)

    fun update(task: Task) = repository.update(task)

    fun delete(task: Task) = repository.delete(task)

    fun getTodayTasks(day: Int) = repository.getTodayTasks(day)

    fun getGroupTasks(group: String) = repository.getGroupTasks(group)
}