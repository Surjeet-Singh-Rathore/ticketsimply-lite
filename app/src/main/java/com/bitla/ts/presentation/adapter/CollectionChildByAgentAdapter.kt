package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildCollectionSublistBinding
import com.bitla.ts.databinding.ChildCollectionSublistBinding.inflate
import com.bitla.ts.domain.pojo.collection_details.PassengerDetail
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import gone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import toast
import visible

class CollectionChildByAgentAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var passengerDetail: ArrayList<PassengerDetail>,
    private val privileges: PrivilegeResponseModel?,
    private var tripSheetCollectionOptionsInTSAppReservationChart: Boolean,
) : RecyclerView.Adapter<CollectionChildByAgentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerDetail.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel = passengerDetail[position]

        holder.tvname.text = searchModel.booked_by
        holder.tvseatNumber.text = searchModel.seat_numbers

        if (privileges != null && !privileges.currency.isNullOrEmpty()) {
            val currencyFormat = privileges.currencyFormat ?: context.getString(R.string.indian_currency_format)

            // conversion off the main thread
            CoroutineScope(Dispatchers.IO).launch {
                val convertedAmount = searchModel.amount?.convert(currencyFormat) ?: 0.0
                withContext(Dispatchers.Main) {
                    holder.tvFare.text = "${privileges.currency} $convertedAmount"
                }
            }
        } else {
            context.toast(context.getString(R.string.server_error))
        }
        holder.tvname.text = searchModel.booked_by
        holder.tvseatNumber.text = searchModel.seat_numbers
        if(tripSheetCollectionOptionsInTSAppReservationChart && !searchModel.from_to.isNullOrEmpty()) {
            holder.fromTo.visible()
            holder.fromTo.text = searchModel.from_to
//            adjustLayoutWeights(holder, true)
        } else {
            holder.fromTo.gone()
//            adjustLayoutWeights(holder, false)
        }

    }

    private fun adjustLayoutWeights(holder: ViewHolder, isFromToVisible: Boolean) {
        val parentLayout = holder.tvFare.parent as LinearLayout
        parentLayout.weightSum = if (isFromToVisible) 5f else 4f

        updateItemWeight(holder.tvFare, 1f)
        updateItemWeight(holder.tvname, 2f)
        updateItemWeight(holder.tvseatNumber, 1f)
    }

    private fun updateItemWeight(view: View, weight: Float) {
        val params = view.layoutParams as LinearLayout.LayoutParams
        params.weight = weight
        view.layoutParams = params
    }

    class ViewHolder(binding: ChildCollectionSublistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tvname = binding.passengerName
        val tvFare = binding.seatFare
        val tvseatNumber = binding.seatNumber
        val fromTo = binding.fromTo
    }
}