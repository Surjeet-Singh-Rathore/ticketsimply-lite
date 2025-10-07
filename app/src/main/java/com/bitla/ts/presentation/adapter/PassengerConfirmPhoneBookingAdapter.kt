package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity.PrivilegeManager.getPrivilegeBase
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.databinding.AdapterPassengerDetailsConfirmPhoneBlockBinding
import com.bitla.ts.domain.pojo.passenger_details_result.PassengerDetailsResult
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import gone
import onChange
import visible

class PassengerConfirmPhoneBookingAdapter(
    private val context: Context,
    val passengerList: ArrayList<PassengerDetailsResult>,
    val clickListener: DialogAnyClickListener
) :
    RecyclerView.Adapter<PassengerConfirmPhoneBookingAdapter.ViewHolder>() {
    private var privileges: PrivilegeResponseModel? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterPassengerDetailsConfirmPhoneBlockBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = passengerList[position]


        privileges = getPrivilegeBase(context)


        if(privileges?.updateTicketUpdationOfFareForPhoneBlockedTickets == true){
            holder.fare.isEnabled = true
        }else{
            holder.fare.isEnabled = false

        }




        holder.name.setText(data.name)
        holder.contact.setText(data.contactDetail[0].cusMobileNumber)
        holder.fare.setText(data.fare)

        if(data.sex.equals("m",true)){
            holder.gender.setText(context.getString(R.string.genderM))
        }else{
            holder.gender.setText(context.getString(R.string.genderF))

        }

        holder.gender.setOnClickListener {
            clickListener.onAnyClickListener(1,holder.gender,position)
        }
        holder.layoutGender.setEndIconOnClickListener {
            clickListener.onAnyClickListener(1,holder.gender,position)
        }


        holder.copyDetailCB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                copyDetailsToAllSeats(data)
            }
        }


        holder.contact.onChange { text ->
            data.contactDetail[0].cusMobileNumber = text
        }

        holder.name.onChange { text ->
            data.name = text
        }

        holder.fare.onChange { text ->
            data.fare = text
        }



        holder.expandIcon.setOnClickListener {
            if(holder.mainGroup.isVisible){
                holder.mainGroup.gone()
            }else{
                holder.mainGroup.visible()
            }
        }

        if(position == 0){
            if(passengerList.size > 1) {
                holder.copyDetailCB.visible()
            }else{
                holder.copyDetailCB.gone()

            }
        }else{
            holder.copyDetailCB.gone()

        }

        holder.seatTitle.setText(context.getString(R.string.seat) + " " + data.seatNumber)



    }

    class ViewHolder(binding: AdapterPassengerDetailsConfirmPhoneBlockBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.nameET
        val gender = binding.genderET
        val layoutGender = binding.layoutGender
        val contact = binding.contactET
        val fare = binding.fareET
        val expandIcon = binding.expandIcon
        val mainGroup = binding.mainG
        val copyDetailCB = binding.copyDetailsCheck
        val seatTitle = binding.seatTitle

    }

    private fun copyDetailsToAllSeats(source: PassengerDetailsResult) {
        for (i in 1 until passengerList.size) {
            passengerList[i].sex = source.sex
            passengerList[i].name = source.name
            passengerList[i].contactDetail[0].cusMobileNumber = source.contactDetail[0].cusMobileNumber
            passengerList[i].fare = source.fare
        }
        notifyDataSetChanged()
    }
}