package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.BuildConfig
import com.bitla.ts.databinding.AdapterRouteWiseMemoBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.routewise_booking_memo.RoutewiseReportData
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.sharedPref.PreferenceUtils

class RouteWiseBookingMemoAdapter(
    private val context: Context,
    private var routeList: ArrayList<RoutewiseReportData>,
    val privilegeResponseModel: PrivilegeResponseModel


) :
    RecyclerView.Adapter<RouteWiseBookingMemoAdapter.ViewHolder>() {
    private var TAG: String = RouteWiseBookingMemoAdapter::class.java.simpleName
    private var currency: String = ""
    private var currencyFormat: String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            AdapterRouteWiseMemoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (privilegeResponseModel != null) {
            currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(context, privilegeResponseModel.currencyFormat)
        }

        val data = routeList[position]
        try {
            holder.busDetailsTV.text = data.from + " - " + data.to
            holder.rateValueTV.text = currency + data.rate.toDouble().convert(currencyFormat)
            holder.totalSeatValueTV.text = data.noOfSeats
            holder.seatNosValueTV.text = data.seatNumber
            holder.amountValueTV.text = currency + data.amount.toDouble().convert(currencyFormat)
        }catch (e :Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }



    }



    class ViewHolder(binding: AdapterRouteWiseMemoBinding) : RecyclerView.ViewHolder(binding.root) {
        val rateValueTV = binding.rateValueTV
        val amountValueTV = binding.amountValueTV
        val totalSeatValueTV = binding.totalSeatValueTV
        val seatNosValueTV = binding.seatNosValueTV
        val busDetailsTV = binding.busDetailsTV
    }
}