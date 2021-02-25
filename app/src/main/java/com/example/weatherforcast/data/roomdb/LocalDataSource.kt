package com.example.weatherforcast.data.roomdb

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.weatherforcast.data.entity.ApiObj

class LocalDataSource {
    lateinit var apiObjDao: ApiObjDao
    constructor (application: Application) {
        apiObjDao = ApiObjDataBase.getDatabase(application).apiObjDao()
    }

    fun getAll(): LiveData<List<ApiObj>> {
        return apiObjDao.getAllApiObj()
    }

    suspend fun insert(apiObj: ApiObj) {
        apiObjDao.insert(apiObj)
    }

    suspend fun delete(apiObj: ApiObj) {
        apiObjDao.delete(apiObj)
    }

     suspend fun deleteApiObj(timeZone: String) {
        apiObjDao.deleteApiObj(timeZone)
    }

     fun getApiObj(timeZone:String) :ApiObj {
        return apiObjDao.getApiObj(timeZone)
    }

}