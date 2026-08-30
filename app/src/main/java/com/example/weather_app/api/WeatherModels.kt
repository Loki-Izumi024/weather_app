package com.example.weather_app.api

import com.google.gson.annotations.SerializedName

/**
 * These classes represent the JSON response we get from the OpenWeatherMap API.
 * The @SerializedName annotation maps the JSON key to our Kotlin variable name.
 */
data class WeatherResponse(
    @SerializedName("name") val cityName: String,
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("wind") val wind: Wind
)

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("humidity") val humidity: Int
)

data class Weather(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class Wind(
    @SerializedName("speed") val speed: Double
)
