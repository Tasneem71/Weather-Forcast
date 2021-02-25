package com.example.weatherforcast.data.retro

import com.example.weatherforcast.data.entity.ApiObj
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherServes {

    private const val BASE_URL = "https://api.openweathermap.org/"
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val apiService: WeatherApi = getRetrofit().create(WeatherApi::class.java)


}