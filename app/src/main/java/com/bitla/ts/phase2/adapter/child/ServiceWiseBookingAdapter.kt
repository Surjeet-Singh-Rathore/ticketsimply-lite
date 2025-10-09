package com.bitla.ts.phase2.adapter.child

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildServiceWiseBookingBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.service_wise_booking_model.response.ServiceWiseBooking
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class ServiceWiseBookingAdapter(
    private val context: Context,
    private var serviceWiseBookingModel: MutableList<ServiceWiseBooking>,
    private var privilegeResponseModel: PrivilegeResponseModel
) :
    RecyclerView.Adapter<ServiceWiseBookingAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildServiceWiseBookingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return serviceWiseBookingModel.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val serviceWiseBookingData: ServiceWiseBooking = serviceWiseBookingModel[position]

        if (serviceWiseBookingData.occupancy.toString().toInt() <= 40) {
            holder.rectangleImage.setBackgroundResource(R.drawable.ic_rectangle_cancelled)
            holder.tvOccupancyValue.setTextColor(ContextCompat.getColor(context, R.color.colorRed))

        } else {
            holder.rectangleImage.setBackgroundResource(R.drawable.ic_rectangle_active)
        }

        val currency = privilegeResponseModel?.currency
        val currencyFormat = privilegeResponseModel?.currencyFormat
        var newGrossValue =
            (serviceWiseBookingData.grossRevenue ?: "0.0").toString().replace(currency ?: "", "")
        var newNetRev = serviceWiseBookingData.revenue.toString().replace(currency ?: "", "")

        newGrossValue =
            newGrossValue.toDouble().convert(currencyFormat ?: context.getString(R.string.indian_currency_format))
        newNetRev = newNetRev.toDouble().convert(currencyFormat ?: context.getString(R.string.indian_currency_format))


        holder.tvOccupancyValue.text = "${serviceWiseBookingData.occupancy}%"
        holder.tvSeatsSoldValue.text = serviceWiseBookingData.seatsSold
        holder.tvGrossAmountValue.text = (currency ?: "") + newGrossValue
        holder.tvNetAmountValue.text = (currency ?: "") + newNetRev
        holder.tvServiceName.text = serviceWiseBookingData.service.toString()

        if (serviceWiseBookingData.occupancy?.toDouble()!! <= 30.0) {

            holder.tvOccupancyValue.setTextColor(context.resources.getColor(R.color.colorRed2))

        } else if (serviceWiseBookingData.occupancy.toDouble() in 30.1..50.0) {

            holder.tvOccupancyValue.setTextColor(context.resources.getColor(R.color.lightest_yellow))

        } else if (serviceWiseBookingData.occupancy.toDouble() in 50.1..70.0) {

            holder.tvOccupancyValue.setTextColor(context.resources.getColor(R.color.color_03_review_02_moderate))

        } else if (serviceWiseBookingData.occupancy.toDouble() >= 70.1) {

            holder.tvOccupancyValue.setTextColor(context.resources.getColor(R.color.booked_tickets))

        }
    }

    fun addNewData(_serviceWiseBookingModel: MutableList<ServiceWiseBooking>){
        serviceWiseBookingModel.addAll(_serviceWiseBookingModel)
        notifyDataSetChanged()
    }
    class ViewHolder(binding: ChildServiceWiseBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val rectangleImage = binding.rectangleImage
        val tvOccupancyValue = binding.tvOccupancyValue
        val tvSeatsSoldValue = binding.tvSeatsSoldValue
        val tvGrossAmountValue = binding.tvGrossAmountValue
        val tvNetAmountValue = binding.tvNetAmountValue
        val tvServiceName = binding.serviceName
    }
}