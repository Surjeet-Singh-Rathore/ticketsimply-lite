package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildFiltersBinding
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import gone
import visible

class FilterAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var searchList: List<SearchModel>,
    lastSelectedPositionPayment: Int,
    var isFromPaymentFragment: Boolean = false,
) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    private var tag: String = FilterAdapter::class.java.simpleName

    private var lastSelectedPosition: Int = lastSelectedPositionPayment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildFiltersBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: SearchModel = searchList[position]
        holder.radioItem.text = searchModel.name
        holder.radioItem.isChecked = lastSelectedPosition == position
        if (position != searchList.size - 1) {
            holder.divider.visible()
        } else {
            holder.divider.gone()
        }

        if(isFromPaymentFragment){
            holder.divider.gone()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ViewHolder(binding: ChildFiltersBinding) : RecyclerView.ViewHolder(binding.root) {
        val radioItem = binding.radioItem
        val layoutParent = binding.layoutParent
        val divider = binding.divider

        init {
            radioItem.setOnClickListener {
                radioItem.tag = tag
                lastSelectedPosition = adapterPosition
                notifyDataSetChanged()

                onItemClickListener.onClick(radioItem, lastSelectedPosition)
            }

            layoutParent.setOnClickListener {
                radioItem.tag = tag
                lastSelectedPosition = adapterPosition
                notifyDataSetChanged()

                onItemClickListener.onClick(radioItem, lastSelectedPosition)
            }
        }
    }
}