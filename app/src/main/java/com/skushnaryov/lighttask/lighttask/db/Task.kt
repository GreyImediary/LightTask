package com.skushnaryov.lighttask.lighttask.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "tasks")
data class Task(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String,
        val date: Long,
        val currentDay: Int,
        val listOfSubtasks: MutableList<String> = mutableListOf(),
        val compoundPercent: String = "0%",
        val groupName: String = "",
        val scheduleName: String = ""
)