package com.skushnaryov.lighttask.lighttask.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "tasks")
data class Task(
        @PrimaryKey
        val id: Int,
        val name: String,
        val date: Long,
        val currentDay: Int,
        val listOfSubtasks: MutableList<String> = arrayListOf(),
        val isCompound: Boolean = false,
        val groupName: String = ""
)