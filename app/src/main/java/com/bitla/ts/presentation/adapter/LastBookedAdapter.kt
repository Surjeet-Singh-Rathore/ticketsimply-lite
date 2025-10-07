package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnPnrListener
import com.bitla.ts.databinding.ChildLastBookedBinding
import com.bitla.ts.domain.pojo.recent_bookings.RecentBooking
import com.bitla.ts.utils.common.inputFormatToOutput
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_YY
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D
import gone

class LastBookedAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private val onPnrListener: OnPnrListener,
    private var recentBookings: MutableList<RecentBooking>
) :
    RecyclerView.Adapter<LastBookedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildLastBookedBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recentBookings.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recentBooking: RecentBooking = recentBookings[position]
        val srcDest = "${recentBooking.origin_name} - ${recentBooking.destination_name}"
        holder.tvSrcDest.text = srcDest
        val totalAmount = "${context.getString(R.string.RuppeeSymbol)}${recentBooking.total_fare}"
        holder.tvAmount.text = totalAmount
        val passengers =
            "${recentBooking.no_of_seats} Passenger(s) | Booked on ${recentBooking.created_on}"
        holder.tvPassengers.text = passengers
        holder.tvDate.text = "${
            inputFormatToOutput(
                recentBooking.travel_date,
                DATE_FORMAT_Y_M_D,
                DATE_FORMAT_D_M_YY
            )
        }"
        holder.tvPnrNo.text = recentBooking.pnr_number


        holder.imgEdit.setOnClickListener {
            onPnrListener.onPnrSelection(
                context.getString(R.string.booking_edit),
                recentBooking.pnr_number
            )
        }

        holder.imgClose.setOnClickListener {
            onPnrListener.onPnrSelection(
                context.getString(R.string.booking_close),
                recentBooking.pnr_number
            )
        }
        if (!recentBookings[position].is_cancellable) {
            holder.imgClose.gone()
        }
        if (!recentBookings[position].is_updatable) {
            holder.imgEdit.gone()
        }
    }

    class ViewHolder(binding: ChildLastBookedBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvPnrNo = binding.tvPnr
        val tvDate = binding.tvDate
        val tvAmount = binding.tvAmount
        val tvPassengers = binding.tvPassengers
        val tvSrcDest = binding.tvSrcDest
        val imgEdit = binding.imgEdit
        val imgClose = binding.imgClose
    }
}