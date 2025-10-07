package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildReviewMappedSeatsBinding
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.response.Seat
import gone
import visible

class ReviewMappedSeatsAdapter(
    private val context: Context,
    private val seatList: MutableList<Seat?>
) : RecyclerView.Adapter<ReviewMappedSeatsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ChildReviewMappedSeatsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return seatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = seatList[position]
        holder.fromSeatTV.text = item?.oldSeat
        holder.toSeatTV.text = item?.newSeat

        if(position == seatList.size -1) {
            holder.tvComma.gone()
        } else {
            holder.tvComma.visible()
        }
    }

    class ViewHolder(binding: ChildReviewMappedSeatsBinding) : RecyclerView.ViewHolder(binding.root) {
        val fromSeatTV=binding.fromSeatTV
        val toSeatTV=binding.toSeatTV
        val tvComma=binding.tvComma
    }

}