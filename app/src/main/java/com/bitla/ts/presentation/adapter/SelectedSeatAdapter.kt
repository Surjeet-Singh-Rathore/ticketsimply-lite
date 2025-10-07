package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.AdapterSelectedSeatsBinding
import com.bitla.ts.databinding.ChildSelectedSeatsBinding
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail

class SelectedSeatAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var selectedSeatDetails: ArrayList<SeatDetail>,
) :
    RecyclerView.Adapter<SelectedSeatAdapter.ViewHolder>() {

    companion object {
        var TAG: String = SelectedSeatAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterSelectedSeatsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return selectedSeatDetails.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seatDetail: SeatDetail = selectedSeatDetails[position]
        holder.title.text = "${seatDetail.number}"


    }

    class ViewHolder(binding: AdapterSelectedSeatsBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.name
    }
}