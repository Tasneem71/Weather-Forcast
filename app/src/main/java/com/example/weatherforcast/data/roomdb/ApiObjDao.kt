package com.example.weatherforcast.data.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherforcast.data.entity.ApiObj

@Dao
interface ApiObjDao {

    @Query("SELECT * FROM ApiObj")
    fun getAllApiObj(): LiveData<List<ApiObj>>

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

}