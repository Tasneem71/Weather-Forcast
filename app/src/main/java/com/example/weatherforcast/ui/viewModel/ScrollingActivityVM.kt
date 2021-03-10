package com.example.weatherforcast.ui.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforcast.R
import com.example.weatherforcast.data.ApiRepository
import com.example.weatherforcast.data.entity.ApiObj
import java.text.SimpleDateFormat
import java.util.*


class ScrollingActivityVM (application: Application) : AndroidViewModel(application) {

    val apiObj= MutableLiveData<ApiObj>();
    var apiRepository:ApiRepository

    init{
        apiRepository = ApiRepository(application)
    }

    public fun getApiObjFromRoom(timeZone:String): ApiObj{
        return apiRepository.getApiObjFromRoom(timeZone)
    }


    public fun loadWeatherObj(context: Context,lat:Double,lon:Double,lang:String,unit:String) : LiveData<ApiObj>{
        apiRepository.fetchWeatherObj(context,lat,lon,lang,unit)
        return apiRepository.weatherObj
    }

    public fun updateAllData(context: Context,lang: String, unit: String){
        apiRepository.UpdateWeatherData(lang,unit,context)

    }

 fun dateFormat(milliSeconds: Int):String{
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds.toLong() * 1000)
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        var day=calendar.get(Calendar.DAY_OF_MONTH).toString()
        var year=calendar.get(Calendar.YEAR).toString()
        return day+"/"+month// +"/"+year

    }

  fun timeFormat(millisSeconds:Int ): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }

    fun backgroundBasedOnTime(root: View, activity: Activity){
        val drawbleDark =  activity!!.resources.getDrawable(R.drawable.background_night_bg,activity!!.theme)
        val drawbleLight =  activity!!.resources.getDrawable(R.drawable.background_bg,activity!!.theme)
        val sdk = android.os.Build.VERSION.SDK_INT;
        val cal = Calendar.getInstance() //Create Calendar-Object

        cal.time = Date() //Set the Calendar to now

        val hour = cal[Calendar.HOUR_OF_DAY] //Get the hour from the calendar
        Log.i("back","hour"+hour)
        if (hour <= 17 && hour >= 5) // Check if hour is between 8 am and 11pm
        {
            Log.i("back","hour2"+hour)
                Log.i("back","light")
                root.background=drawbleLight


        }else{

                Log.i("back","Dark")
                root.background=drawbleDark
            val window: Window = activity.getWindow()
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getColor(activity,R.color.darkstatus))

        }

    }

}
