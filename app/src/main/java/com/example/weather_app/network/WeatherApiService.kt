package com.example.weatherapp.util

import android.content.Context

class PreferencesManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "weather_preferences",
        Context.MODE_PRIVATE
    )

    var unit: String
        get() = preferences.getString("unit", "metric") ?: "metric"
        set(value) {
            preferences.edit()
                .putString("unit", value)
                .apply()
        }
}