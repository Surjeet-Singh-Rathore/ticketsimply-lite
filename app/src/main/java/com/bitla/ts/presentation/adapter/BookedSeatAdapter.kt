package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildBookedSeatsBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details_response.PassengerDetails
import gone
import toast
import visible

class BookedSeatAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var passengerDetailsList: List<PassengerDetails>,
    val privileges: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<BookedSeatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildBookedSeatsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerDetailsList.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (privileges != null) {


            if (privileges?.bulkUpdationOfTickets == null) {
                holder.tvEdit.gone()
            } else {
                if (privileges.bulkUpdationOfTickets) {
                    holder.tvEdit.visible()
                } else {
                    holder.tvEdit.gone()
                }
            }
        } else {
            context.toast(context.getString(R.string.server_error))
        }


        val passengerDetails: PassengerDetails = passengerDetailsList[position]

        if (passengerDetails.ticketNo != null && passengerDetails.ticketNo!!.contains(" ")) {
            val ticketNo = passengerDetails.ticketNo!!.substringBefore(" ").trim()
            holder.tvPnr.text = ticketNo
        } else
            holder.tvPnr.text = passengerDetails.ticketNo
        holder.tvName.text = passengerDetails.name
        holder.tvSeatNo.text = passengerDetails.seatNo

        holder.tvEdit.setOnClickListener {
            holder.tvEdit.tag = context.getString(R.string.edit)
            onItemClickListener.onClick(holder.tvEdit, position)
        }
    }

    class ViewHolder(binding: ChildBookedSeatsBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvPnr = binding.tvPnr
        val tvEdit = binding.tvEdit
        val tvName = binding.tvName
        val tvSeatNo = binding.tvSeatNo
    }
}