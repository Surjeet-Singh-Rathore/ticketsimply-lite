package com.bitla.ts.presentation.view.merge_bus

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildTicketShiftedToBinding
import com.bitla.ts.domain.pojo.samePNRSeatModel.SamePNRSeatModel


class TicketShiftedToAdapter(
    private val context: Context,
    private val samePNRModelList: MutableList<SamePNRSeatModel>
): RecyclerView.Adapter<TicketShiftedToAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildTicketShiftedToBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return samePNRModelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = samePNRModelList.get(position)

        holder.pnrHeader.text = context.getString(R.string.pnr) + ": "
        holder.pnrNumber.text = item.pnr

        holder.toHeader.text = context.getString(R.string.to_pascal_case) + ": "
        holder.destinationName.text = item.destinationName
        holder.bookedBy.text = item.bookedBy

        val seatShiftedToAdapter = SeatShiftedToAdapter(context, item.seatShiftList)
        holder.rvSeatShiftedTo.adapter = seatShiftedToAdapter

    }

    class ViewHolder(binding: ChildTicketShiftedToBinding): RecyclerView.ViewHolder(binding.root) {
        val rvSeatShiftedTo = binding.rvSeatShiftedTo
        val pnrHeader = binding.pnrHeader
        val pnrNumber = binding.pnrNumber
        val toHeader = binding.toHeader
        val destinationName = binding.destinationName
        val bookedBy = binding.bookedBy
    }
}
