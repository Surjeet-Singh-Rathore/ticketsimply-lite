package com.bitla.ts.presentation.adapter.NewSortByAdaper

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.MultiCheckboxHorizontalItemBinding

class MultiSeatLuggageAdapter(
    private val context: Context,
    private val seatList: List<String>
) : RecyclerView.Adapter<MultiSeatLuggageAdapter.ViewHolder>() {

    private val selectedSeats = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MultiCheckboxHorizontalItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seat = seatList[position]
        holder.apply {
            tvItem.text = seat
            chkItem.isChecked = selectedSeats.contains(seat)

            chkItem.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedSeats.add(seat)
                } else {
                    selectedSeats.remove(seat)
                }
            }
            mainCL.setOnClickListener {
                chkItem.isChecked = !chkItem.isChecked
            }
        }
    }

    override fun getItemCount(): Int = seatList.size

    fun getSelectedSeats(): List<String> = selectedSeats.toList()

    inner class ViewHolder(binding: MultiCheckboxHorizontalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tvItem = binding.tvItem
        val chkItem = binding.chkItem
        val mainCL = binding.mainCL

    }
}