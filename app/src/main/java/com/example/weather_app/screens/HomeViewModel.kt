package com.example.weather_app.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.modules.BaseModel
import com.example.weather_app.modules.CurrentLocation
import com.example.weather_app.modules.DailyForecasts
import com.example.weather_app.modules.HourlyForecast
import com.example.weather_app.modules.Location
import com.example.weather_app.network.LocationService
import com.example.weather_app.repositories.WeatherRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class HomeViewModel : ViewModel(), KoinComponent {
    private val repo: WeatherRepo by inject()
    private val locationService: LocationService by inject()

    private val _locations: MutableStateFlow<BaseModel<List<Location>>?> = MutableStateFlow(null)
    val locations = _locations.asStateFlow()

    private val _currentLocation =
        MutableStateFlow<BaseModel<CurrentLocation>>(BaseModel.Loading)
    val currentLocation = _currentLocation.asStateFlow()

    private val _currentLocationDailyWeather =
        MutableStateFlow<BaseModel<DailyForecasts>>(BaseModel.Loading)
    val currentLocationDailyWeather = _currentLocationDailyWeather.asStateFlow()

    private val _currentLocationHourlyWeather =
        MutableStateFlow<BaseModel<List<HourlyForecast>>>(BaseModel.Loading)
    val currentLocationHourlyWeather = _currentLocationHourlyWeather.asStateFlow()

    private var lastFetchTime: Long = 0
    private val CACHE_DURATION = TimeUnit.MINUTES.toMillis(15) // 15 minutes cache
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshAllData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            getCurrentLocation(true)
            getCurrentLocationDailyWeather(true)
            getCurrentLocationHourlyWeather(true)
            _isRefreshing.value = false
        }
    }

    private fun isCacheExpired(): Boolean {
        return System.currentTimeMillis() - lastFetchTime > CACHE_DURATION
    }

    fun searchLocation(query: String) {
        viewModelScope.launch {
            _locations.update { BaseModel.Loading }
            repo.searchLocation(query).also { data ->
                _locations.update { data }
            }
        }
    }

    fun getCurrentLocation(forceRefresh: Boolean = false) {
        if (!forceRefresh && !isCacheExpired() && _currentLocation.value is BaseModel.Success) {
            return
        }

        viewModelScope.launch {
            _currentLocation.value = BaseModel.Loading
            val location = locationService.getCurrentLocation()
            if (location != null) {
                _currentLocation.value =
                    repo.getCurrentLocation(location.latitude, location.longitude)
                lastFetchTime = System.currentTimeMillis()
            } else {
                _currentLocation.value =
                    BaseModel.Error("Unable to get current location. Please check your location settings and permissions.")
            }
        }
    }

    fun getCurrentLocationDailyWeather(forceRefresh: Boolean = false) {
        if (!forceRefresh && !isCacheExpired() && _currentLocationDailyWeather.value is BaseModel.Success) {
            return
        }

        viewModelScope.launch {
            _currentLocationDailyWeather.value = BaseModel.Loading
            val location = locationService.getCurrentLocation()
            if (location != null) {
                _currentLocationDailyWeather.value =
                    repo.getCurrentLocationDailyForecasts(location.latitude, location.longitude)
                lastFetchTime = System.currentTimeMillis()
            } else {
                _currentLocationDailyWeather.value =
                    BaseModel.Error("Unable to get current location. Please check your location settings and permissions.")
            }
        }
    }

    fun getCurrentLocationHourlyWeather(forceRefresh: Boolean = false) {
        if (!forceRefresh && !isCacheExpired() && _currentLocationHourlyWeather.value is BaseModel.Success) {
            return
        }

        viewModelScope.launch {
            _currentLocationHourlyWeather.value = BaseModel.Loading
            val location = locationService.getCurrentLocation()
            if (location != null) {
                _currentLocationHourlyWeather.value =
                    repo.getCurrentLocationHourlyForecasts(location.latitude, location.longitude)
                lastFetchTime = System.currentTimeMillis()
            } else {
                _currentLocationHourlyWeather.value =
                    BaseModel.Error("Unable to get current location. Please check your location settings and permissions.")
            }
        }
    }


}