package com.example.weatherapp.ui

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weatherapp.LoginActivity
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.db.WeatherDbHelper
import com.example.weatherapp.model.WeatherRecord
import com.example.weatherapp.network.RetrofitClient
import com.example.weatherapp.util.LocationHelper
import com.example.weatherapp.util.PreferencesManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationHelper: LocationHelper
    private lateinit var preferences: PreferencesManager
    private lateinit var database: WeatherDbHelper

    private var weatherJob: Job? = null

    private val apiKey = "YOUR_OPENWEATHER_API_KEY"

    private var currentWeather: WeatherRecord? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWeatherBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        locationHelper =
            LocationHelper(requireActivity())

        preferences =
            PreferencesManager(requireContext())

        database =
            WeatherDbHelper(requireContext())

        binding.refreshButton.setOnClickListener {
            loadWeather()
        }

        binding.saveButton.setOnClickListener {
            saveWeather()
        }

        binding.shareButton.setOnClickListener {
            shareWeather()
        }

        binding.logoutButton.setOnClickListener {

            FirebaseAuth
                .getInstance()
                .signOut()

            startActivity(
                Intent(
                    requireContext(),
                    LoginActivity::class.java
                )
            )

            requireActivity().finish()
        }

        loadWeather()
    }

    private fun isInternetAvailable(): Boolean {

        val connectivityManager =
            requireContext()
                .getSystemService(
                    ConnectivityManager::class.java
                )

        val network =
            connectivityManager.activeNetwork
                ?: return false

        val capabilities =
            connectivityManager
                .getNetworkCapabilities(network)
                ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }

    private fun loadWeather() {

        if (!isInternetAvailable()) {

            Toast.makeText(
                requireContext(),
                "No internet connection",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (!locationHelper.hasPermission()) {

            locationHelper.requestPermission()
            return
        }

        binding.progressBar.visibility =
            View.VISIBLE

        locationHelper.getLocation(
            onSuccess = { latitude, longitude ->

                weatherJob?.cancel()

                weatherJob =
                    CoroutineScope(Dispatchers.IO).launch {

                        try {

                            val unit =
                                preferences.unit

                            val response =
                                RetrofitClient.api
                                    .getCurrentWeather(
                                        latitude,
                                        longitude,
                                        apiKey,
                                        unit
                                    )

                            val record =
                                WeatherRecord(
                                    location = response.name,
                                    temperature =
                                        response.main.temp,
                                    condition =
                                        response.weather
                                            .firstOrNull()
                                            ?.description
                                            ?: "Unknown",
                                    humidity =
                                        response.main.humidity,
                                    windSpeed =
                                        response.wind.speed,
                                    timestamp =
                                        System.currentTimeMillis()
                                )

                            withContext(
                                Dispatchers.Main
                            ) {

                                currentWeather =
                                    record

                                displayWeather(record)

                                binding.progressBar
                                    .visibility =
                                    View.GONE
                            }

                        } catch (e: Exception) {

                            withContext(
                                Dispatchers.Main
                            ) {

                                binding.progressBar
                                    .visibility =
                                    View.GONE

                                Toast.makeText(
                                    requireContext(),
                                    "Weather request failed: " +
                                            "${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
            },
            onError = {

                binding.progressBar.visibility =
                    View.GONE

                Toast.makeText(
                    requireContext(),
                    "Location unavailable. Check permission and try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun displayWeather(
        record: WeatherRecord
    ) {

        val symbol =
            if (preferences.unit == "imperial")
                "°F"
            else
                "°C"

        binding.locationText.text =
            record.location

        binding.tempText.text =
            String.format(
                "%.1f%s",
                record.temperature,
                symbol
            )

        binding.conditionText.text =
            record.condition.replaceFirstChar {
                it.uppercase()
            }

        binding.humidityText.text =
            "Humidity\n${record.humidity}%"

        binding.windText.text =
            "Wind\n${record.windSpeed} m/s"

        binding.timeText.text =
            "Updated\nnow"
    }

    private fun saveWeather() {

        val weather = currentWeather

        if (weather == null) {

            Toast.makeText(
                requireContext(),
                "Load weather first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        database.insert(weather)

        Toast.makeText(
            requireContext(),
            "Weather record saved",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun shareWeather() {

        val weather =
            currentWeather
                ?: return

        val unit =
            if (preferences.unit == "imperial")
                "°F"
            else
                "°C"

        val text =
            """
            Weather in ${weather.location}
            
            Temperature: ${weather.temperature}$unit
            Condition: ${weather.condition}
            Humidity: ${weather.humidity}%
            Wind Speed: ${weather.windSpeed} m/s
            """.trimIndent()

        val intent =
            Intent(Intent.ACTION_SEND).apply {

                type = "text/plain"

                putExtra(
                    Intent.EXTRA_TEXT,
                    text
                )
            }

        startActivity(
            Intent.createChooser(
                intent,
                "Share weather"
            )
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 1001) {

            if (
                grantResults.any {
                    it == android.content.pm.PackageManager
                        .PERMISSION_GRANTED
                }
            ) {

                loadWeather()

            } else {

                Toast.makeText(
                    requireContext(),
                    "Location permission is required to get local weather.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {

        weatherJob?.cancel()

        _binding = null

        super.onDestroyView()
    }
}