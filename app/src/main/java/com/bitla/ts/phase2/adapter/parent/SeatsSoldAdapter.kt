package com.bitla.ts.phase2.adapter.parent

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.AdapterSeatsSoldBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.revenue_data.SeatSold
import com.bitla.ts.utils.common.convert

class SeatsSoldAdapter(
    private val context: Context,
    private val seatSold: ArrayList<SeatSold>?,
    private val privilegeResponse: PrivilegeResponseModel?,
) :
    RecyclerView.Adapter<SeatsSoldAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterSeatsSoldBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return seatSold?.size!!
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = seatSold!![position]
        holder.apply {
            serviceName.text = data.serviceName
            seatTV.text = data.seats.toString()
            fareTV.text = privilegeResponse?.currency + data.fare?.toDouble()?.convert(
                privilegeResponse?.currencyFormat
                    ?: context.getString(R.string.indian_currency_format))
        }

    }

    class ViewHolder(binding: AdapterSeatsSoldBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val serviceName = binding.nameTV
        val seatTV = binding.seatTV
        val fareTV = binding.fareTV

    }
}