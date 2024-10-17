package com.example.weather_app.repositories

import com.example.weather_app.modules.BaseModel
import com.example.weather_app.modules.DailyForecasts
import com.example.weather_app.modules.HourlyForecast
import com.example.weather_app.modules.Location


interface WeatherRepo {
    suspend fun searchLocation(query:String):BaseModel<List<Location>>
    suspend fun getDailyForecasts(locationKey:String):BaseModel<DailyForecasts>
    suspend fun getHourlyForecasts(locationKey: String):BaseModel<List<HourlyForecast>>
}