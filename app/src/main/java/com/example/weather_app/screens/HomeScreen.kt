package com.example.weather_app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.sharp.ArrowDownward
import androidx.compose.material.icons.sharp.ArrowUpward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.weather_app.modules.BaseModel
import com.example.weather_app.ui.theme.russoFont
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    navController: NavController, viewModel: HomeViewModel = viewModel()
) {
    val locations by viewModel.locations.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val currentLocationDailyWeather by viewModel.currentLocationDailyWeather.collectAsState()
    val currentLocationHourlyWeather by viewModel.currentLocationHourlyWeather.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val (city, setCity) = remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        viewModel.getCurrentLocation()
        viewModel.getCurrentLocationDailyWeather()
        viewModel.getCurrentLocationHourlyWeather()
    }

    LaunchedEffect(city) {
        delay(500)
        if (city.isNotEmpty()) {
            viewModel.searchLocation(city)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to weather app.",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        IconButton (
            onClick = { viewModel.refreshAllData() },
            modifier = Modifier.size(48.dp)
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.White
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            TextField(modifier = Modifier.fillMaxWidth(), value = city, onValueChange = {
                setCity(it)
            }, colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,

                ), placeholder = {
                Text("Search for Specific City")
            })
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(
            visible = locations is BaseModel.Success,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Column {
                Text(text = "Choose your city:", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                when (val data = locations) {
                    is BaseModel.Success -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(data.data) { location ->
                                Row(modifier = Modifier
                                    .fillMaxWidth()
//                                    .height(50.dp)
                                    .clip(
                                        RoundedCornerShape(8.dp)
                                    )
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .clickable {
                                        navController.navigate("weather/${location.key}/${location.englishName}/${location.country.englishName}")
                                    }
                                    .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(
                                            location.englishName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            location.country.englishName,
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )

                                    }
                                }
                            }
                        }
                    }

                    else -> {}

                }
            }
        }
        AnimatedVisibility(
            visible = locations is BaseModel.Loading,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(visible = currentLocation is BaseModel.Success) {
            val data = currentLocation as BaseModel.Success
            val location = data.data
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = location.englishName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(text = location.country.englishName, color = Color.Gray)
            }
        }

        AnimatedVisibility(visible = currentLocationHourlyWeather is BaseModel.Success) {
            val data = currentLocationHourlyWeather as BaseModel.Success
            val temp = data.data.first()
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${temp.temperature.value}°",
                    fontWeight = FontWeight.Bold,
                    fontSize = 80.sp,
                    color = Color.White,
                    fontFamily = russoFont
                )
                Text(
                    text = temp.iconPhrase,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White,
                    fontFamily = russoFont
                )
            }
        }
        AnimatedVisibility(visible = currentLocationHourlyWeather is BaseModel.Loading) {
            Loading()
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Hourly Forecasts:",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedVisibility(visible = currentLocationHourlyWeather is BaseModel.Success) {
            val data = currentLocationHourlyWeather as BaseModel.Success
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(data.data) { forecast ->
                    Column(
                        modifier = Modifier
                            .size(100.dp, 140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = SimpleDateFormat("H a").format(Date(forecast.epochDateTime * 1000)),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            modifier = Modifier.size(70.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://developer.accuweather.com/sites/default/files/${forecast.weatherIcon.fixIcon()}-s.png")
                                .build(),
                            contentScale = ContentScale.Fit,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = forecast.temperature.value.toString() + "°",
                            color = Color.White
                        )

                    }
                }
            }
        }
        AnimatedVisibility(visible = currentLocationHourlyWeather is BaseModel.Loading) {
            Loading()
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Daily Forecasts:",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedVisibility(
            visible = locations is BaseModel.Success,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Column {
                Text(text = "Choose your city:", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                when (val data = locations) {
                    is BaseModel.Success -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(data.data) { location ->
                                Row(modifier = Modifier
                                    .fillMaxWidth()
//                                    .height(50.dp)
                                    .clip(
                                        RoundedCornerShape(8.dp)
                                    )
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .clickable {
                                        navController.navigate("weather/${location.key}/${location.englishName}/${location.country.englishName}")
                                    }
                                    .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(
                                            location.englishName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            location.country.englishName,
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
        AnimatedVisibility(
            visible = locations is BaseModel.Loading,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedVisibility(visible = currentLocationDailyWeather is BaseModel.Success) {
            val data = currentLocationDailyWeather as BaseModel.Success
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(data.data.dailyForecasts) { forecast ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(
                                RoundedCornerShape(
                                    8.dp
                                )
                            )
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(start = 16.dp, end = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${SimpleDateFormat("d").format(Date(forecast.epochDate * 1000))}th",
                            color = Color.White
                        )
                        Row {
                            Icon(
                                Icons.Sharp.ArrowDownward,
                                tint = Color(0xffff5353),
                                contentDescription = null
                            )
                            Text(text = "${forecast.temperature.min.value}°", color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Icon(
                                Icons.Sharp.ArrowUpward,
                                tint = Color(0xff2eff8c),
                                contentDescription = null
                            )
                            Text(text = "${forecast.temperature.max.value}°", color = Color.White)
                        }
                        AsyncImage(
                            modifier = Modifier.size(70.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://developer.accuweather.com/sites/default/files/${forecast.day.icon.fixIcon()}-s.png")
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
        AnimatedVisibility(visible = currentLocationDailyWeather is BaseModel.Loading) {
            Loading()
        }
    }
}