package com.skushnaryov.lighttask.lighttask.viewModels

import androidx.lifecycle.ViewModel
import com.skushnaryov.lighttask.lighttask.db.entities.Group
import com.skushnaryov.lighttask.lighttask.db.repositories.GroupRepository

class GroupViewModel : ViewModel() {
    private val repository = GroupRepository()
    val allGroups = repository.getAllGroups()

    fun insert(group: Group) = repository.insert(group)

    fun delete(group: Group) = repository.delete(group)

    fun update(group: Group) = repository.update(group)

    fun updateTaskGroupName(oldName: String, newName: String) = repository.updateTaskGroupName(oldName, newName)

    fun deleteTaskGroupName(groupName: String) = repository.deleteTaskGroupName(groupName)
}