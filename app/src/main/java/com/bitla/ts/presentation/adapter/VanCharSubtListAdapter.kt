package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.PickupVanSubChildBinding
import com.bitla.ts.domain.pojo.pickUpVanChart.PassengerList
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import gone
import toast
import visible
import java.util.*


class VanCharSubtListAdapter(
    private val context: Context,
    private var role: String,
    private val searchList:ArrayList<PassengerList>,
    private val boardedClick:((switchView: SwitchCompat?,status: TextView?, seatNumber:String?, pnr: String, name: String , dialogBox:Boolean ) -> Unit),
    private var listener: DialogButtonAnyDataListener
) :
    RecyclerView.Adapter<VanCharSubtListAdapter.ViewHolder>()
    {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PickupVanSubChildBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size

    }

    @SuppressLint("RtlHardcoded", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(role == context.getString(R.string.role_agent)) {
            holder.apply {
                boardedLayout.gone()
                statusLL.gone()
                val callParams = callPassenger.layoutParams as LinearLayout.LayoutParams
                callParams.weight = 3f
                callPassenger.layoutParams = callParams
            }
        } else {
            holder.apply {
                boardedLayout.visible()
                statusLL.visible()

                val callParams = callPassenger.layoutParams as LinearLayout.LayoutParams
                callParams.weight = 0.9f
                callPassenger.layoutParams = callParams

                val statusParams = statusLL.layoutParams as LinearLayout.LayoutParams
                statusParams.weight = 0.9f
                statusLL.layoutParams = statusParams

                val boardedParams = boardedLayout.layoutParams as LinearLayout.LayoutParams
                boardedParams.weight = 1.2f
                boardedLayout.layoutParams = boardedParams
            }
        }

        val item: PassengerList= searchList[position]

        when (item.status){
            "7"->{
                holder.status.text= context.getString(R.string.boarded_status)
            }
            "6"->{
                holder.status.text= context.getString(R.string.yet_to_board)
            }
            "8"->{
                holder.status.text= context.getString(R.string.no_show)
            }
        }
        holder.passengerName.text= item.passenger_name.replace("\"", "")
        holder.pnrInfo.text= item.pnr_number.replace("\"", "").substringBefore("(")
        holder.seatNumber.text= item.seat_number.replace("\"", "")
        holder.boardedSwitch.isChecked= item.status== "7"
        val switchCheck= holder.boardedSwitch.isChecked

        holder.boardedSwitch.setOnClickListener {
            if (holder.status.text== context.getString(R.string.boarded_status)){
                holder.boardedSwitch.isChecked= true
                context.toast(context.getString(R.string.alreadyBoarded))
            }else{
                holder.boardedSwitch.isChecked= false
                boardedClick.invoke(holder.boardedSwitch,holder.status,item.seat_number, item.pnr_number.split(" ").get(0),item.passenger_name , false)
            }
        }
        holder.statusBtn.setOnClickListener {
            boardedClick.invoke(holder.boardedSwitch,holder.status,item.seat_number, item.pnr_number.split(" ").get(0),item.passenger_name , true)

        }
        holder.call.setOnClickListener {
            listener.onDataSend(1,item.phone_number)

        }
    }


    inner class ViewHolder(binding: PickupVanSubChildBinding) :
        RecyclerView.ViewHolder(binding.root) {


        val seatNumber = binding.seatNumber
        val pnrInfo = binding.pnrNumber
        val passengerName = binding.passName
        val call = binding.callPassenger
        val boardedSwitch = binding.boardedSwitch
        val statusBtn = binding.StatusChange
        val status = binding.yetToBoard
        val boardedLayout = binding.boardedLayout
        val statusLL = binding.status
        val callPassenger = binding.callPassenger
    }

}