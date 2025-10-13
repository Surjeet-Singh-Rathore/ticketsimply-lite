package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildBranchCollectionBookingCancellationReportBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.branch_collection_detailed_report_data.Cancellation
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.sharedPref.PreferenceUtils


class BranchCollectionCancellationAdapter(
    private val context: Context,
    private var cancellationDataList: ArrayList<Cancellation>?,
    val privilegeResponseModel: PrivilegeResponseModel,
) :
    RecyclerView.Adapter<BranchCollectionCancellationAdapter.ViewHolder>() {
    private var TAG: String = BranchCollectionCancellationAdapter::class.java.simpleName
    private var currency: String = ""
    private var currencyFormat: String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ChildBranchCollectionBookingCancellationReportBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return cancellationDataList!!.size

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (privilegeResponseModel != null) {
            currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(context, privilegeResponseModel.currencyFormat)
        }

        val data = cancellationDataList?.get(position)

        try {
            holder.ticketNumber.text = ": ${data?.ticketNumber}"
            holder.bookedOn.text = data?.bookedOn
            holder.noOfSeats.text = data?.noOfSeats
            holder.travelDate.text = data?.travelDate
            holder.bookedBy.text = data?.bookedBy
            holder.totalAmount.text = currency + data?.refundAmount?.convert(currencyFormat)
            holder.refundAmount.text = context.getString(R.string.refund_amount)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    class ViewHolder(binding: ChildBranchCollectionBookingCancellationReportBinding) : RecyclerView.ViewHolder(binding.root) {
        val ticketNumber = binding.ticketNumberValueTV
        val bookedOn = binding.seatCountValueTV
        val noOfSeats = binding.bookingAmountValueTV
        val travelDate = binding.travelDateValueTV
        val bookedBy = binding.bookedByValueTV
        val totalAmount = binding.totalAmountValueTV
        val refundAmount = binding.totalAmountTV

    }
}