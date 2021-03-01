package com.example.weatherforcast

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherforcast.data.ApiRepository
import com.example.weatherforcast.data.retro.SettingsEnum


class WorkerApi(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    var weatherRepository : ApiRepository = ApiRepository(appContext.applicationContext as Application)
    override fun doWork(): Result {
        // Do the work here--in this case, upload the images.
        weatherRepository.UpdateWeatherData(SettingsEnum.ENGLISH.Value, SettingsEnum.IMPERIAL.Value)
        Log.i("tasneem","llllss")
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

}