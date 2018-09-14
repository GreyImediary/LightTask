package com.skushnaryov.lighttask.lighttask.db.repositories

import androidx.lifecycle.LiveData
import com.skushnaryov.lighttask.lighttask.LightTask
import com.skushnaryov.lighttask.lighttask.db.entities.Group
import kotlinx.coroutines.experimental.*

class GroupRepository {
    private val groupDao = LightTask.database.groupDao()

    fun insert(group: Group) = launch(CommonPool) { groupDao.insert(group) }

    fun delete(group: Group) = launch(CommonPool) { groupDao.delete(group) }

    fun update(group: Group) = launch(CommonPool) { groupDao.update(group) }

    fun deleteTaskGroupName(groupName: String) = launch(CommonPool) { groupDao.deleteTaskGroupName(groupName) }

    fun updateTaskGroupName(oldName: String, newName: String) = launch(CommonPool) { groupDao.updateTaskGroupName(oldName, newName) }

    fun getAllGroups(): LiveData<List<Group>> = groupDao.getAllGroups()
}