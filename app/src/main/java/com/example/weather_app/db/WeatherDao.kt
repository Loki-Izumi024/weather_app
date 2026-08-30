package com.example.weather_app.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * DAO stands for Data Access Object. 
 * This interface defines the operations we can perform on the weather_records table.
 */
@Dao
interface WeatherDao {

    // Adds a new weather record to the database
    @Insert
    suspend fun insertWeather(weather: WeatherEntity)

    // Retrieves all saved weather records, ordered by the newest first
    @Query("SELECT * FROM weather_records ORDER BY timestamp DESC")
    suspend fun getAllWeatherRecords(): List<WeatherEntity>

    // Updates an existing record (e.g., if the user wants to change something)
    @Update
    suspend fun updateWeather(weather: WeatherEntity)

    // Deletes a specific record from the database
    @Delete
    suspend fun deleteWeather(weather: WeatherEntity)
}
