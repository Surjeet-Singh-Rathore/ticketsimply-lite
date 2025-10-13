package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildServiceWiseCityPickupClosureReportBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.service_wise_city_pickup_report_data.ServiceWiseCityPickupData

class ServiceWiseCityPickupClosureReportAdapter(
    private val context: Context,
    private var dataList: ArrayList<ServiceWiseCityPickupData>
) : RecyclerView.Adapter<ServiceWiseCityPickupClosureReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildServiceWiseCityPickupClosureReportBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        val notAvailable = holder.itemView.context.getString(R.string.notAvailable)
        holder.serviceNameTV.text = "${data.origin ?: ""} to ${data.destination ?: ""}".trim()?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.serviceNameValueTV.text = data.serviceNo?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.vehicleNoValueTV.text = data.vehicleNumber?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.driver1ValueTV.text = data.driver1?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.driver2ValueTV.text = data.driver2?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.totalSeatsBookedValueTV.text = data.totalSeatsBooked?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.boardedValueTV.text = data.boarded.toString()?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.yetToBoardValueTV.text = data.yetToBoard?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.unBoardedValueTV.text = data.unBoarded?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.noShowValueTV.text = data.noShow?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.missingValueTV.text = data.missing?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.availableSeatsValueTV.text = data.availableSeats?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.totalNumberOfCustomerTravellingValueTV.text = data.totalNumberOfCustomerTravelling.toString()?.takeIf { it.isNotEmpty() } ?: notAvailable
        holder.cityPickupClosedByValueTV.text = data.cityPickupClosedBy?.takeIf { it.isNotEmpty() } ?: notAvailable
    }

    class ViewHolder(binding: ChildServiceWiseCityPickupClosureReportBinding) : RecyclerView.ViewHolder(binding.root) {
        val serviceNameTV = binding.serviceNumberTV
        val serviceNameValueTV = binding.serviceNameTVValueTV
        val vehicleNoValueTV = binding.vehicleNumberValueTV
        val driver1ValueTV = binding.driver1ValueTV
        val driver2ValueTV = binding.driver2ValueTV
        val totalSeatsBookedValueTV = binding.totalSeatsBookedValueTV
        val boardedValueTV = binding.boardedValueTV
        val yetToBoardValueTV = binding.yetToBoardValueTV
        val unBoardedValueTV = binding.unboardedValueTV
        val noShowValueTV = binding.noShowValueTV
        val missingValueTV = binding.missingValueTV
        val availableSeatsValueTV = binding.availableSeatsValueTV
        val totalNumberOfCustomerTravellingValueTV = binding.noOfPassengerTravellingValueTV
        val cityPickupClosedByValueTV = binding.pickupClosedByValueTV
    }
}