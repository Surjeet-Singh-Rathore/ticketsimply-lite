package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildMultistationListBinding
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.MultistationFareDetails
import com.bitla.ts.presentation.view.fragments.BottomModalSheetFragment
import com.bitla.ts.utils.common.convert

class MultistationFareListAdapter(
    private val context: Context,
    private var searchList: List<MultistationFareDetails>,
    private val routeId: String?,
    private val reservationId: String?,
    private val currency: String,
    private val currencyFormat: String,
) :
    RecyclerView.Adapter<MultistationFareListAdapter.ViewHolder>() {

    class ViewHolder(binding: ChildMultistationListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvName = binding.tvname
        val tvrates = binding.tvrates
        val tvEdit = binding.tvEdit

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildMultistationListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text =
            "${searchList[position].origin_name} - ${searchList[position].destination_name}"
        var rate = ""
        searchList[position].fareDetails.forEach {
            rate =
                rate + currency + if (it.fare != null && it.fare!!.isNotEmpty()) (it.fare!!.toDouble()).convert(
                    currencyFormat
                ) + ", "
                else it.fare + "/"
        }
//        rate = rate.substring(0, rate.length - 1)
        holder.tvrates.text = rate.substringBeforeLast(",")

        holder.tvEdit.setOnClickListener {
            val ft = (context as AppCompatActivity).supportFragmentManager

            BottomModalSheetFragment().newInstance(
                searchList[position].fareDetails,
                holder.tvName.text.toString(),
                routeId.toString(),
                reservationId.toString(),
                searchList[position].origin_id,
                searchList[position].destination_id
            ).apply {
                show(ft, BottomModalSheetFragment.TAG)
            }
        }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }
}