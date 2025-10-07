package com.bitla.ts.presentation.view.merge_bus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.AdapterMergeShiftServicesBinding
import com.bitla.ts.presentation.view.merge_bus.pojo.ExactRouteService
import com.bitla.ts.utils.constants.DATE_FORMAT_12
import com.bitla.ts.utils.constants.DATE_FORMAT_HH_MM_24
import convertTimeFormat
import toPercentageValue



class ExactRouteServicesAdapter(
    private val context: Context,
    private val exactRouteServices: ArrayList<ExactRouteService>,
    private val onItemClick: ((exactRouteService: ExactRouteService) -> Unit)
) :
    RecyclerView.Adapter<ExactRouteServicesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterMergeShiftServicesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return exactRouteServices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = exactRouteServices[position]
        holder.itemView.rootView.setOnClickListener {
            onItemClick.invoke(item)
        }

        holder.serviceInfoTV.text =
            item.depTime?.convertTimeFormat(DATE_FORMAT_HH_MM_24, DATE_FORMAT_12) + " | " + item.name
        holder.serviceTypeTV.text = item.busType
        holder.exactSeatNoTV.text =
            context.getString(R.string.exact_seat_nos, item.matchScore?.seatNos)
        holder.exactSeatTypeTV.text =
            context.getString(R.string.exact_seat_type, item.matchScore?.seatType)
        holder.seatsTV.text = item.availableSeats.toString() + "/" + item.totalSeats.toString()
        holder.netRevenueTV.text = (context.getString(R.string.net_revenue_, item.netRevenue)) ?: ""
        val seatNosPercent = item.matchScore?.seatNumberAvailabilityPercentage?.toPercentageValue()
        when (seatNosPercent!!) {
            100.0 -> {
                holder.exactSeatNoTV.background =
                    AppCompatResources.getDrawable(context, R.drawable.bg_selected_green)
            }

            in 21.0..99.9 -> {
                holder.exactSeatNoTV.background =
                    AppCompatResources.getDrawable(context, R.drawable.bg_selected_slate_grey)

            }

            in 0.0..20.99 -> {
                holder.exactSeatNoTV.background =
                    AppCompatResources.getDrawable(context, R.drawable.bg_selected_red)

            }

            else -> {
                holder.exactSeatNoTV.background =
                    AppCompatResources.getDrawable(context, R.drawable.bg_selected_red)
            }
        }



        val seatTypePercent = item.matchScore?.seatTypeMatchPercentage?.toPercentageValue()
        when (seatTypePercent!!) {
            100.0 -> {
                holder.exactSeatTypeTV.background =
                    AppCompatResources.getDrawable(context, R.drawable.bg_selected_green)
            }
            in 21.0..99.9 -> {
                holder.exactSeatTypeTV.background =
                    AppCompatResources.getDrawable(context, R.drawable.bg_selected_slate_grey)

            }
            in 0.0..20.99 -> {
                holder.exactSeatTypeTV.background =
                    AppCompatResources.getDrawable(context, R.drawable.bg_selected_red)

            }

            else -> {
                holder.exactSeatTypeTV.background =
                    AppCompatResources.getDrawable(context, R.drawable.bg_selected_red)
            }
        }


    }

    class ViewHolder(binding: AdapterMergeShiftServicesBinding) :
        RecyclerView.ViewHolder(binding.root) {


        val serviceInfoTV = binding.nameServiceTV
        val serviceTypeTV = binding.seatTypeTV
        val seatsTV = binding.seatsTV
        val netRevenueTV = binding.revenueTV
        val exactSeatNoTV = binding.exactSeatNumberTV
        val exactSeatTypeTV = binding.exactSeatTypeTV

    }
}