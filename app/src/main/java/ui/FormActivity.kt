package com.example.uasmobprog.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.uasmobprog.data.Event
import com.example.uasmobprog.data.EventRepository
import com.example.uasmobprog.databinding.ActivityFormBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private val repo = EventRepository()

    private var mode: String = "create"
    private var eventId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mode = intent.getStringExtra("mode") ?: "create"
        eventId = intent.getIntExtra("id", -1)

        // tombol back di bawah card
        binding.btnBack.setOnClickListener { finish() }

        // isi spinner status dengan label Indonesia
        val statusAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Akan Datang", "Sudah Lewat")
        )
        binding.spStatus.adapter = statusAdapter

        if (mode == "edit") {
            binding.tvHeader.text = "Edit Event"

            binding.etTitle.setText(intent.getStringExtra("title") ?: "")
            binding.etDate.setText(intent.getStringExtra("date") ?: "")
            binding.etTime.setText(intent.getStringExtra("time") ?: "")
            binding.etLocation.setText(intent.getStringExtra("location") ?: "")
            binding.etDescription.setText(intent.getStringExtra("description") ?: "")
            binding.etCapacity.setText(
                intent.getIntExtra("capacity", 0).toString()
            )

            val s = intent.getStringExtra("status") ?: "upcoming"
            // kalau status dari server "past" → pilih "Sudah Lewat"
            binding.spStatus.setSelection(if (s == "past") 1 else 0)
        } else {
            binding.tvHeader.text = "Create Event"
        }

        binding.btnSave.setOnClickListener { save() }
        binding.btnClear.setOnClickListener { clearForm() }
    }

    private fun save() {
        val title = binding.etTitle.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val time = binding.etTime.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val description = binding.etDescription.text.toString().trim().ifBlank { null }
        val capacity = binding.etCapacity.text.toString().trim().toIntOrNull()

        // label yang tampil di spinner (Indonesia)
        val statusLabel = binding.spStatus.selectedItem?.toString() ?: "Akan Datang"
        // mapping ke value yang dipakai API
        val status = if (statusLabel.equals("Sudah Lewat", ignoreCase = true)) {
            "past"
        } else {
            "upcoming"
        }

        if (title.isBlank() || date.isBlank() || time.isBlank() || location.isBlank()) {
            binding.tvError.text = "Title/Date/Time/Location wajib diisi."
            binding.tvError.show()
            return
        }

        binding.tvError.hide()
        binding.progressBar.show()

        val payload = Event(
            id = if (mode == "edit") eventId else null,
            title = title,
            date = date,
            time = time,
            location = location,
            description = description,
            capacity = capacity,
            status = status
        )

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                if (mode == "edit") {
                    repo.updateEvent(eventId, payload)
                } else {
                    repo.createEvent(payload)
                }
            }
            binding.progressBar.hide()
            finish()
        }
    }

    private fun clearForm() {
        binding.etTitle.text?.clear()
        binding.etDate.text?.clear()
        binding.etTime.text?.clear()
        binding.etLocation.text?.clear()
        binding.etDescription.text?.clear()
        binding.etCapacity.text?.clear()
        binding.spStatus.setSelection(0) // kembali ke "Akan Datang"
        binding.tvError.hide()
    }

    // helper extension biar gampang show/hide view
    private fun android.view.View.show() {
        this.visibility = android.view.View.VISIBLE
    }

    private fun android.view.View.hide() {
        this.visibility = android.view.View.GONE
    }
}
