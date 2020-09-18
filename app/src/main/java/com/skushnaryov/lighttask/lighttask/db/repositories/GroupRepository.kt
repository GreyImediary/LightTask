package com.skushnaryov.lighttask.lighttask.db.repositories

import androidx.lifecycle.LiveData
import com.skushnaryov.lighttask.lighttask.LightTask
import com.skushnaryov.lighttask.lighttask.db.entities.Group
import kotlinx.coroutines.*

class GroupRepository {
    private val groupDao = LightTask.database.groupDao()

    fun insert(group: Group) = GlobalScope.launch { groupDao.insert(group) }

    fun delete(group: Group) = GlobalScope.launch { groupDao.delete(group) }

    fun update(group: Group) = GlobalScope.launch { groupDao.update(group) }

    fun deleteTaskGroupName(groupName: String) = GlobalScope.launch { groupDao.deleteTaskGroupName(groupName) }

    fun updateTaskGroupName(oldName: String, newName: String) = GlobalScope.launch { groupDao.updateTaskGroupName(oldName, newName) }

    fun getAllGroups(): LiveData<List<Group>> = groupDao.getAllGroups()
}