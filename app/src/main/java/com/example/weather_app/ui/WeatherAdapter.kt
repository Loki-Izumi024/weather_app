package com.example.weather_app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.databinding.ItemWeatherBinding
import com.example.weather_app.db.WeatherEntity

/**
 * An Adapter tells the RecyclerView how to display our list of WeatherEntity objects.
 */
class WeatherAdapter(
    private var records: List<WeatherEntity>,
    private val onUpdateClick: (WeatherEntity) -> Unit,
    private val onDeleteClick: (WeatherEntity) -> Unit
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    // A ViewHolder holds the view for a single item in the list
    class WeatherViewHolder(val binding: ItemWeatherBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val record = records[position]
        holder.binding.itemCity.text = record.cityName
        holder.binding.itemTemp.text = record.temperature
        holder.binding.itemCondition.text = record.condition
        holder.binding.itemHumidity.text = "Humidity: ${record.humidity}%"
        holder.binding.itemWind.text = "Wind: ${record.windSpeed}m/s"

        holder.binding.updateButton.setOnClickListener {
            onUpdateClick(record)
        }

        holder.binding.deleteButton.setOnClickListener {
            onDeleteClick(record)
        }
    }

    override fun getItemCount() = records.size

    /**
     * Updates the list of records and refreshes the UI.
     */
    fun updateData(newRecords: List<WeatherEntity>) {
        records = newRecords
        notifyDataSetChanged()
    }
}
