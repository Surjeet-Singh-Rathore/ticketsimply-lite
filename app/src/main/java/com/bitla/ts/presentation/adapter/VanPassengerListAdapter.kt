package com.bitla.ts.presentation.adapter

import android.content.*
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.VanPassengerListChildItemBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetailX
import com.bitla.ts.utils.sharedPref.*
import gone
import toast
import visible

class VanPassengerListAdapter(
    private val context: Context,
    private var role: String,
    private val passengerList: ArrayList<PassengerDetailX?>,
    private val privilegeResponseModel: PrivilegeResponseModel?,
    private var listener: DialogButtonAnyDataListener,
    private val boardedSwitchAction: ((switchView: SwitchCompat, status: TextView, seatNumber: String?, pnr: String, name: String, dialogBox: Boolean) -> Unit),
) : RecyclerView.Adapter<VanPassengerListAdapter.ViewHolder>() {

    private var loginModelPref: LoginModel = LoginModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            VanPassengerListChildItemBinding.inflate(LayoutInflater.from(context), parent, false)
        loginModelPref = PreferenceUtils.getLogin()
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == passengerList.size.minus(1)) {
            holder.dottedLine.gone()
        } else {
            holder.dottedLine.visible()
        }

        if (role == context.getString(R.string.role_agent)) {
            holder.checkBoarded.gone()
            holder.menuOption.gone()
        }

        val passengerItem: PassengerDetailX? = passengerList[position]

        holder.boardingStatus.gone()
        holder.dotIcon.gone()
        when (passengerItem?.status) {
            7 -> {
                holder.boardingStatus.text = context.getString(R.string.boarded_status)
            }

            6 -> {
                holder.boardingStatus.text = context.getString(R.string.yet_to_board)
            }

            8 -> {
                holder.boardingStatus.text = context.getString(R.string.no_show)
            }
        }

        holder.seatNumber.text = passengerItem?.seat_number
        val passengerInfo =
            "${passengerItem?.passenger_name} (${passengerItem?.sex}${passengerItem?.age})"
        holder.passengerName.text = passengerInfo
        holder.checkBoarded.isChecked = passengerItem?.status == 7

        if (passengerItem?.total_trip != null) {
            holder.totalTrips.visible()
            val passengerTripInfo =
                "${passengerItem.total_trip} ${context.getString(R.string.trips)}"
            holder.totalTrips.text = passengerTripInfo
        } else {
            holder.totalTrips.gone()
        }

        if (privilegeResponseModel?.updatePassengerTravelStatus == true) {
            holder.checkBoarded.visible()
        } else {
            holder.checkBoarded.gone()
        }

        holder.checkBoarded.setOnTouchListener(View.OnTouchListener { v, event -> event.actionMasked == MotionEvent.ACTION_MOVE })
        holder.checkBoarded.setOnClickListener {
            if (holder.boardingStatus.text == context.getString(R.string.boarded_status)) {
                holder.checkBoarded.isChecked = true
                context.toast(context.getString(R.string.alreadyBoarded))
            } else {
                holder.checkBoarded.isChecked = false
                holder.checkBoarded.tag = "boarded"

                boardedSwitchAction.invoke(
                    holder.checkBoarded,
                    holder.boardingStatus,
                    passengerItem?.seat_number ?: "",
                    passengerItem?.pnr_number?.split(" ")?.get(0) ?: "",
                    passengerItem?.passenger_name ?: "",
                    false
                )
            }
        }

        holder.callOption.setOnClickListener {
            listener.onDataSend(1, passengerItem?.phone_number ?: "")
        }

        holder.menuOption.setOnClickListener {
            boardedSwitchAction.invoke(
                holder.checkBoarded,
                holder.boardingStatus,
                passengerItem?.seat_number ?: "",
                passengerItem?.pnr_number?.split(" ")?.get(0) ?: "",
                passengerItem?.passenger_name ?: "",
                true
            )
        }
    }


    class ViewHolder(binding: VanPassengerListChildItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkBoarded = binding.boardedSwitch
        val passengerName = binding.passengerName
        val seatNumber = binding.seatNumber
        val totalTrips = binding.totalTrips
        val boardingStatus = binding.passengerStatus
        val dotIcon = binding.dotIcon
        val menuOption = binding.threeDotMenu
        val callOption = binding.callIcon
        val dottedLine = binding.dottedLine
    }
}