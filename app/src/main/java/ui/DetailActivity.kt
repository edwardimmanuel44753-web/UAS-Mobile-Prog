package com.example.uasmobprog.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.uasmobprog.data.Event
import com.example.uasmobprog.data.EventRepository
import com.example.uasmobprog.databinding.ActivityDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val repo = EventRepository()

    private var eventId: Int = -1
    private var current: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getIntExtra("id", -1)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnEdit.setOnClickListener {
            val e = current ?: return@setOnClickListener
            val i = Intent(this, FormActivity::class.java)
            i.putExtra("mode", "edit")
            i.putExtra("id", e.id ?: -1)
            i.putExtra("title", e.title)
            i.putExtra("date", e.date)
            i.putExtra("time", e.time)
            i.putExtra("location", e.location)
            i.putExtra("description", e.description ?: "")
            i.putExtra("capacity", e.capacity ?: 0)
            i.putExtra("status", e.status)
            startActivity(i)
        }
        binding.btnDelete.setOnClickListener {
            confirmDelete()
        }

        loadDetail()
    }

    override fun onResume() {
        super.onResume()
        loadDetail()
    }

    private fun loadDetail() {
        binding.progressBar.show()

        CoroutineScope(Dispatchers.Main).launch {
            val resp = withContext(Dispatchers.IO) { repo.getEventById(eventId) }
            binding.progressBar.hide()

            val e = resp.data
            current = e

            if (e == null) {
                binding.tvTitle.text = "Event tidak ditemukan"
                return@launch
            }

            binding.tvTitle.text = e.title
            binding.tvDateTime.text = "${e.date} • ${e.time}"
            binding.tvLocation.text = e.location
            binding.tvStatus.text = e.status
            binding.tvCapacity.text = "Capacity: ${e.capacity ?: "-"}"
            binding.tvDescription.text = e.description ?: "-"
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Event?")
            .setMessage("Event ini akan dihapus permanen.")
            .setPositiveButton("Hapus") { _, _ -> deleteNow() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteNow() {
        binding.progressBar.show()

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) { repo.deleteEvent(eventId) }
            binding.progressBar.hide()
            finish()
        }
    }

    private fun android.view.View.show() { this.visibility = android.view.View.VISIBLE }
    private fun android.view.View.hide() { this.visibility = android.view.View.GONE }
}
