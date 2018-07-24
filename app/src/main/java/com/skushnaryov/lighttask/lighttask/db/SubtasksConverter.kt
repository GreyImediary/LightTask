package com.skushnaryov.lighttask.lighttask.db

import androidx.room.TypeConverter

class SubtasksConverter {

    @TypeConverter
    fun fromSubtaskList(list: List<String>): String {
        var string = ""
        list.forEach { string += "$it," }
        return string.trimEnd(',')
    }

    @TypeConverter
    fun toSubtaskList(string: String): List<String> {
        return string.split(",")
    }
}