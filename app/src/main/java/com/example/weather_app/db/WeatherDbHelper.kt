package com.example.weatherapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.weatherapp.model.WeatherRecord

class WeatherDbHelper(context: Context) :
    SQLiteOpenHelper(context, "weather.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE weather (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                location TEXT NOT NULL,
                temperature REAL NOT NULL,
                condition TEXT NOT NULL,
                humidity INTEGER NOT NULL,
                wind_speed REAL NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS weather")
        onCreate(db)
    }

    fun insert(record: WeatherRecord): Long {

        val values = ContentValues().apply {
            put("location", record.location)
            put("temperature", record.temperature)
            put("condition", record.condition)
            put("humidity", record.humidity)
            put("wind_speed", record.windSpeed)
            put("timestamp", record.timestamp)
        }

        return writableDatabase.insert(
            "weather",
            null,
            values
        )
    }

    fun getAll(): MutableList<WeatherRecord> {

        val list = mutableListOf<WeatherRecord>()

        readableDatabase.query(
            "weather",
            null,
            null,
            null,
            null,
            null,
            "timestamp DESC"
        ).use { cursor ->

            while (cursor.moveToNext()) {

                list.add(
                    WeatherRecord(
                        id = cursor.getLong(
                            cursor.getColumnIndexOrThrow("id")
                        ),
                        location = cursor.getString(
                            cursor.getColumnIndexOrThrow("location")
                        ),
                        temperature = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("temperature")
                        ),
                        condition = cursor.getString(
                            cursor.getColumnIndexOrThrow("condition")
                        ),
                        humidity = cursor.getInt(
                            cursor.getColumnIndexOrThrow("humidity")
                        ),
                        windSpeed = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("wind_speed")
                        ),
                        timestamp = cursor.getLong(
                            cursor.getColumnIndexOrThrow("timestamp")
                        )
                    )
                )
            }
        }

        return list
    }

    fun update(record: WeatherRecord): Int {

        val values = ContentValues().apply {
            put("location", record.location)
            put("temperature", record.temperature)
            put("condition", record.condition)
            put("humidity", record.humidity)
            put("wind_speed", record.windSpeed)
            put("timestamp", record.timestamp)
        }

        return writableDatabase.update(
            "weather",
            values,
            "id=?",
            arrayOf(record.id.toString())
        )
    }

    fun delete(id: Long): Int {

        return writableDatabase.delete(
            "weather",
            "id=?",
            arrayOf(id.toString())
        )
    }
}