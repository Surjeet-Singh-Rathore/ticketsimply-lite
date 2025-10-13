package com.bitla.ts.phase2.adapter.child

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildPhoneblockedPendingquotaItemBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.phone_blocked_model.response.PassengerDetail
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import visible

class PhoneBlockedAdapter(
    context: Context,
    items: MutableList<PassengerDetail>?,
    var serviceTitle: String,
    val privilegeResponse: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<PhoneBlockedAdapter.ViewHolder>() {

    private val items: MutableList<PassengerDetail>?
    private val context: Context

    init {
        this.items = items
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildPhoneblockedPendingquotaItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if (items != null) {
            return items.count()
        }

        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seatNo = items?.get(position)?.seatNo
        val name = items?.get(position)?.name
        val blockedBy = items?.get(position)?.blockedBy
        val collection = items?.get(position)?.collection
        val dateTime = items?.get(position)?.dateTime
        val pnrNumber = items?.get(position)?.pnrNumber
        val status = items?.get(position)?.status
        val releasedBy = items?.get(position)?.releasedBy
        val isPayAtBus = items?.get(position)?.isPayAtBus

        val seatList = seatNo?.split(",")

        if (seatList?.isNotEmpty() == true) {
            holder.tvSeatNumber.text = seatList[0]

            if (seatList.size > 1) {
                holder.totalSeats.text = "+${(seatList.size - 1)}"
                holder.totalSeats.visible()
                holder.totalSeats.setOnClickListener {
                    DialogUtils.phoneBlockTotalSeatsDialog(
                        context = context,
                        totalSeats = seatList.size,
                        serviceHeader = serviceTitle,
                        seatNos = seatNo
                    )
                }
            }

        } else {
            holder.tvSeatNumber.text = seatNo
        }

        holder.tvName.text = name

        if (blockedBy.isNullOrEmpty() && releasedBy.isNullOrEmpty()) {
            holder.blockedbyLayout.gone()
        } else {
            holder.blockedbyLayout.visible()
        }

        if (status.contentEquals("Pending")) {
            holder.tvBlockedBy.text = "${context.getString(R.string.blocked_by)} : $blockedBy"
        } else if (status.contentEquals("Released")) {
            holder.tvBlockedBy.text = "${context.getString(R.string.released_by)} : $releasedBy"
        }

        if (status.isNullOrEmpty()) {
            holder.statusLayout.gone()
        } else {
            holder.statusLayout.visible()
            holder.tvStatusValue.text = status.toString()
        }

        var newRev = collection.toString().replace(privilegeResponse?.currency ?: "₹", "")
        newRev = newRev.toDouble().convert(privilegeResponse?.currencyFormat ?: context.getString(R.string.indian_currency_format))


        holder.tvCollectionValue.text = "${privilegeResponse?.currency ?: ""}${newRev}"
        holder.tvTime.text = dateTime
        if (!pnrNumber.isNullOrEmpty()) {
            holder.pnrNumberBlock.visible()
            holder.pnrNumber.text = "${context.getString(R.string.pnr)}: $pnrNumber"
        } else {
            holder.pnrNumberBlock.gone()
        }

        if(isPayAtBus == true) {
            holder.payAtBusLayout.visible()
        }
    }

    class ViewHolder(binding: ChildPhoneblockedPendingquotaItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var tvSeatNumber: TextView
        var tvBlockedBy: TextView
        var tvName: TextView
        var tvCollectionValue: TextView
        var tvTime: TextView
        var pnrNumberBlock: RelativeLayout
        var pnrNumber: TextView
        var statusLayout: ConstraintLayout
        var tvStatusValue: TextView
        var blockedbyLayout: RelativeLayout
        var totalSeats: TextView
        var payAtBusLayout: LinearLayout

        init {
            this.tvSeatNumber = binding.tvSeatNumber
            this.tvName = binding.tvName
            this.tvBlockedBy = binding.tvBlockedBy
            this.tvCollectionValue = binding.tvCollectionValue
            this.tvTime = binding.tvTime
            this.pnrNumberBlock = binding.pnrNumberBlock
            this.pnrNumber = binding.pnrNumber
            this.statusLayout = binding.statusLayout
            this.tvStatusValue = binding.tvStatusValue
            this.blockedbyLayout = binding.blockedbyLayout
            this.totalSeats = binding.totalSeats
            this.payAtBusLayout = binding.payAtBusLayout

        }
    }

}