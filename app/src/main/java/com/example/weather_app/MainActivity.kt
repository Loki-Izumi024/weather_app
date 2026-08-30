package com.example.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.ui.SavedRecordsFragment
import com.example.weatherapp.ui.SettingsFragment
import com.example.weatherapp.ui.WeatherFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            showWeather()
        }

        binding.bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_weather -> {
                    showWeather()
                    true
                }

                R.id.nav_saved -> {
                    showSaved()
                    true
                }

                R.id.nav_settings -> {
                    showSettings()
                    true
                }

                else -> false
            }
        }
    }

    private fun showWeather() {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                WeatherFragment()
            )
            .commit()
    }

    private fun showSaved() {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                SavedRecordsFragment()
            )
            .commit()
    }

    private fun showSettings() {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                SettingsFragment()
            )
            .commit()
    }
}