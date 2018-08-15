package com.skushnaryov.lighttask.lighttask.viewModels

import androidx.lifecycle.ViewModel
import com.skushnaryov.lighttask.lighttask.db.Group
import com.skushnaryov.lighttask.lighttask.db.GroupRepository

class GroupViewModel : ViewModel() {
    private val repository = GroupRepository()
    val allGroups = repository.getAllGroups()

    fun insert(group: Group) = repository.insert(group)

    fun delete(group: Group) = repository.delete(group)

    fun update(group: Group) = repository.update(group)
}