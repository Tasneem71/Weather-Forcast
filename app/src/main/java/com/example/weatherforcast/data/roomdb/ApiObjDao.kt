package com.example.weatherforcast.data.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherforcast.data.entity.ApiObj
import com.example.weatherforcast.data.entity.AlarmObj

@Dao
interface ApiObjDao {

    @Query("SELECT * FROM ApiObj")
    fun getAllApiObj(): LiveData<List<ApiObj>>

    @Query("SELECT * FROM ApiObj")
    fun getAllList(): List<ApiObj>

    @Query("SELECT * FROM ApiObj Where timezone = :timezone ")
    fun getApiObj(timezone:String): ApiObj

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(apiObj: ApiObj)

    @Query("DELETE FROM ApiObj")
    suspend fun deleteAll()

    @Query("DELETE FROM ApiObj WHERE timezone = :timezone")
        suspend fun deleteApiObj(timezone:String)

    @Delete
    suspend fun delete(apiObj: ApiObj): Void

    @Query("SELECT * FROM AlarmObj")
    fun getAllAlarms(): LiveData<List<AlarmObj>>

    @Query("SELECT * FROM AlarmObj Where id = :id ")
    fun getApiObj(id:Int): AlarmObj

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarmObj: AlarmObj):Long

    @Query("DELETE FROM AlarmObj WHERE id = :id")
    suspend fun deleteAlarmObj(id:Int)


}