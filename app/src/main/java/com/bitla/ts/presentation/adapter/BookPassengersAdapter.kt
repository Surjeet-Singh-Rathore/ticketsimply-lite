package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildPassengersBinding
import com.bitla.ts.domain.pojo.passenger_details_result.PassengerDetailsResult


class BookPassengersAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var passengerList: List<PassengerDetailsResult>,
) :
    RecyclerView.Adapter<BookPassengersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildPassengersBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val passenger: PassengerDetailsResult = passengerList[position]
        holder.tvName.text = passenger.name
        holder.tvAge.text = passenger.age
        holder.tvSeatNo.text = passenger.seatNumber
    }

    class ViewHolder(binding: ChildPassengersBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvName = binding.tvName
        val tvAge = binding.tvAge
        val tvSeatNo = binding.tvSeatNo
    }


}