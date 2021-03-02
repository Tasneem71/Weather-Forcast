package com.example.weatherforcast

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherforcast.data.ApiRepository
import com.example.weatherforcast.data.retro.SettingsEnum


class WorkerApi(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    var weatherRepository : ApiRepository = ApiRepository(appContext.applicationContext as Application)
    var prefs= PreferenceManager.getDefaultSharedPreferences(applicationContext)
    override fun doWork(): Result {
        // Do the work here--in this case, upload the images.
        var unit=prefs.getString("UNIT_SYSTEM", SettingsEnum.IMPERIAL.Value).toString()
        var lang=prefs.getString("APP_LANG", SettingsEnum.ENGLISH.Value).toString()
        weatherRepository.UpdateWeatherData(unit,lang,applicationContext)
        Log.i("tasneem","llllss")
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

}