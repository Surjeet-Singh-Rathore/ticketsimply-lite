package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.LayoutItemNotifyOptionBinding
import com.bitla.ts.utils.showToast
import gone
import toast
import visible

class NotifyPassengerOptionsAdapter(private var context: Context, private var itemList: List<String>?, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<NotifyPassengerOptionsAdapter.ViewHolder>() {

    inner class ViewHolder(binding: LayoutItemNotifyOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        val notifyOptionTV = binding.itemNotifyOptionTV
        val dividerView = binding.dividerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemNotifyOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.notifyOptionTV.text = item

        // Show divider for all items except the last one
        if (position == itemCount - 1) {
            holder.dividerView.gone()
        } else {
            holder.dividerView.visible()
        }

        holder.notifyOptionTV.setOnClickListener {
            val updatedType = when(item) {
                context.getString(R.string.ticket_details) -> context.getString(R.string.notify_option_1)
                context.getString(R.string.bus_info_sms) -> context.getString(R.string.notify_option_2)
                context.getString(R.string.crew_details) -> context.getString(R.string.notify_option_3)
                context.getString(R.string.boarding_details) -> context.getString(R.string.notify_option_4)
                else -> context.getString(R.string.notify_option_1)
            }
            onItemClick(updatedType)
        }
    }

    override fun getItemCount(): Int = itemList?.size ?: 0

}