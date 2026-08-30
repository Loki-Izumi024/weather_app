package com.example.weather_app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app.api.WeatherApiService
import com.example.weather_app.api.WeatherResponse
import com.example.weather_app.databinding.ActivityMainBinding
import com.example.weather_app.db.WeatherDatabase
import com.example.weather_app.db.WeatherEntity
import com.example.weather_app.ui.WeatherAdapter
import com.example.weather_app.utils.LocationHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The main screen of the application.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationHelper: LocationHelper
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var db: WeatherDatabase
    private val apiService = WeatherApiService.create()

    // Replace with your actual OpenWeatherMap API Key!
    private val API_KEY = "YOUR_API_KEY_HERE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationHelper = LocationHelper(this)
        db = WeatherDatabase.getDatabase(this)

        setupRecyclerView()
        checkPermissionsAndFetchWeather()

        binding.refreshButton.setOnClickListener {
            checkPermissionsAndFetchWeather()
        }

        binding.saveButton.setOnClickListener {
            saveCurrentWeather()
        }

        binding.shareButton.setOnClickListener {
            shareWeatherInfo()
        }

        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        loadSavedRecords()
    }

    private fun setupRecyclerView() {
        weatherAdapter = WeatherAdapter(
            emptyList(),
            onUpdateClick = { record -> updateRecord(record) },
            onDeleteClick = { record -> deleteRecord(record) }
        )
        binding.recordsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recordsRecyclerView.adapter = weatherAdapter
    }

    private fun checkPermissionsAndFetchWeather() {
        if (locationHelper.hasLocationPermission()) {
            fetchLocationAndWeather()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchLocationAndWeather()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchLocationAndWeather() {
        locationHelper.getLastLocation().addOnSuccessListener { location ->
            if (location != null) {
                fetchWeather(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        lifecycleScope.launch {
            try {
                // Determine unit preference
                val sharedPref = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
                val isCelsius = sharedPref.getBoolean("is_celsius", true)
                val unit = if (isCelsius) "metric" else "imperial"

                val response = withContext(Dispatchers.IO) {
                    apiService.getCurrentWeather(lat, lon, API_KEY, unit)
                }
                updateUI(response, isCelsius)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Failed to fetch weather: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUI(weather: WeatherResponse, isCelsius: Boolean) {
        val unitLabel = if (isCelsius) "°C" else "°F"
        binding.currentCity.text = weather.cityName
        binding.currentTemp.text = "${weather.main.temp.toInt()}$unitLabel"
        binding.currentCondition.text = weather.weather[0].description.replaceFirstChar { it.uppercase() }
        binding.currentHumidity.text = "Humidity: ${weather.main.humidity}%"
        binding.currentWind.text = "Wind: ${weather.wind.speed}m/s"
    }

    private fun saveCurrentWeather() {
        val record = WeatherEntity(
            cityName = binding.currentCity.text.toString(),
            temperature = binding.currentTemp.text.toString(),
            condition = binding.currentCondition.text.toString(),
            humidity = binding.currentHumidity.text.toString().filter { it.isDigit() },
            windSpeed = binding.currentWind.text.toString().filter { it.isDigit() || it == '.' }
        )

        lifecycleScope.launch(Dispatchers.IO) {
            db.weatherDao().insertWeather(record)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Record Saved!", Toast.LENGTH_SHORT).show()
                loadSavedRecords()
            }
        }
    }

    private fun loadSavedRecords() {
        lifecycleScope.launch(Dispatchers.IO) {
            val records = db.weatherDao().getAllWeatherRecords()
            withContext(Dispatchers.Main) {
                weatherAdapter.updateData(records)
            }
        }
    }

    private fun deleteRecord(record: WeatherEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.weatherDao().deleteWeather(record)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Record Deleted", Toast.LENGTH_SHORT).show()
                loadSavedRecords()
            }
        }
    }

    private fun updateRecord(record: WeatherEntity) {
        lifecycleScope.launch {
            try {
                // To update, we fetch current data for this city again
                // Note: OpenWeatherMap also supports fetching by city name
                // For simplicity, we'll just show a toast that it's "updating" 
                // and fetch new data if we had lat/lon saved. 
                // Since we only saved city name and stats, we'll just refresh current location weather 
                // or assume we want to refresh the saved stats.
                
                Toast.makeText(this@MainActivity, "Updating ${record.cityName}...", Toast.LENGTH_SHORT).show()
                // In a real app, you'd call the API with the city name here.
                // For this beginner version, let's just re-save current weather over it.
                saveCurrentWeather() 
                deleteRecord(record) // Remove old version
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareWeatherInfo() {
        val shareText = "Current Weather in ${binding.currentCity.text}: " +
                "${binding.currentTemp.text}, ${binding.currentCondition.text}. " +
                "${binding.currentHumidity.text}, ${binding.currentWind.text}."

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Share weather via"))
    }
}
