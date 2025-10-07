package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.databinding.AdapterSeatTypeBinding
import com.bitla.ts.domain.pojo.city_pair.*


class SeatTypeAdapter(
    private val context: Context,
    private var setCommissionSeatType: ArrayList<FareDetail>,
    private val listener: DialogAnyClickListener

) :
    RecyclerView.Adapter<SeatTypeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterSeatTypeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return setCommissionSeatType.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.seatTypeTV.text = setCommissionSeatType[position].seatType
        holder.binding.checkboxCB.setOnClickListener {
            setCommissionSeatType[position].isChecked = holder.binding.checkboxCB.isChecked
        }
        if (holder.binding.seatTypeTV.text == "All"){
            holder.binding.checkboxCB.setOnClickListener{
                if (holder.binding.checkboxCB.isChecked) {
                    listener.onAnyClickListener(2,"Select All", position)

                }else{
                    setCommissionSeatType[position].isChecked = holder.binding.checkboxCB.isChecked
                }
            }

        }

        if(setCommissionSeatType[position].isChecked){
            holder.binding.checkboxCB.isChecked = true
        }else{
            holder.binding.checkboxCB.isChecked = false

        }

    }

    class ViewHolder(binding: AdapterSeatTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val binding = binding



    }
}