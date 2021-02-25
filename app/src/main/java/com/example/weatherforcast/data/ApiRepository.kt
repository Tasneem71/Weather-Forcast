package com.example.weatherforcast.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforcast.data.entity.ApiObj
import com.example.weatherforcast.data.retro.WeatherServes
import com.example.weatherforcast.data.roomdb.LocalDataSource
import kotlinx.coroutines.*

class ApiRepository {
    lateinit var localDataSource: LocalDataSource
    lateinit var weatherService: WeatherServes
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<Boolean>()
    val WeatherLiveData = MutableLiveData<ApiObj>()

    constructor(application: Application) {
        localDataSource = LocalDataSource(application)
        weatherService = WeatherServes
    }


    fun fetchWeatherData(context: Context,lat:Double,lon:Double) : LiveData<List<ApiObj>> {
        if(isOnline(context)){
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, exeption ->exeption.printStackTrace()
            Log.i("log","exption")
            errorLiveData.value = true
            loadingLiveData.postValue(false)
        }
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            val response = WeatherServes.apiService.getCurrentWeatherByLatLng(lat,lon)
            if (response.isSuccessful) {
                response.body()?.let { localDataSource.insert(it) }

            }
        }}
        return  localDataSource.getAll()
    }



    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun getWeatherData(): LiveData<List<ApiObj>> {
        return  localDataSource.getAll()
    }


}