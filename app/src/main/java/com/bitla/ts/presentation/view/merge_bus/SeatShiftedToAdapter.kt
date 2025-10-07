package com.bitla.ts.presentation.view.merge_bus

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.text.italic
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildSeatShiftedToBinding
import com.bitla.ts.domain.pojo.samePNRSeatModel.SeatShiftModel
import gone
import visible

class SeatShiftedToAdapter(
    private val context: Context,
    private val seatShiftList: MutableList<SeatShiftModel>,
): RecyclerView.Adapter<SeatShiftedToAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildSeatShiftedToBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return seatShiftList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = seatShiftList[position]

        holder.seatShiftedFrom.text = item.oldSeat.number
        holder.passengerType.text = item.oldSeat.type

        if(item.newSeat == null) {
            holder.greenCheckIcon.gone()
            val notAssignedString = SpannableStringBuilder()
                .color(ContextCompat.getColor(context, R.color.colorDimShadow)) {
                    italic {
                        append(context.getString(R.string.not_assigned_))
                    }
                }
            holder.seatShiftedTo.text = notAssignedString

        } else {
            holder.seatShiftedTo.text = item.newSeat?.number ?: ""
            holder.greenCheckIcon.visible()
        }
    }

    class ViewHolder(binding: ChildSeatShiftedToBinding): RecyclerView.ViewHolder(binding.root) {
        val seatShiftedFrom = binding.seatShiftedFrom
        val seatShiftedTo = binding.seatShiftedTo
        val greenCheckIcon = binding.greenCheckIcon
        val passengerType = binding.passengerType
    }
}