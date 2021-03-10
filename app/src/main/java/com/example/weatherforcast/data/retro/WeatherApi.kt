package com.example.weatherforcast.data.retro

import com.example.weatherforcast.data.entity.ApiObj
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
const val API_KEY = "fe73bba9347d5f20956405171ad80715"
interface WeatherApi {
    @GET("data/2.5/onecall")
    suspend fun getCurrentWeatherByLatLng(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appid: String = API_KEY
    ): Response<ApiObj>
}