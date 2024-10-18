package com.example.weather_app.modules

import com.google.gson.annotations.SerializedName

data class CurrentLocation(
    @SerializedName("Key") val key: String,
    @SerializedName("LocalizedName") val localizedName: String,
    @SerializedName("EnglishName") val englishName: String,
    @SerializedName("Country") val country: Country
)

data class Country(
    @SerializedName("ID") val id: String,
    @SerializedName("LocalizedName") val localizedName: String,
    @SerializedName("EnglishName") val englishName: String
)
