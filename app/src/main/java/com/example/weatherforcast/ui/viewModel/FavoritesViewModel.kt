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
import java.text.SimpleDateFormat
import java.util.*

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

     fun timeFormat(millisSeconds: Int): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }
     fun dateFormat(milliSeconds: Int):String{
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds.toLong() * 1000)
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        var day=calendar.get(Calendar.DAY_OF_MONTH).toString()
        var year=calendar.get(Calendar.YEAR).toString()
        return day+month// +year

    }

}