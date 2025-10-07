package com.example.buscoach

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buscoach.databinding.ChildMultiHopSeatSamePnrGroupParentBinding
import com.example.buscoach.multistation_data.MultiHopSeatDetail
import com.example.buscoach.service_details_response.SeatDetail

class MultiHopSeatSamePnrGroupParentAdapter(
    private val context: Context,
    private val iconResourceId: Int,
    private var multiHopSeatDetailsList: MutableList<MultiHopSeatDetail>,
    private val setAdapterCallback: ((position:Int, item: MultiHopSeatSamePnrGroupChildAdapter) -> Unit),
    private val onClickParentAdapter: ((parentPosition:Int, childPosition:Int, childSeatDetail: SeatDetail) -> Unit)
) : RecyclerView.Adapter<MultiHopSeatSamePnrGroupParentAdapter.ViewHolder>() {

    fun updateList(list: MutableList<MultiHopSeatDetail>) {
        multiHopSeatDetailsList = list
        notifyDataSetChanged()
    }

    fun updateItemAt(position: Int, item: MultiHopSeatDetail) {
        multiHopSeatDetailsList[position] = item
        notifyItemChanged(position)
    }


    fun drawBorderAroundPNRGroupAt(position: Int) {
        multiHopSeatDetailsList.forEachIndexed { index, item ->
            if(position == index) {
                item.isPNRGroupSelected = true
            } else {
                item.isPNRGroupSelected = false
            }
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildMultiHopSeatSamePnrGroupParentBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return multiHopSeatDetailsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = multiHopSeatDetailsList[position]

        /*holder.rootLayout.setOnClickListener {
            onClick.invoke(item)
        }*/


        val multiHopSeatSamePnrGroupChildAdapter = MultiHopSeatSamePnrGroupChildAdapter(context,iconResourceId, item.seat_details ?: mutableListOf()) {childPosition, childSeatDetail ->
            onClickParentAdapter.invoke(position, childPosition, childSeatDetail)
        }

        holder.rvChildMultiHopSeats.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        holder.tvHeader.text = "${item.seat_details?.size} Seats in this PNR"

        holder.rvChildMultiHopSeats.adapter = multiHopSeatSamePnrGroupChildAdapter
        setAdapterCallback.invoke(position, multiHopSeatSamePnrGroupChildAdapter)

        if(item.isPNRGroupSelected == true) {
            holder.rvChildMultiHopSeats.setBackgroundResource(R.drawable.dotted_border)
        } else {
            holder.rvChildMultiHopSeats.setBackgroundResource(R.drawable.transparent_background)
        }
    }

    class ViewHolder(binding: ChildMultiHopSeatSamePnrGroupParentBinding) :
    RecyclerView.ViewHolder(binding.root) {
        /*val seatLayout = binding.seatLayout
        val imageView = binding.imageView
        val tvTop = binding.tvTop
        val tvPNR = binding.tvPNR
        val tvSourceDestination = binding.tvSourceDestination*/
        val rootLayout = binding.rootLayout
        val tvHeader = binding.tvHeader
        val rvChildMultiHopSeats = binding.rvChildMultiHopSeats

    }
}