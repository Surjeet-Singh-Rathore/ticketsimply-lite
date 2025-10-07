package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.MapChildListBinding
import com.bitla.ts.domain.pojo.BpDpService.response.PassengerDetail
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import gone
import visible

class MapListAdapter(
    private val context: Context,
    private var searchList: MutableList<PassengerDetail>,
    private var onItemPassData: OnItemPassData,
    private var privilegeResponse: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<MapListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MapChildListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val searchModel: com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail? =
//            searchList[position]
        val seat = arrayListOf<String>()
        holder.passsengerSeatNumber.text = searchList[position].seat_number
        if (searchList[position].title.equals("mr", true)) {
            holder.passengerSex.text = context.getString(R.string.male)
        } else {
            holder.passengerSex.text = context.getString(R.string.female)
        }
        holder.passName.text = searchList[position].name
        holder.bookedBy.text =
            "${context.getString(R.string.booked_by)} :${searchList[position].booked_by}"



        if (searchList[position].status == 2) {
            holder.switch.tag = "1"
            holder.switch.isChecked = true
        } else {
            holder.switch.isChecked = false
            holder.switch.tag = "2"
        }
        if(privilegeResponse?.updatePassengerTravelStatus == true) {
            holder.switch.visible()
        } else {
            holder.switch.gone()
        }
        holder.switch.setOnClickListener {

            holder.switch.isChecked = searchList[position].switch
            onItemPassData.onItemDataMore(
                holder.switch,
                searchList[position].name ?: "",
                searchList[position].pnr_number,
                searchList[position].seat_number ?: ""
            )
        }

//        loginUser = PreferenceUtils.getObject<LoginModel>(PREF_LOGGED_IN_USER)!!


//        val status = searchModel!!.boardingStatus?.lowercase(Locale.getDefault())
//        Timber.d("status-status: ${status}")


    }


    class ViewHolder(binding: MapChildListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val passName = binding.passengerName
        val passsengerSeatNumber = binding.passengerSeat
        val passengerSex = binding.passengerSex
        val bookedBy = binding.passengerBookedBy
        val switch = binding.switch1

    }
}