package com.example.weatherapp.model

data class WeatherResponse(
    val name: String,
    val main: MainData,
    val weather: List<WeatherData>,
    val wind: WindData
)

data class MainData(
    val temp: Double,
    val humidity: Int
)

data class WeatherData(
    val main: String,
    val description: String
)

data class WindData(
    val speed: Double
)

data class WeatherRecord(
    val id: Long = 0,
    val location: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val timestamp: Long
)