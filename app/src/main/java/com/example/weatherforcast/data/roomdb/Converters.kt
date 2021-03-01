package com.example.weatherforcast.data.roomdb

import android.util.Log
import androidx.room.TypeConverter
import com.example.weatherforcast.data.entity.Alerts
import com.example.weatherforcast.data.entity.Daily
import com.example.weatherforcast.data.entity.Hourly
import com.example.weatherforcast.data.entity.Weather
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun  listHourlyToJson (value:List<Hourly>) = Gson().toJson(value)
    @TypeConverter
    fun  listDailyToJson (value:List<Daily>) = Gson().toJson(value)
    @TypeConverter
    fun  listAlertToJson (value:List<Alerts>?) = Gson().toJson(value)
    @TypeConverter
    fun  listWeatherToJson (value: List<Weather>) = Gson().toJson(value)
    @TypeConverter
    fun jsonToWeatherList(value: String) = Gson().fromJson(value, Array<Weather>::class.java).toList()
    @TypeConverter
    fun jsonToHourlyList(value: String) = Gson().fromJson(value, Array<Hourly>::class.java).toList()
    @TypeConverter
    fun jsonToDailyList(value: String) = Gson().fromJson(value, Array<Daily>::class.java).toList()

    @TypeConverter
    fun jsonToAlertList(value: String?): List<Alerts>? {
        value?.let {
            return Gson().fromJson(value, Array<Alerts>::class.java)?.toList()
        }
            return emptyList()
    }
}