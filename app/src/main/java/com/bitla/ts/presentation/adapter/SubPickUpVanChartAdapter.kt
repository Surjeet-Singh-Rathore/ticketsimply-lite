package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.SubPickupVanChartListChildBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.view_reservation.PnrGroup
import com.bitla.ts.utils.common.richText
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import visible
import java.util.Locale

class SubPickUpVanChartAdapter(
    private val context: Context,
    private var role: String,
    private var pnrGroupList: List<PnrGroup?>,
    private val privilegeResponse: PrivilegeResponseModel?,
    private var listener: DialogButtonAnyDataListener,
    private val boardedSwitchAction: ((switchView: SwitchCompat, status: TextView, seatNumber: String?, pnr: String, name: String, dialogBox: Boolean) -> Unit),
) : RecyclerView.Adapter<SubPickUpVanChartAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    private var loginModelPref: LoginModel = LoginModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SubPickupVanChartListChildBinding.inflate(LayoutInflater.from(context), parent, false)
        loginModelPref = PreferenceUtils.getLogin()
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pnrGroupList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pnrGroup: PnrGroup? = pnrGroupList[position]
        val pnrNumber = pnrGroup?.pnr_number?.split(" ")?.get(0) ?: ""

        if (!pnrGroup?.passenger_details.isNullOrEmpty()) {
            pnrGroup?.passenger_details?.forEach {
                if (it?.is_phone_booking == true) {
                    holder.isPhoneBooking.visible()
                } else {
                    holder.isPhoneBooking.gone()
                }
            }
        } else {
            holder.isPhoneBooking.gone()
        }

        if (pnrGroup?.remarks?.isNotEmpty() == true) {
            holder.remarksText.text = pnrGroup.remarks
        } else {
            holder.remarksLayout.gone()
        }

        if (pnrGroup?.remarks.equals("Via-Mobility App", true)) {
            holder.remarksLayout.gone()
        }

        val pnrInfo =
            richText(normalText = "${context.getString(R.string.pnr)}:", boldText = pnrNumber)
        holder.pnrNumberText.text = pnrInfo

        val currency = privilegeResponse?.currency ?: ""
        val bookedBy = pnrGroup?.booked_by?.lowercase(Locale.getDefault()) ?: ""

        val operatorIconResId = when {
            bookedBy.contains("redbus") -> R.drawable.ic_red_bus_test
            bookedBy.contains("easybook") -> R.drawable.easybook
            bookedBy.contains("travel oka") -> R.drawable.traveloka
            bookedBy.contains("bookonlineticket") -> R.drawable.bookticket
            else -> 0 // No operator icon
        }

        holder.bookedByOperatorIcon.apply {
            if (operatorIconResId != 0) {
                visible()
                setImageResource(operatorIconResId)
            } else {
                gone()
            }
        }

        val bookedByInfoText = richText(
            normalText = "${context.getString(R.string.booked_by)}:",
            boldText = pnrGroup?.booked_by?.substringBefore(",") ?: ""
        )
        holder.bookedByInfo.text = bookedByInfoText

        when {
            operatorIconResId != 0 -> {
                val amountInfoText = richText(
                    normalText = ":",
                    boldText = currency + (pnrGroup?.total_net_fare ?: "0")
                )
                holder.amountInfo.text = amountInfoText
            }

            else -> {
                val amountInfoText = richText(
                    normalText = context.getString(R.string.net_amt),
                    boldText = currency + (pnrGroup?.total_net_fare ?: "0")
                )
                holder.amountInfo.text = amountInfoText
            }
        }

        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val vanPassengerListAdapter = VanPassengerListAdapter(
            context,
            role,
            pnrGroup?.passenger_details ?: arrayListOf(),
            privilegeResponse,
            listener,
            boardedSwitchAction = { switchView: SwitchCompat, status: TextView, seatNumber: String?, pnr: String, name: String, dialogBox: Boolean ->
                boardedSwitchAction.invoke(
                    switchView,
                    status,
                    seatNumber,
                    pnr,
                    name,
                    dialogBox
                )
            }
        )
        holder.rvList.layoutManager = layoutManager
        holder.rvList.adapter = vanPassengerListAdapter
        holder.rvList.setRecycledViewPool(viewPool)
    }


    class ViewHolder(binding: SubPickupVanChartListChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val rvList = binding.pnrListRv
        val pnrNumberText = binding.pnrNumber
        val remarksText = binding.remarksText
        val remarksLayout = binding.remarksLayout
        val isPhoneBooking = binding.isPhoneBookImg
        val bookedByOperatorIcon = binding.bookedByOperatorIcon
        val bookedByInfo = binding.bookedByInfo
        val amountInfo = binding.amountInfo
    }
}