package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.MoveToExtraOnItemClickListener
import com.bitla.ts.databinding.ChildSelectMultipleSeatBinding

class SelectMultiSeatAdapterForMultiSelection(
    private val context: Context,
    private var neededSeatNumbers: MutableList<String> = mutableListOf(),
    private val onItemCheckListener: MoveToExtraOnItemClickListener
) : RecyclerView.Adapter<SelectMultiSeatAdapterForMultiSelection.ViewHolder>() {

    private var mSelectedItemsIds: SparseBooleanArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildSelectMultipleSeatBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seatNumber: String? = neededSeatNumbers.getOrNull(position)
        if (seatNumber != null) {
            holder.checkSeatNumber.text = seatNumber
        }

        holder.checkSeatNumber.isChecked = mSelectedItemsIds[position, false]

        holder.checkSeatNumber.setOnClickListener {
            if (holder.checkSeatNumber.isChecked) {
                onItemCheckListener.onSeatCheck(seatNumber ?: "")
            } else {
                onItemCheckListener.onSeatUncheck(seatNumber ?: "")
            }
            checkCheckBox(position, !mSelectedItemsIds.get(position))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun checkCheckBox(position: Int, value: Boolean) {
        if (value) mSelectedItemsIds.put(position, true)
        else {
            mSelectedItemsIds.delete(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = neededSeatNumbers.size

    class ViewHolder(binding: ChildSelectMultipleSeatBinding) : RecyclerView.ViewHolder(binding.root) {
        val checkSeatNumber = binding.checkSeatNumber
    }
}