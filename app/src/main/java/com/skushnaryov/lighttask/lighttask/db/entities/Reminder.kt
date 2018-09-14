package com.skushnaryov.lighttask.lighttask.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder (
        @PrimaryKey
        val id: Int,
        val name: String,
        val time: Int,
        val timeType: String,
        val isOn: Boolean = true
)