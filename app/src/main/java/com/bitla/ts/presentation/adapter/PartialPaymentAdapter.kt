package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import com.bitla.ts.databinding.ChildFiltersBinding
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildFiltersHorizontalBinding

class PartialPaymentAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var searchList: List<SearchModel>,
    lastSelectedPositionPayment: Int,
) :
    RecyclerView.Adapter<PartialPaymentAdapter.ViewHolder>() {
    private var tag: String = PartialPaymentAdapter::class.java.simpleName

    private var lastSelectedPosition: Int = lastSelectedPositionPayment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildFiltersHorizontalBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: SearchModel = searchList[position]
        holder.radioItem.text = searchModel.name
        holder.radioItem.isChecked = lastSelectedPosition == position

        if (holder.radioItem.isChecked)
            holder.radioItem.setTextColor(context.resources.getColor(R.color.colorBlackShadow))
        else
            holder.radioItem.setTextColor(context.resources.getColor(R.color.colorDimShadow6))
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ViewHolder(binding: ChildFiltersHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        val radioItem = binding.radioItem
        val layoutParent = binding.layoutParent

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