package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.SpinnerWithCheckboxDropdownItemBinding
import com.bitla.ts.domain.pojo.destination_pair.SearchModel


class SeatSelectionAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var searchList: List<SearchModel>
) :
    RecyclerView.Adapter<SeatSelectionAdapter.ViewHolder>() {

    // A callback that gets invoked when an item is checked (or unchecked)
    private var callback: Callback? = null
    private var isCheckedAll: Boolean = false

    // Sets the callback
    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    fun selectAll(isCheckedAll: Boolean) {
        this.isCheckedAll = isCheckedAll
        //notifyDataSetChanged()
    }

    // Callback interface, used to notify when an item's checked status changed
    interface Callback {
        fun onCheckedChanged(item: String?, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SpinnerWithCheckboxDropdownItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: SearchModel = searchList[position]
        holder.tvItem.text = searchModel.name

        holder.chkItem.isChecked = isCheckedAll


        // Listen to changes (i.e. when the user checks or unchecks the box)
        holder.chkItem.setOnCheckedChangeListener { _, isChecked -> // Invoke the callback
            if (callback != null)
                callback!!.onCheckedChanged(
                    searchModel.name,
                    isChecked
                )
        }

    }

    class ViewHolder(binding: SpinnerWithCheckboxDropdownItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvItem = binding.tvItem
        val chkItem = binding.chkItem
    }
}