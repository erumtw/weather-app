package com.example.weather_app.repositories

import com.example.weather_app.modules.BaseModel
import com.example.weather_app.modules.CurrentLocation
import com.example.weather_app.modules.DailyForecasts
import com.example.weather_app.modules.HourlyForecast
import com.example.weather_app.modules.Location
import com.example.weather_app.network.Api
import retrofit2.Response

class WeatherRepoImpl(private val api: Api) : WeatherRepo {
    override suspend fun searchLocation(query: String): BaseModel<List<Location>> {
        return request {
            api.searchLocation(query = query)
        }
    }

    override suspend fun getDailyForecasts(locationKey: String): BaseModel<DailyForecasts> {
        return request {
            api.getDailyForecasts(locationKey = locationKey)
        }
    }

    override suspend fun getHourlyForecasts(locationKey: String): BaseModel<List<HourlyForecast>> {
        return request {
            api.getHourlyForecasts(locationKey = locationKey)
        }
    }

    override suspend fun getCurrentLocation(
        latitude: Double,
        longitude: Double
    ): BaseModel<CurrentLocation> {
        return request {
            val coordinates = "$latitude,$longitude"
            api.getLocationByCoordinates(coordinates = coordinates)
        }
    }

    override suspend fun getCurrentLocationDailyForecasts(
        latitude: Double,
        longitude: Double
    ): BaseModel<DailyForecasts> {
        return request {
            val coordinates = "$latitude,$longitude"
            val locationResponse = api.getLocationByCoordinates(coordinates = coordinates)
            if (locationResponse.isSuccessful) {
                val currentPosition = locationResponse.body()!!
                api.getDailyForecasts(locationKey = currentPosition.key)
            } else {
                throw Exception("Failed to get location")
            }
        }
    }

    override suspend fun getCurrentLocationHourlyForecasts(
        latitude: Double,
        longitude: Double
    ): BaseModel<List<HourlyForecast>> {
        return request {
            val coordinates = "$latitude,$longitude"
            val locationResponse = api.getLocationByCoordinates(coordinates = coordinates)
            if (locationResponse.isSuccessful) {
                val currentPosition = locationResponse.body()!!
                api.getHourlyForecasts(locationKey = currentPosition.key)
            } else {
                throw Exception("Failed to get location")
            }
        }
    }
}

suspend fun <T> request(request: suspend () -> Response<T>): BaseModel<T> {
    try {
        request().also {
            return if (it.isSuccessful) {
                BaseModel.Success(it.body()!!)
            } else {
                BaseModel.Error(it.errorBody()?.string().toString())
            }
        }
    } catch (e: Exception) {
        return BaseModel.Error(e.message.toString())
    }
}