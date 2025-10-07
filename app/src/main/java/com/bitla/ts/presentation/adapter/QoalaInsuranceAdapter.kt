package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildAmenitiesAdapterBinding
import com.bitla.ts.databinding.ChildInsuranceBinding
import com.bitla.ts.domain.pojo.available_routes.BusAmenity
import com.bitla.ts.domain.pojo.ticket_details.response.Detail
import com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail
import com.bumptech.glide.Glide

class QoalaInsuranceAdapter(
    private val context: Context,
    private var detail: List<Detail>
) :
    RecyclerView.Adapter<QoalaInsuranceAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildInsuranceBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return detail.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detail: Detail = detail[position]
        holder.tvSeatNo.text = detail.seat_no.toString()
        holder.tvPolicyNo.text = detail.info.policy_number
        holder.tvBookingCode.text = detail.info.booking_code
    }

    class ViewHolder(binding: ChildInsuranceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvSeatNo = binding.tvSeatNo
        val tvPolicyNo = binding.tvPolicyNo
        val tvBookingCode = binding.tvBookingCode
    }
}