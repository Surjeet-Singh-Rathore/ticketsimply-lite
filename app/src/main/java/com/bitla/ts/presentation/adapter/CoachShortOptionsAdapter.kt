package com.bitla.ts.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener

class CoachShortOptionsAdapter(private val dataList: List<String>, private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<CoachShortOptionsAdapter.ViewHolder>() {

    // ViewHolder class to hold the views for each item
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOptionName: TextView = itemView.findViewById(R.id.tvOptionName)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.coach_short_options_child, parent, false)
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.tvOptionName.text = item

        holder.tvOptionName.setOnClickListener {
            onItemClickListener.onClick(holder.tvOptionName,position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
