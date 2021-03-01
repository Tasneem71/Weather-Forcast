package com.example.weatherforcast.data.retro

import com.example.weatherforcast.data.entity.ApiObj
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
const val API_KEY = "657a3a141a21f4b9151316c7a77c0d5e"
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