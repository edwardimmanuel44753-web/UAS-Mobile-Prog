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

        binding.btnBack.setOnClickListener { finish() }

        val statusAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("upcoming", "past")
        )
        binding.spStatus.adapter = statusAdapter

        if (mode == "edit") {
            binding.tvHeader.text = "Edit Event"
            binding.etTitle.setText(intent.getStringExtra("title") ?: "")
            binding.etDate.setText(intent.getStringExtra("date") ?: "")
            binding.etTime.setText(intent.getStringExtra("time") ?: "")
            binding.etLocation.setText(intent.getStringExtra("location") ?: "")
            binding.etDescription.setText(intent.getStringExtra("description") ?: "")
            binding.etCapacity.setText((intent.getIntExtra("capacity", 0)).toString())

            val s = intent.getStringExtra("status") ?: "upcoming"
            binding.spStatus.setSelection(if (s == "past") 1 else 0)
        } else {
            binding.tvHeader.text = "Tambah Event"
        }

        binding.btnSave.setOnClickListener { save() }
    }

    private fun save() {
        val title = binding.etTitle.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val time = binding.etTime.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val description = binding.etDescription.text.toString().trim().ifBlank { null }
        val capacity = binding.etCapacity.text.toString().trim().toIntOrNull()
        val status = binding.spStatus.selectedItem?.toString() ?: "upcoming"

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
                if (mode == "edit") repo.updateEvent(eventId, payload) else repo.createEvent(payload)
            }
            binding.progressBar.hide()
            finish()
        }
    }

    private fun android.view.View.show() { this.visibility = android.view.View.VISIBLE }
    private fun android.view.View.hide() { this.visibility = android.view.View.GONE }
}
