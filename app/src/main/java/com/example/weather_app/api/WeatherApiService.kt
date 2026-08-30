package com.example.weather_app.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * This interface defines how we talk to the OpenWeatherMap API.
 */
interface WeatherApiService {

    /**
     * Gets the current weather for a specific latitude and longitude.
     * @Query adds parameters to the URL (e.g., ?lat=...&lon=...&appid=...)
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" // Default to Celsius
    ): WeatherResponse

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        /**
         * Creates and returns a Retrofit instance for networking.
         */
        fun create(): WeatherApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Converts JSON to Kotlin objects
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}
