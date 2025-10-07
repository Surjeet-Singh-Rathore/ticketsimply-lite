package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildRetrievePaxBinding
import com.bitla.ts.domain.pojo.passenger_details_result.PassengerDetailsResult
import com.bitla.ts.domain.pojo.passenger_history.PassengerHistoryModel
import toast


class RetrievePaxAdapter(
    private val context: Context,
    private var paxList: List<PassengerHistoryModel>,
    private var passengerList: ArrayList<PassengerDetailsResult>,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<RetrievePaxAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildRetrievePaxBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return paxList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pax: PassengerHistoryModel = paxList[position]
        holder.checkPax.text = pax.name

        holder.checkPax.setOnClickListener {
            pax.isChecked = holder.checkPax.isChecked
            val checkedPaxList = paxList.filter { it.isChecked }
            if (checkedPaxList.size > passengerList.size)
            {
                pax.isChecked = false
                holder.checkPax.isChecked = false
                context.toast("${context.getString(R.string.validate_max_pax)} ${passengerList.size} ${context.getString(R.string.max_passenger)}")
            }else {
                holder.checkPax.tag = context.getString(R.string.retrieve)
                onItemClickListener.onClick(view = holder.checkPax,position = position)
            }
        }
    }

    class ViewHolder(binding: ChildRetrievePaxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkPax = binding.checkPax
        val layoutPax = binding.layoutPax
    }
}