package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterUpdateFareBinding
import com.bitla.ts.domain.pojo.city_pair.*


class UpdateFareDownAdapter(
    private val context: Context,
    private var fareDetailList: MutableList<FareDetail>,
    private val parentPosition: Int?,
    private var onFareChangeChild: ((item: FareDetail, position: Int) -> Unit)
) :
    RecyclerView.Adapter<UpdateFareDownAdapter.ViewHolder>() {
    
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterUpdateFareBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun getItemCount(): Int {
        return fareDetailList.size
    }
    
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        
        val item: FareDetail = fareDetailList[position]
        
        item.editedFare = item.fare
        
        holder.seatType.text = item.seatType
        
        if (item.fare.isNullOrEmpty()) {
            holder.fare.setText("")
        } else {
            holder.fare.setText(item.fare)
            
            item.fare.let {
                holder.setSeatFare(it.toString())
            }
        }
        

        
        holder.fare.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                try {
                    item.fare = s.toString()
//                    item.editedFare = s.toString()

                    if (parentPosition == 0) {
                        item.editedFare = s.toString()
                        onFareChangeChild.invoke(item, position)
                    }
                } catch (ex: NumberFormatException) {
                }
            }
        })
        
    }
    
    class ViewHolder(val binding: AdapterUpdateFareBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        var seatType = binding.titleTV
        var fare = binding.etSeatFare
        
        fun setSeatFare(seatFare: CharSequence?) = fare.setText(seatFare)
        
    }
}