package com.example.weatherforcast.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforcast.data.ApiRepository
import com.example.weatherforcast.data.entity.ApiObj
import com.example.weatherforcast.data.roomdb.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    val apiObj= MutableLiveData<ApiObj>();
    var apiRepository: ApiRepository
    lateinit var localDataSource :LocalDataSource
    val navegate = MutableLiveData<String>()
    val displayListener = MutableLiveData<ApiObj>()

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

    public fun deleteApiObj(timeZone: String)  {

        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.deleteApiObj(timeZone)
        }
    }

    fun getNavigate(): LiveData<String> {
        return navegate
    }

    public fun onRemoveClick(timeZone:String)  {

        navegate.value=timeZone

    }

    public fun getWeatherList() : LiveData<List<ApiObj>> {
        return apiRepository.getWeatherData()
    }

    fun showObj(): LiveData<ApiObj> {
        return displayListener
    }

    public fun onShowClick(weatherObj:ApiObj){
        displayListener.value=weatherObj
    }
}