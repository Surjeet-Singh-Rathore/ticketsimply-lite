package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.AdapterServiceListItemBinding
import com.bitla.ts.domain.pojo.available_routes.Result

class ServiceListAdapter(
    private val context: Context,
    private val servicesList: MutableList<Result>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ServiceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterServiceListItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return servicesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        val serviceItem = servicesList[position]

        holder.tvServiceName.text = serviceItem.number
        holder.tvSrcDest.text =
            "${serviceItem.origin} ${context.getString(R.string.to)} ${serviceItem.destination}"
        holder.tvDeptTime.text = serviceItem.dep_time
        holder.checkBox.isChecked = serviceItem.isSelected

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            serviceItem.isSelected = isChecked
            onItemClickListener.onClickOfItem("", position)
        }
    }


    class ViewHolder(binding: AdapterServiceListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvServiceName = binding.tvServiceName
        val tvDeptTime = binding.tvDeptTime
        val checkBox = binding.checkBox
        val tvSrcDest = binding.tvSrcDest
    }
}