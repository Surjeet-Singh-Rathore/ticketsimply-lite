package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildFiltersBinding

class CityFilterHubAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var searchList: List<String>,
    lastSelectedPositionPayment: Int,
) :
    RecyclerView.Adapter<CityFilterHubAdapter.ViewHolder>() {
    private var tag: String = CityFilterHubAdapter::class.java.simpleName

    private var lastSelectedPosition: Int = lastSelectedPositionPayment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildFiltersBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val searchModel: String = searchList[position]
        holder.radioItem.isChecked = lastSelectedPosition == position
//        lastSelectedPosition= position
        holder.radioItem.text = searchModel
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ViewHolder(binding: ChildFiltersBinding) : RecyclerView.ViewHolder(binding.root) {
        val radioItem = binding.radioItem
        val layoutParent = binding.layoutParent

        init {
            radioItem.setOnClickListener {
//                radioItem.tag = tag
                lastSelectedPosition = adapterPosition

                onItemClickListener.onClickOfItem(radioItem.text.toString(), lastSelectedPosition)
                notifyDataSetChanged()

            }

            layoutParent.setOnClickListener {
                radioItem.tag = tag
                lastSelectedPosition = adapterPosition
                notifyDataSetChanged()

                onItemClickListener.onClickOfItem(radioItem.text.toString(), lastSelectedPosition)
            }
        }
    }
}











