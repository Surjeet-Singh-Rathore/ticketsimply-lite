package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.AdapterAdditionalInfoBinding
import com.bitla.ts.databinding.AdapterMultiCityBookingBinding
import com.bitla.ts.databinding.AdapterServiceListRouteBinding
import com.bitla.ts.domain.pojo.available_routes.BusAmenity
import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import com.bumptech.glide.Glide

class AdditionalInfoAdapter(
    private val context: Context,
    private var additionalInfoArray: ArrayList<CitiesListData>,
    var listener: DialogAnyClickListener
) :
    RecyclerView.Adapter<AdditionalInfoAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterAdditionalInfoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return additionalInfoArray.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.additionalInfoTV.text = additionalInfoArray[position].name


        if(additionalInfoArray[position].isChecked){
            holder.binding.checkboxCB.isChecked = true
        }else{
            holder.binding.checkboxCB.isChecked = false
        }

        holder.binding.checkboxCB.setOnClickListener {
            if (holder.binding.checkboxCB.isChecked){
                listener.onAnyClickListener(1,true,position)
            }else{
                listener.onAnyClickListener(1,false,position)
            }
        }
    }

    class ViewHolder(binding: AdapterAdditionalInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val binding = binding


    }
}