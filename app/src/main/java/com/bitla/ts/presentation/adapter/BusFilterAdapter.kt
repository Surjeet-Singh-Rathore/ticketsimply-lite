package com.bitla.ts.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildBusFiltersBinding
import com.bitla.ts.domain.pojo.filter_model.BusFilterModel

class BusFilterAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var busFilterModelList: List<BusFilterModel>,
) :
    RecyclerView.Adapter<BusFilterAdapter.ViewHolder>() {
    private var tag: String = BusFilterAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildBusFiltersBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return busFilterModelList.size
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val busType: BusFilterModel = busFilterModelList[position]

        busType.icon?.let { holder.imgFilter.setImageResource(it) }
        busType.busType?.let { holder.tvBusType.text = it }

        holder.imgFilter.setOnClickListener {
            holder.tvBusType.tag = context.getString(R.string.bus_type)
            busType.isSelected = !(busType.isSelected)!!
            if (busType.isSelected!!) {
                val newColor: Int = ContextCompat.getColor(context, R.color.button_light_color)
                holder.imgFilter.setColorFilter(newColor)
            } else {
                holder.imgFilter.colorFilter = null

            }
//            onItemClickListener.onClick(holder.tvBusType,position)
        }

//        if(busType.isSelected!!) {
//            holder.layoutFilter.setBackgroundColor(Color.parseColor("#ffdd21"))
//        }
//        else {
//            holder.layoutFilter.setBackgroundColor(Color.parseColor("#ffffff"))
//        }


    }


    class ViewHolder(binding: ChildBusFiltersBinding) : RecyclerView.ViewHolder(binding.root) {
        val imgFilter = binding.imgFilter
        val tvBusType = binding.tvBusType
        val layoutFilter = binding.layoutFilter
    }
}