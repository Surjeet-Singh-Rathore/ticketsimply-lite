package com.bitla.ts.presentation.adapter.tripCollection

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildTripCollectionBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.collection_details.trip_collection.BranchBooking
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat

class ChildTripCollectionAdapter(
    private val context: Context,
    private var dataList: ArrayList<BranchBooking>,
    private val privileges: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<ChildTripCollectionAdapter.ViewHolder>(), OnItemClickListener {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildTripCollectionBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tripDetailsCollectionData=dataList[position]
        holder.nameTV.text=tripDetailsCollectionData.branchName
        holder.seatCount.text=tripDetailsCollectionData.totalSeats.toString()

        val  currencyFormat = getCurrencyFormat(context, privileges?.currencyFormat)
        if (privileges?.currency?.isNotEmpty()==true) {
            holder.seat.text = "${privileges?.currency} ${
                (tripDetailsCollectionData.totalAmount)?.toDouble()?.convert(currencyFormat)
            }"
        } else {
            holder.seat.text = (tripDetailsCollectionData.totalAmount)?.toDouble()?.convert(currencyFormat)

        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val collectionChildAdapter = ChildTripCollectionDetailsAdapter(context = context, passengerDetail =  tripDetailsCollectionData.passengerDetails!!,privileges)
        holder.rvNestedItems.layoutManager = layoutManager
        holder.rvNestedItems.adapter = collectionChildAdapter
    }


    class ViewHolder(binding: ChildTripCollectionBinding) : RecyclerView.ViewHolder(binding.root) {
        var nameTV=binding.tvAgentName
        var seatCount=binding.seatCount
        var seat=binding.seatAmount
        var rvNestedItems=binding.rvNestedItems

    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }
}