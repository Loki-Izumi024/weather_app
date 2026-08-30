package com.example.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.util.PreferencesManager

class SettingsFragment : Fragment() {

    private var _binding:
            FragmentSettingsBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var preferences:
            PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentSettingsBinding.inflate(
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

        super.onViewCreated(
            view,
            savedInstanceState
        )

        preferences =
            PreferencesManager(requireContext())

        if (preferences.unit == "metric") {

            binding.celsiusRadio.isChecked =
                true

        } else {

            binding.fahrenheitRadio.isChecked =
                true
        }

        binding.unitGroup
            .setOnCheckedChangeListener { _, id ->

                preferences.unit =
                    if (id == R.id.fahrenheitRadio)
                        "imperial"
                    else
                        "metric"
            }
    }

    override fun onDestroyView() {

        _binding = null

        super.onDestroyView()
    }
}