package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildSelectMultipleItemHorizontalBinding
import com.bitla.ts.domain.pojo.alloted_services.Service
import firstLetterWord

class MultipleHorizontalItemSelectionAdapter(
    val context: Context,
    val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MultipleHorizontalItemSelectionAdapter.ViewHolder>() {

    var totalSelectedServices = 0
    var serviceListFiltered = mutableListOf<Service>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildSelectMultipleItemHorizontalBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = serviceListFiltered[position]
        holder.tvServiceName.text = item.number
        holder.checkBoxService.isChecked = item.isSeatChecked

        holder.tvServiceName.text = firstLetterWord(item.number.toString())

//        if (item.number == "Lower Berth") {
//            holder.tvServiceName.text = "LB"
//        }
//
//        if (item.number == "Upper Berth") {
//            holder.tvServiceName.text = "UB"
//        }
//
//        if (item.number == "Side Lower Berth") {
//            holder.tvServiceName.text = "SLB"
//        }
//
//        if (item.number == "Side Upper Berth") {
//            holder.tvServiceName.text = "SUB"
//        }
//
//        if (item.number == "Double Lower Berth") {
//            holder.tvServiceName.text = "DLB"
//        }
//
//        if (item.number == "Double Upper Berth") {
//            holder.tvServiceName.text = "DUB"
//        }
//
        if (item.number == "Sleeper") {
            holder.tvServiceName.text = "SL"
        }

        if (item.number == "Seater") {
            holder.tvServiceName.text = "ST"
        }

        holder.checkBoxService.setOnClickListener {
            serviceListFiltered[position].isChecked = holder.checkBoxService.isChecked.not()
            onItemClickListener.onClickOfItem(
                holder.checkBoxService.isChecked.toString(),
                item.routeId ?: 0
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

    class ViewHolder(binding: ChildSelectMultipleItemHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkBoxService = binding.checkBoxService
        val layout = binding.layout
        val tvServiceName = binding.tvServiceName
    }

}