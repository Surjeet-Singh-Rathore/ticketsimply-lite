package com.bitla.ts.presentation.view.merge_bus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.AdapterShiftingSuccessBinding
import com.bitla.ts.domain.pojo.merge_bus_shift_passenger.response.ShiftedSeat


class ShiftedSeatsAdapter(
    private val context: Context,
    private val seatShiftList: List<ShiftedSeat?>
) :
    RecyclerView.Adapter<ShiftedSeatsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterShiftingSuccessBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return seatShiftList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = seatShiftList[position]
        holder.tvOldSeat.text = item?.from
        holder.tvNewSeat.text = item?.to
        if(item?.to.equals(context.getString(R.string.notAvailable))) {
            holder.shiftStatus.text = context.getString(R.string.not_shifted)
            holder.shiftStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_service_cancelled_colored, 0, 0, 0)
            holder.shiftStatus.background = ContextCompat.getDrawable(context, R.drawable.seat_not_shifted)
            holder.shiftStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
        } else {
            holder.shiftStatus.text = context.getString(R.string.shifted)
            holder.shiftStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_green, 0, 0, 0)
            holder.shiftStatus.background = ContextCompat.getDrawable(context, R.drawable.seat_shifted)
            holder.shiftStatus.setTextColor(ContextCompat.getColor(context, R.color.green_color_text))
        }

    }
    class ViewHolder(binding: AdapterShiftingSuccessBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvOldSeat = binding.tvOldSeat
        val tvNewSeat = binding.tvNewSeat
        val shiftStatus = binding.shiftStatus
    }
}