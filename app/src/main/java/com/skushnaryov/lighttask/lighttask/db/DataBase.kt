package com.skushnaryov.lighttask.lighttask.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [(Task::class), (Group::class)], version = 1)
@TypeConverters(SubtasksConverter::class)
abstract class DataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun groupDao(): GroupDao
}