package com.skushnaryov.lighttask.lighttask.db

import androidx.room.TypeConverter

class SubtasksConverter {

    @TypeConverter
    fun fromSubtaskList(list: MutableList<String>): String {
        var string = ""
        if (!list.isEmpty()) {
            list.forEach { string += "$it," }
            return string.trimEnd(',')
        }
        return string
    }

    @TypeConverter
    fun toSubtaskList(string: String): MutableList<String> {
        if (string == "") {
            return arrayListOf()
        }
        return string.split(",").toMutableList()
    }
}