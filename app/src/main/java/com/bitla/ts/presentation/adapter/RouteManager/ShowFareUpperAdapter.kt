package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.content.Context
import android.text.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterShowFareBinding
import com.bitla.ts.domain.pojo.city_pair.*
import firstLetterWord


class ShowFareUpperAdapter(
    private val context: Context,
    private var fareDetailList: MutableList<FareDetail>,
) :
    RecyclerView.Adapter<ShowFareUpperAdapter.ViewHolder>() {
    
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterShowFareBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun getItemCount(): Int {
        return fareDetailList.size
    }
    
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        
        val item: FareDetail = fareDetailList[position]
        holder.tvSeatName.text = "${item.seatType}: "


        if (item.fare.isNullOrEmpty()) {
            holder.tvSeatFare.text = "₹0"
        } else {
            holder.tvSeatFare.text = "₹"+item.fare
          /*  item.fare.let {
                holder.setSeatFare(it.toString())
            }*/
        }
        
        holder.tvSeatFare.addTextChangedListener(object :
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
                   // item.fare = s.toString()
                } catch (ex: NumberFormatException) {
                }
            }
        })
        
    }
    
    class ViewHolder(binding: AdapterShowFareBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvSeatName = binding.tvSeatName
        val tvSeatFare = binding.tvSeatFare
        
        fun setSeatFare(seatFare: CharSequence?) {
            tvSeatFare.text = seatFare
        }
    }
}