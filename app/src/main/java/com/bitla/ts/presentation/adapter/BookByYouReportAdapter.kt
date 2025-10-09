package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnPnrListener
import com.bitla.ts.databinding.ChildBookedByYouReportBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.Data
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.sharedPref.PREF_DATE_TYPE_ISSUE_TRAVEL_DATE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import visible


class BookByYouReportAdapter(
    private val context: Context,
    private var dataListList: List<Data>,
    private val onPnrListener: OnPnrListener,
    private val isPaymentReport: Boolean = false,
    val privilegeResponseModel: PrivilegeResponseModel?

) :
    RecyclerView.Adapter<BookByYouReportAdapter.ViewHolder>() {
    private var TAG: String = BookByYouReportAdapter::class.java.simpleName
    private var currency: String = ""
    private var currencyFormat: String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ChildBookedByYouReportBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataListList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (privilegeResponseModel != null) {
            currency = privilegeResponseModel?.currency?:""
            currencyFormat = getCurrencyFormat(context, privilegeResponseModel?.currencyFormat)
        }

        val data = dataListList[position]

        try {
            holder.pnrNumberTV.text = data.pnrNumber
            holder.busDetailsTV.text = "| ${data.route}"
            holder.routeNumberTV.text = data.routeNumber

            if(privilegeResponseModel?.country.equals("India",true))
            {  holder.travelDateValueTV.text = ": ${data.travelDate}"
                holder.seatNoValueTV.text = ": ${data.seatNumbers}"
                holder.totalFareTV.text = currency + data.totalFare.convert(currencyFormat)}
            else{
                if(isPaymentReport){
                    holder.seatNoValueTV.text = ": "+data.noOfSeats+" ("+data.seatNumbers+")"
                    holder.totalUnpaidHeadTv.visible()
                    holder.totalPaidHeadTv.visible()
                    holder.totalUnpaidValueTv.visible()
                    holder.totalPaidValueTv.visible()
                    holder.totalPaidValueTv.text=" $currency " + data.paidAmount
                        .convert(currencyFormat)

                    holder.totalUnpaidValueTv.text=" $currency " + data.pendingAmount
                        .convert(currencyFormat)

                    holder.totalFareTV.gone()


                }else{
                    holder.seatNoValueTV.text = ": "+data.noOfSeats+" ("+data.seatNumber+")"
                }

                holder.totalFareTV.text = currency +" "+data.totalFare.convert(currencyFormat)

                var dateTyepRadio = PreferenceUtils.getString(PREF_DATE_TYPE_ISSUE_TRAVEL_DATE)
                if(dateTyepRadio.equals("1"))
                {
                    holder.travelDateTV.setText(R.string.issue_date)
                    holder.travelDateValueTV.text = ": ${data.bookedOn}"
                }
                else if(dateTyepRadio.equals("2")){
                    holder.travelDateTV.setText(R.string.travel_date)
                    holder.travelDateValueTV.text = ": ${data.travelDate}"
                }

                holder.pnrNumberTV.setOnClickListener {
                    onPnrListener.onPnrSelection(
                        context.getString(R.string.view_ticket),
                        data.pnrNumber
                    )
                }
            }


           // holder.totalFareTV.text = data.totalFare
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    class ViewHolder(binding: ChildBookedByYouReportBinding) : RecyclerView.ViewHolder(binding.root) {
        val pnrNumberTV = binding.pnrNumberTV
        val busDetailsTV = binding.busDetailsTV
        val routeNumberTV = binding.routeNumberTV
        val seatNoValueTV = binding.seatNoValueTV
        val travelDateValueTV = binding.travelDateValueTV
        val totalFareTV = binding.totalFareTV
        val travelDateTV = binding.travelDateTV
        val totalPaidHeadTv=binding.totalPaidHeadTV
        val totalPaidValueTv=binding.totalPaidValueTV
        val totalUnpaidHeadTv=binding.totalUnPaidHeadTV
        val totalUnpaidValueTv=binding.totalUnPaidValueTV
    }
}