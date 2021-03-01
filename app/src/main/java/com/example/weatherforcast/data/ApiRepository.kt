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
    var weatherObj=MutableLiveData<ApiObj>()

    constructor(application: Application) {
        localDataSource = LocalDataSource(application)
        weatherService = WeatherServes
    }


    fun fetchWeatherData(context: Context, lat: Double, lon: Double, lang: String, unit: String): LiveData<List<ApiObj>> {
        //  loadingLiveData.postValue(true)
        /* val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
            errorLiveData.value = true
            loadingLiveData.postValue(false)
        }*/
        if (isOnline(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = weatherService.apiService.getCurrentWeatherByLatLng(lat, lon, lang, unit)
                try {
                    if (response.isSuccessful) {
                        response.body()?.let { localDataSource.insert(it) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return localDataSource.getAll()
    }

    fun fetchWeatherObj(context: Context, lat: Double, lon: Double, lang: String, unit: String){
        // loadingLiveData.postValue(true)
        /* val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
            errorLiveData.value = true
            loadingLiveData.postValue(false)
        }*/
        if (isOnline(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = weatherService.apiService.getCurrentWeatherByLatLng(lat, lon, lang, unit)
                try {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            localDataSource.insert(it)
                            weatherObj.postValue(it)
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
    fun UpdateWeatherData(lang: String, unit: String){
        /* val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
            errorLiveData.value = true
            loadingLiveData.postValue(false)
        }*/
        CoroutineScope(Dispatchers.IO).launch {
            var weatherData=localDataSource.getAllList()
            for (item in weatherData) {
                val response =
                        weatherService.apiService.getCurrentWeatherByLatLng(item.lat, item.lon, lang, unit)
                try {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            localDataSource.insert(it)
                            Log.i("ola","kkjhhf")
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
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