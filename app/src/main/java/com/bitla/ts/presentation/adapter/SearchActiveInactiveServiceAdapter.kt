package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildSearchServiceBinding
import com.bitla.ts.domain.pojo.active_inactive_services.response.Service

class SearchActiveInactiveServiceAdapter(
    val context: Context, private val onItemClick: ((serviceId: Long, isChecked: Boolean) -> Unit)
) : RecyclerView.Adapter<SearchActiveInactiveServiceAdapter.ViewHolder>() {

    var totalSelectedServices = 0
    private var serviceListFiltered = mutableListOf<Service?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildSearchServiceBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = serviceListFiltered[position]

        holder.tvServiceName.text = item?.serviceName
        holder.checkBoxService.isChecked = item?.isChecked ?: true

        holder.checkBoxService.setOnClickListener {
            serviceListFiltered[position]?.isChecked = holder.checkBoxService.isChecked.not()
            onItemClick.invoke(
                item?.serviceId ?: 0, holder.checkBoxService.isChecked
            )
        }
        holder.layout.setOnClickListener {
            holder.checkBoxService.performClick()
        }

    }

    override fun getItemCount(): Int {
        return serviceListFiltered.size
    }

    fun addData(serviceList: MutableList<Service?>) {
        serviceListFiltered = serviceList
        notifyDataSetChanged()
    }

    class ViewHolder(binding: ChildSearchServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        val checkBoxService = binding.checkBoxService
        val layout = binding.layout
        val tvServiceName = binding.tvServiceName
    }

}