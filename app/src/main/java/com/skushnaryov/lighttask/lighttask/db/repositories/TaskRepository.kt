package com.skushnaryov.lighttask.lighttask.db.repositories

import com.skushnaryov.lighttask.lighttask.LightTask
import com.skushnaryov.lighttask.lighttask.db.entities.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TaskRepository {
    private val taskDao = LightTask.database.taskDao()

    fun insert(task: Task) = GlobalScope.launch { taskDao.insert(task) }

    fun update(task: Task) = GlobalScope.launch { taskDao.update(task) }

    fun delete(task: Task) = GlobalScope.launch { taskDao.delete(task) }

    fun deleteById(id: Int) = GlobalScope.launch { taskDao.deleteTaskById(id) }

    fun getAllTasks() = taskDao.getAllTasks()

    fun getGroupTasks(group: String) = taskDao.getGroupTasks(group)

    fun getTodayTasks(day: Long) = taskDao.getTodayTasks(day)
}