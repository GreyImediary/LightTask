package com.skushnaryov.lighttask.lighttask.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.skushnaryov.lighttask.lighttask.db.entities.Task

@Dao
interface TaskDao : BaseDao<Task> {

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE groupName=:group")
    fun getGroupTasks(group: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE currentDay=:day")
    fun getTodayTasks(day: Long): LiveData<List<Task>>

    @Query("DELETE FROM tasks WHERE id=:id")
    fun deleteTaskById(id: Int)
}