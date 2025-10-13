package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.BuildConfig
import com.bitla.ts.databinding.ChildBookedByYouReportBinding
import com.bitla.ts.databinding.ChildBranchCollectionSummaryReportBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.Data
import com.bitla.ts.domain.pojo.all_reports.new_response.occupany_report_data.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class NewOccupancyReportAdapter(
    private val context: Context,
    private var dataListList: ArrayList<Result>

) :
    RecyclerView.Adapter<NewOccupancyReportAdapter.ViewHolder>() {
    private var TAG: String = NewOccupancyReportAdapter::class.java.simpleName
    private var currency: String = ""
    private var currencyFormat: String = ""
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ChildBranchCollectionSummaryReportBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataListList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        val data = dataListList[position]

        holder.totalSeats.text = "Total seats"
        holder.primeBooking.text = "Prime bookings"
        holder.viaBooking.text = "Via bookings"
        holder.totalCapacity.text = "Total Capacity"


        try {
            holder.totalSeatsValue.text = data.totalSeats.toString()
            holder.primeBookingValue.text = data.totalPrimeBookings.toString()
            holder.viaBookingValue.text = data.totalViaBookings.toString()
            holder.totalCapacityValue.text = data.totalBookings.toString()
            holder.serviceName.text = data.routeNumber
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    class ViewHolder(binding: ChildBranchCollectionSummaryReportBinding) : RecyclerView.ViewHolder(binding.root) {
        val totalSeats = binding.seatCountTV
        val primeBooking = binding.bookingAmountTV
        val viaBooking = binding.cancellationAmountTV
        val totalCapacity = binding.totalAmountTV
        val serviceName = binding.serviceNumberTV

        val totalSeatsValue = binding.seatCountValueTV
        val primeBookingValue = binding.bookingAmountValueTV
        val viaBookingValue = binding.cancellationAmountValueTV
        val totalCapacityValue = binding.totalAmountValueTV
    }
}