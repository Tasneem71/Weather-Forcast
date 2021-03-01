package com.example.weatherforcast.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforcast.data.ApiRepository
import com.example.weatherforcast.data.entity.AlarmObj
import com.example.weatherforcast.data.entity.ApiObj
import com.example.weatherforcast.data.roomdb.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlartViewModel (application: Application) : AndroidViewModel(application) {

    lateinit var localDataSource : LocalDataSource
    val navegate = MutableLiveData<AlarmObj>()
    val displayListener = MutableLiveData<AlarmObj>()

    init{
        localDataSource = LocalDataSource(application)
    }


    public fun deleteAlarmObj(id: Int)  {

        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.deleteAlarmObj(id)
        }
    }

    public suspend fun insertAlarmObj(alarmObj: AlarmObj):Long  {

           return localDataSource.insertAlarm(alarmObj)

    }


    public fun onEditClick(alarmObj: AlarmObj)  {

        navegate.value=alarmObj

    }

    public fun getAlarmList() : LiveData<List<AlarmObj>> {
        return localDataSource.getAllAlarmObj()
    }


    fun getNavigate(): LiveData<AlarmObj> {
        return navegate
    }
}