package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterFrequentTravellerDataBinding
import com.bitla.ts.domain.pojo.frequent_traveller_model.response.Result

class FrequentTravellerDataAdapter(
    private val context: Context,
    private var frequentTravellerData: MutableList<Result>,
) :
    RecyclerView.Adapter<FrequentTravellerDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterFrequentTravellerDataBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return frequentTravellerData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val frequentTravellerData: Result = frequentTravellerData.get(position)

        holder.tvName.text = " ${frequentTravellerData.passengerName} | ${frequentTravellerData.mobileNo}"
        holder.tvPnrNo.text = frequentTravellerData.pnrNo
        holder.tvSeatNo.text = frequentTravellerData.seatNo
        holder.tvTrips.text = frequentTravellerData.tripCounts
    }

    class ViewHolder(binding: AdapterFrequentTravellerDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tvName = binding.nameAndNumberTV
        val tvPnrNo = binding.pnrTV
        val tvSeatNo = binding.seatNumberTV
        val tvTrips = binding.totalTripsValue
    }
}