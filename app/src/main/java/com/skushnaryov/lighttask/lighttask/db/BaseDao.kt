package com.skushnaryov.lighttask.lighttask.db

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T)

    @Update
    fun update(item: T)

    @Delete
    fun delete(item: T)
}