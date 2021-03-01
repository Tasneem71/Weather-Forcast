package com.example.weatherforcast.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforcast.data.ApiRepository
import com.example.weatherforcast.data.entity.ApiObj
import com.example.weatherforcast.data.retro.WeatherServes
import com.example.weatherforcast.data.roomdb.LocalDataSource

class ScrollingActivityVM (application: Application) : AndroidViewModel(application) {

    val apiObj= MutableLiveData<ApiObj>();
    var apiRepository:ApiRepository
    lateinit var localDataSource : LocalDataSource

    init{
        apiRepository = ApiRepository(application)
        localDataSource = LocalDataSource(application)
    }

    public fun loadWeather(context: Context, lat:Double, lon:Double,lang:String,unit:String) : LiveData<List<ApiObj>> {
        return apiRepository.fetchWeatherData(context,lat, lon,lang,unit)
    }

    public fun getApiObj() : LiveData<ApiObj> {
        return apiObj;
    }

    public fun getApiObjFromRoom(timeZone:String): ApiObj{
        return localDataSource.getApiObj(timeZone)
    }


    public fun loadWeatherObj(context: Context,lat:Double,lon:Double,lang:String,unit:String) : LiveData<ApiObj>{
        apiRepository.fetchWeatherObj(context,lat,lon,lang,unit)
        return apiRepository.weatherObj
    }
}



//public fun loadWeather() {
//    wetherServes.getWeatherService(ScrollingActivityVM.lat, ScrollingActivityVM.lon).enqueue(object : Callback<ApiObj> {
//        override fun onResponse(call: Call<ApiObj>, response: Response<ApiObj>) {
//            if (response.code() == 200) {
//                val weatherResponse = response.body()!!
//                task.postValue(weatherResponse)
//                Log.i("load","egergerg"+weatherResponse.toString())
//
//            }
//        }
//
//        override fun onFailure(call: Call<ApiObj>, t: Throwable) {
//            Log.i("error","egergerg")
//        }
//    })
//}