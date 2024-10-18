package com.example.weather_app.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.modules.BaseModel
import com.example.weather_app.modules.DailyForecasts
import com.example.weather_app.modules.HourlyForecast
import com.example.weather_app.repositories.WeatherRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WeatherViewModel : ViewModel(), KoinComponent {
    private val repo: WeatherRepo by inject()

    private val _hourlyForecast: MutableStateFlow<BaseModel<List<HourlyForecast>>> =
        MutableStateFlow(BaseModel.Loading)
    val hourlyForecast = _hourlyForecast.asStateFlow()

    private val _dailyForecast: MutableStateFlow<BaseModel<DailyForecasts>> =
        MutableStateFlow(BaseModel.Loading)
    val dailyForecast = _dailyForecast.asStateFlow()

    private val _currentLocationDailyForecast: MutableStateFlow<BaseModel<DailyForecasts>> =
        MutableStateFlow(BaseModel.Loading)
    val currentLocationDailyForecast = _currentLocationDailyForecast.asStateFlow()

    private val _currentLocationHourlyForecast: MutableStateFlow<BaseModel<List<HourlyForecast>>> =
        MutableStateFlow(BaseModel.Loading)
    val currentLocationHourlyForecast = _currentLocationHourlyForecast.asStateFlow()

    fun getCurrentLocationDailyWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repo.getCurrentLocationDailyForecasts(latitude, longitude).also { data ->
                _currentLocationDailyForecast.update { data }
            }
        }
    }

    fun getCurrentLocationHourlyWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repo.getCurrentLocationHourlyForecasts(latitude, longitude).also { data ->
                _currentLocationHourlyForecast.update { data }
            }
        }
    }

    fun getHourlyForecast(locationKey: String) {
        viewModelScope.launch {
            repo.getHourlyForecasts(locationKey).also { data ->
                _hourlyForecast.update { data }
            }
        }
    }

    fun getDailyForecast(locationKey: String) {
        viewModelScope.launch {
            repo.getDailyForecasts(locationKey).also { data ->
                _dailyForecast.update { data }
            }
        }
    }
}