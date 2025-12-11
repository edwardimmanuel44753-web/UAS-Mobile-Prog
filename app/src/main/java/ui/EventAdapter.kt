package com.example.uasmobprog.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uasmobprog.data.Event
import com.example.uasmobprog.databinding.ItemEventBinding

class EventAdapter(
    private val onClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.VH>() {

    private val items = mutableListOf<Event>()

    fun submitList(list: List<Event>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(e: Event) {
            binding.tvTitle.text = e.title
            // 1 baris meta: tanggal • waktu • lokasi
            binding.tvMeta.text = "${e.date} • ${e.time} • ${e.location}"
            binding.chipStatus.text = e.status
            binding.root.setOnClickListener { onClick(e) }
        }
    }
}
