package com.bitla.ts.presentation.view.merge_bus.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.AdapterMergeShiftServicesBinding
import com.bitla.ts.databinding.ChildBusDelayBinding
import com.bitla.ts.domain.pojo.Weekdays


class NearByMatchesAdapter(
    private val context: Context,
) :
    RecyclerView.Adapter<NearByMatchesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterMergeShiftServicesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
    class ViewHolder(binding: AdapterMergeShiftServicesBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}