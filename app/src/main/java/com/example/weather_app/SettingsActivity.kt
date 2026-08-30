package com.example.weather_app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weather_app.databinding.ActivitySettingsBinding

/**
 * Allows the user to change preferences, like temperature units.
 * We use SharedPreferences to save these settings locally.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val isCelsius = sharedPref.getBoolean("is_celsius", true)

        // Set the radio buttons based on saved preference
        if (isCelsius) {
            binding.radioCelsius.isChecked = true
        } else {
            binding.radioFahrenheit.isChecked = true
        }

        binding.saveSettingsButton.setOnClickListener {
            val selectedIsCelsius = binding.radioCelsius.isChecked
            sharedPref.edit().putBoolean("is_celsius", selectedIsCelsius).apply()
            finish() // Close the settings screen
        }
    }
}
