package com.example.weather_app.network

import com.example.weather_app.modules.DailyForecasts
import com.example.weather_app.modules.HourlyForecast
import com.example.weather_app.modules.Location
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val APIKEY = "lA0fA1HUqGwQ8mwmkMuPy3LIbL8KMgUr"

interface Api {
    @GET("locations/v1/cities/search")
    suspend fun searchLocation(
        @Query("apikey") apiKey: String = APIKEY,
        @Query("q") query: String
    ): Response<List<Location>>

    @GET("forecasts/v1/daily/5day/{location_key}")
    suspend fun getDailyForecasts(
        @Path("location_key") locationKey: String,
        @Query("apikey") apiKey: String = APIKEY,
        @Query("metric") metric: Boolean = true
    ): Response<DailyForecasts>

    @GET("forecasts/v1/hourly/12hour/{location_key}")
    suspend fun getHourlyForecasts(
        @Path("location_key") locationKey: String,
        @Query("apikey") apiKey: String = APIKEY,
        @Query("metric") metric: Boolean = true
    ): Response<List<HourlyForecast>>

//    @GET("locations/v1/cities/geoposition/search")
//    suspend fun getLocationByCoordinates(
//        @Query("apikey") apiKey: String,
//        @Query("q") coordinates: String
//    ): Response<Geoposition>
}

