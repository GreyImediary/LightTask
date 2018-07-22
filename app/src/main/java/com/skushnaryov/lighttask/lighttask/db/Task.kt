package com.skushnaryov.lighttask.lighttask.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks")
data class Task(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String,
        val timeBegin: Calendar,
        val timeEnd: Calendar? = null,
        val currentDay: Int,
        val listOfSubtasks: MutableList<Task> = mutableListOf(),
        val groupName: String = "",
        val scheduleName: String = ""
)