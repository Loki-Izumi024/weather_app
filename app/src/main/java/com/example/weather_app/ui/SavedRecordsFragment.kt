package com.example.weatherapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapter.SavedRecordsAdapter
import com.example.weatherapp.databinding.FragmentSavedRecordsBinding
import com.example.weatherapp.db.WeatherDbHelper
import com.example.weatherapp.model.WeatherRecord

class SavedRecordsFragment : Fragment() {

    private var _binding:
            FragmentSavedRecordsBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var database: WeatherDbHelper
    private lateinit var adapter: SavedRecordsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentSavedRecordsBinding.inflate(
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

        database =
            WeatherDbHelper(requireContext())

        adapter =
            SavedRecordsAdapter(
                onEdit = {
                    editRecord(it)
                },
                onDelete = {
                    deleteRecord(it)
                }
            )

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerView.adapter =
            adapter

        loadRecords()
    }

    private fun loadRecords() {

        val records =
            database.getAll()

        adapter.submitList(records)

        if (records.isEmpty()) {

            binding.emptyText.visibility =
                View.VISIBLE

            binding.recyclerView.visibility =
                View.GONE

        } else {

            binding.emptyText.visibility =
                View.GONE

            binding.recyclerView.visibility =
                View.VISIBLE
        }
    }

    private fun editRecord(
        record: WeatherRecord
    ) {

        val input =
            EditText(requireContext())

        input.setText(record.location)

        AlertDialog.Builder(requireContext())
            .setTitle("Update Location")
            .setMessage(
                "Edit the saved location name"
            )
            .setView(input)
            .setPositiveButton("Update") {
                    _, _ ->

                val newLocation =
                    input.text
                        .toString()
                        .trim()
                        .ifEmpty {
                            record.location
                        }

                database.update(
                    record.copy(
                        location = newLocation
                    )
                )

                loadRecords()
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    private fun deleteRecord(
        record: WeatherRecord
    ) {

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Record?")
            .setMessage(
                "Delete weather record for ${record.location}?"
            )
            .setPositiveButton("Delete") {
                    _, _ ->

                database.delete(record.id)

                loadRecords()
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    override fun onDestroyView() {

        _binding = null

        super.onDestroyView()
    }
}