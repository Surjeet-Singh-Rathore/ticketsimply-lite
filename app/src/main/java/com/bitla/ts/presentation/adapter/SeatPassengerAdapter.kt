package com.bitla.ts.presentation.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.AdapterPassengerNameBinding
import com.bitla.ts.domain.pojo.multistation_data.PassengerDetail
import com.bitla.ts.presentation.view.activity.CoachLayoutReportingActivity
import com.bitla.ts.presentation.view.activity.NewCoachActivity

class SeatPassengerAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var passengerList: ArrayList<PassengerDetail>?,
    var lastSelectedSeatPosition: Int?,
    var activity: Activity


) :
    RecyclerView.Adapter<SeatPassengerAdapter.ViewHolder>() {
    private var TAG: String = SeatPassengerAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val binding =
            AdapterPassengerNameBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = passengerList!![position].ticket_no.substringBefore("(")
        holder.name.setOnClickListener {
            onItemClickListener.onClick(holder.rootId,position)
        }

        if(lastSelectedSeatPosition == position){
            if (activity is NewCoachActivity) {
                (activity as NewCoachActivity).setSeatBookingDetails(lastSelectedSeatPosition ?: 0)
            } else if(activity is CoachLayoutReportingActivity) {
                (activity as CoachLayoutReportingActivity).setSeatBookingDetails(lastSelectedSeatPosition ?: 0)
            }
            holder.name.background = ContextCompat.getDrawable(context, R.drawable.bg_selected_blue_round)
            holder.name.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
        }else{
            holder.name.background = ContextCompat.getDrawable(context, R.drawable.circle_shape_grey_stroke)
            holder.name.setTextColor(ContextCompat.getColor(context,R.color.gray))
        }

    }


    class ViewHolder(binding: AdapterPassengerNameBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.name
        val rootId = binding.passengerNameRT
    }

    fun updateList(list : ArrayList<PassengerDetail>,lastPos : Int?= 0){
        lastSelectedSeatPosition = lastPos
        passengerList = list
        notifyDataSetChanged()
    }
}