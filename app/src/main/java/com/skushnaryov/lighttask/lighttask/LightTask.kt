package com.skushnaryov.lighttask.lighttask

import android.app.Application
import androidx.room.Room
import com.skushnaryov.lighttask.lighttask.db.DataBase

class LightTask : Application() {
    companion object {
        private lateinit var database_: DataBase
        val database
            get() = database_
    }

    override fun onCreate() {
        super.onCreate()
        database_ = Room.databaseBuilder(applicationContext,
                DataBase::class.java, "database_.db").build()
    }
}