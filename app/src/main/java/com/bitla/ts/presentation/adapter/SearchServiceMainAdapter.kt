package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemCheckedListener
import com.bitla.ts.databinding.ChildSearchServiceBinding
import com.bitla.ts.domain.pojo.alloted_services.Service

class SearchServiceMainAdapter(
    val context: Context,
    val OnItemCheckedListener: OnItemCheckedListener
) : RecyclerView.Adapter<SearchServiceMainAdapter.ViewHolder>() {

    var totalSelectedServices = 0
    var serviceListFiltered = mutableListOf<Service>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildSearchServiceBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = serviceListFiltered[position]

        holder.tvServiceName.text = item.number
        holder.checkBoxService.isChecked = item.isChecked

        holder.checkBoxService.setOnClickListener {
            serviceListFiltered[position].isChecked = holder.checkBoxService.isChecked.not()
            OnItemCheckedListener.onItemChecked(
                serviceListFiltered[position].isChecked,
                holder.checkBoxService,
                position
            )
        }
        holder.layout.setOnClickListener {
            holder.checkBoxService.performClick()
        }

    }

    override fun getItemCount(): Int {
        return serviceListFiltered.size
    }

    fun addData(serviceList: MutableList<Service>) {
        serviceListFiltered = serviceList
        notifyDataSetChanged()
    }

    class ViewHolder(binding: ChildSearchServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkBoxService = binding.checkBoxService
        val layout = binding.layout
        val tvServiceName = binding.tvServiceName
    }

}