package com.example.weatherforcast.data.roomdb

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforcast.data.entity.ApiObj


@Database(entities = arrayOf(ApiObj::class), version = 1)
@TypeConverters(Converters::class)
abstract class ApiObjDataBase : RoomDatabase() {
    abstract fun apiObjDao(): ApiObjDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ApiObjDataBase? = null

        fun getDatabase(application: Application): ApiObjDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        application.applicationContext,
                        ApiObjDataBase::class.java,
                        "ApiObjDB"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}