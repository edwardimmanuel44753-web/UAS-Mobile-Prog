package com.example.uasmobprog

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.uasmobprog.data.Event
import com.example.uasmobprog.data.EventRepository
import com.example.uasmobprog.databinding.ActivityMainBinding
import com.example.uasmobprog.ui.DetailActivity
import com.example.uasmobprog.ui.EventAdapter
import com.example.uasmobprog.ui.FormActivity
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val repo = EventRepository()

    private lateinit var adapter: EventAdapter
    private var allEvents: List<Event> = emptyList()

    private var selectedStatus: String? = null // null=all, "upcoming", "past", dll
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        adapter = EventAdapter { event ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("id", event.id ?: -1)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter

        setupChips()
        loadEvents()
    }

    override fun onResume() {
        super.onResume()
        // refresh setelah balik dari FormActivity
        loadEvents()
    }

    private fun setupChips() {
        binding.chipGroup.removeAllViews()

        val chipAll = makeChip("All", true)
        chipAll.setOnClickListener {
            selectedStatus = null
            setChipChecked(chipAll)
            loadEvents()
        }

        val chipUpcoming = makeChip("Upcoming", false)
        chipUpcoming.setOnClickListener {
            selectedStatus = "upcoming"
            setChipChecked(chipUpcoming)
            loadEvents()
        }

        val chipPast = makeChip("Past", false)
        chipPast.setOnClickListener {
            selectedStatus = "past"
            setChipChecked(chipPast)
            loadEvents()
        }

        binding.chipGroup.addView(chipAll)
        binding.chipGroup.addView(chipUpcoming)
        binding.chipGroup.addView(chipPast)
    }

    private fun makeChip(text: String, checked: Boolean): Chip {
        val chip = Chip(this)
        chip.text = text
        chip.isCheckable = true
        chip.isChecked = checked
        return chip
    }

    private fun setChipChecked(target: Chip) {
        for (i in 0 until binding.chipGroup.childCount) {
            val c = binding.chipGroup.getChildAt(i) as Chip
            c.isChecked = (c == target)
        }
    }

    private fun loadEvents() {
        binding.progressBar.show()

        CoroutineScope(Dispatchers.Main).launch {
            val resp = withContext(Dispatchers.IO) {
                repo.getEvents(status = selectedStatus)
            }

            binding.progressBar.hide()

            val data = resp.data ?: emptyList()
            allEvents = data
            applySearchAndShow()
        }
    }

    private fun applySearchAndShow() {
        val q = searchQuery.trim().lowercase()
        val filtered = if (q.isBlank()) {
            allEvents
        } else {
            allEvents.filter {
                it.title.lowercase().contains(q) ||
                        (it.description ?: "").lowercase().contains(q) ||
                        it.location.lowercase().contains(q)
            }
        }
        adapter.submitList(filtered)
        binding.tvCount.text = "Menampilkan: ${filtered.size} event"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val sv = searchItem.actionView as SearchView
        sv.queryHint = "Cari event..."
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query ?: ""
                applySearchAndShow()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText ?: ""
                applySearchAndShow()
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(this, FormActivity::class.java))
                true
            }
            R.id.action_refresh -> {
                loadEvents()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun android.view.View.show() { this.visibility = android.view.View.VISIBLE }
    private fun android.view.View.hide() { this.visibility = android.view.View.GONE }
}
