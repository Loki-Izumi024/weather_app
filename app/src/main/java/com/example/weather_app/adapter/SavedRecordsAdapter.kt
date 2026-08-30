package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemWeatherRecordBinding
import com.example.weatherapp.model.WeatherRecord
import java.text.DateFormat
import java.util.Date

class SavedRecordsAdapter(
    private val onEdit: (WeatherRecord) -> Unit,
    private val onDelete: (WeatherRecord) -> Unit
) : RecyclerView.Adapter<SavedRecordsAdapter.ViewHolder>() {

    private val items =
        mutableListOf<WeatherRecord>()

    fun submitList(
        list: List<WeatherRecord>
    ) {

        items.clear()
        items.addAll(list)

        notifyDataSetChanged()
    }

    inner class ViewHolder(
        val binding: ItemWeatherRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(record: WeatherRecord) {

            binding.location.text =
                record.location

            binding.details.text =
                "${record.temperature}°  •  " +
                        "${record.condition}  •  " +
                        "Humidity ${record.humidity}%  •  " +
                        "Wind ${record.windSpeed} m/s"

            binding.date.text =
                DateFormat
                    .getDateTimeInstance()
                    .format(
                        Date(record.timestamp)
                    )

            binding.editButton.setOnClickListener {
                onEdit(record)
            }

            binding.deleteButton.setOnClickListener {
                onDelete(record)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            ItemWeatherRecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int =
        items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.bind(items[position])
    }
}