package com.example.buscoach

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.buscoach.databinding.ChildMultiHopSeatSamePnrGroupChildBinding
import com.example.buscoach.service_details_response.SeatDetail

class MultiHopSeatSamePnrGroupChildAdapter(
    private val context: Context,
    private val iconResourceId: Int,
    private var seatDetailList: MutableList<SeatDetail>,
    private val onClickChildAdapter: ((position:Int, childSeatDetail: SeatDetail) -> Unit)
) : RecyclerView.Adapter<MultiHopSeatSamePnrGroupChildAdapter.ViewHolder>() {


    fun updateList(list: MutableList<SeatDetail>) {
        seatDetailList = list
        notifyDataSetChanged()
    }

    fun updateItemAt(position: Int, item: SeatDetail) {
        item.isSelected = true
        seatDetailList[position] = item
        notifyItemChanged(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildMultiHopSeatSamePnrGroupChildBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return seatDetailList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = seatDetailList[position]

        holder.tvPNR.text = "PNR: "+item.passengerDetails?.ticketNo
        holder.tvSourceDestination.text = "${item.passengerDetails?.originName} ${context.getString(R.string.to)} ${item.passengerDetails?.destinationName}"
        holder.tvTop.text = item.passengerDetails?.seatNo
        holder.imageView.setImageResource(iconResourceId)

        if(item.isSelected) {
            holder.imageView.setColorFilter(Color.parseColor("#2043C2"))
            holder.tvTop.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.imageView.setColorFilter(Color.parseColor("#DFDCDC"))
            holder.tvTop.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        holder.rootLayout.setOnClickListener {
            onClickChildAdapter.invoke(position, item)
        }

    }

    class ViewHolder(binding: ChildMultiHopSeatSamePnrGroupChildBinding) :
    RecyclerView.ViewHolder(binding.root) {
        val seatLayout = binding.seatLayout
        val imageView = binding.imageView
        val tvTop = binding.tvTop
        val tvPNR = binding.tvPNR
        val tvSourceDestination = binding.tvSourceDestination
        val rootLayout = binding.rootLayout

    }
}