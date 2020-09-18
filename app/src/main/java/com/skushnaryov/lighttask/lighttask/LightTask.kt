package com.skushnaryov.lighttask.lighttask

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skushnaryov.lighttask.lighttask.db.DataBase
import com.skushnaryov.lighttask.lighttask.db.entities.Group
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LightTask : Application() {
    companion object {
        private lateinit var database_: DataBase
        val database
            get() = database_
    }

    override fun onCreate() {
        super.onCreate()
        database_ = Room.databaseBuilder(applicationContext,
                DataBase::class.java, "database_.db")
                .addCallback(object : RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        GlobalScope.launch { database_.groupDao().insert(Group(name = getString(R.string.today))) }
                    }
                })
                .build()

    }
}