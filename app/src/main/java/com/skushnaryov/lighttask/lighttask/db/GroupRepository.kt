package com.skushnaryov.lighttask.lighttask.db

import androidx.lifecycle.LiveData
import com.skushnaryov.lighttask.lighttask.LightTask
import kotlinx.coroutines.experimental.*

class GroupRepository {
    private val groupDao = LightTask.database.groupDao()

    fun insert(group: Group) = launch(CommonPool) { groupDao.insert(group) }

    fun delete(group: Group) = launch(CommonPool) { groupDao.delete(group) }

    fun update(group: Group) = launch(CommonPool) { groupDao.update(group) }

    fun getAllGroups(): LiveData<List<Group>> = groupDao.getAllGroups()
}