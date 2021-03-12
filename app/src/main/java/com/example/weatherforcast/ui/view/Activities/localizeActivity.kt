package com.example.weatherforcast.ui.view.Activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.weatherforcast.R
import com.example.weatherforcast.data.retro.SettingsEnum
import java.util.*

open class localizeActivity :  AppCompatActivity() {
    private lateinit var mCurrentLocale: Locale
    private var lang: String = ""
    private var isUpdated: Boolean = false
    override fun onStart() {
        super.onStart()
        mCurrentLocale = resources.configuration.locale
        Log.i("lang", "onStart")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCurrentLocale = resources.configuration.locale
        val locale: Locale = getLocale(this)
        if (!locale.equals(mCurrentLocale) || isUpdated) {
            // mCurrentLocale = locale
            setLocale()
            Log.i("lang", "onCreate")
        }
    }

    override fun onRestart() {
        super.onRestart()
        val locale: Locale = getLocale(this)
        if (!locale.equals(mCurrentLocale) || isUpdated) {
            // mCurrentLocale = locale
            setLocale()
            recreate()
            Log.i("lang", "onRestart")
        }
    }

    private fun setLocale() {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources: Resources = this.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())

    }

    fun getLocale(context: Context): Locale {
        val sharedPreferences: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
        lang = sharedPreferences.getString("APP_LANG", SettingsEnum.ENGLISH.Value).toString()
        isUpdated = sharedPreferences.getBoolean("isUpdated", false)
        Log.i("lang", lang + " " + isUpdated)
        return Locale(lang)
    }

    fun backgroundBasedOnTime(root: View, activity: Activity){
        val drawbleDark =  activity!!.resources.getDrawable(R.drawable.background_night_bg,activity!!.theme)
        val drawbleLight =  activity!!.resources.getDrawable(R.drawable.background_bg,activity!!.theme)
        val drawbleSunsit =  activity!!.resources.getDrawable(R.drawable.background_sunset_bg,activity!!.theme)
        val cal = Calendar.getInstance() //Create Calendar-Object

        cal.time = Date() //Set the Calendar to now

        val hour = cal[Calendar.HOUR_OF_DAY] //Get the hour from the calendar
        Log.i("back","hour"+hour)
        if (hour <= 16 && hour >= 7) // Check if hour is between 8 am and 11pm
        {
            Log.i("back","hour2"+hour)
            Log.i("back","light")
            root.background=drawbleLight
            val window: Window = activity.getWindow()
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorSecond))


        }else if(hour <= 5 && hour > 18){

            Log.i("back","Dark")
            root.background=drawbleDark
            val window: Window = activity.getWindow()
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.darkstatus))

        }
        else{

            root.background=drawbleSunsit
            val window: Window = activity.getWindow()
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.sunsetstatus))

        }

    }

}