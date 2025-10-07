package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.PickupVanListChildBinding
import com.bitla.ts.domain.pojo.pickUpVanChart.PassengerList
import com.google.gson.JsonElement

class VanChartListAdapter(
    private val context: Context,
    private var role: String,
    private var searchList:JsonElement,
    private var keySet:kotlin.collections.List<String>,
    private val boardedClick:((switchView: SwitchCompat?,status: TextView?, seatNumber:String?, pnr: String, name: String , dialogBox:Boolean ) -> Unit),
    private val listener: DialogButtonAnyDataListener

) :
    RecyclerView.Adapter<VanChartListAdapter.ViewHolder>(), DialogButtonAnyDataListener {
        private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PickupVanListChildBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return keySet.size

    }

    @SuppressLint("RtlHardcoded", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val passengerList: ArrayList<PassengerList> = arrayListOf()

        holder.stationname.text= keySet[position]

        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val searchModel=searchList.asJsonObject
        val list= searchModel.get(keySet[position]).asJsonArray
        for (i in 0.. list.size().minus(1)){

                passengerList.add(PassengerList(
                    pnr_number = list[i].asJsonObject.get("pnr_number").toString(),
                    seat_number = list[i].asJsonObject.get("seat_number").toString(),
                    passenger_name = list[i].asJsonObject.get("passenger_name").toString(),
                    age = list[i].asJsonObject.get("age").toString().toIntOrNull(),
                    sex = list[i].asJsonObject.get("sex").toString(),
                    phone_number = list[i].asJsonObject.get("phone_number").toString(),
                    stage_dep_time = list[i].asJsonObject.get("stage_dep_time").toString(),
                    stage_name = list[i].asJsonObject.get("stage_name").toString(),
                    status = list[i].asJsonObject.get("status").toString(),
                ))
        }
        holder.count.text= "${context.getString(R.string.count)} ${passengerList.size.toString()}"
        val childSortSublistAdapter = VanCharSubtListAdapter(
            context, role,passengerList, boardedClick = {switchView, status, seatNumber, pnr, name, dialogBox ->
                boardedClick.invoke(switchView, status,seatNumber, pnr, name, dialogBox)
            },this
        )
        layoutManager.initialPrefetchItemCount = passengerList.size
        holder.rvNested.layoutManager = layoutManager
        holder.rvNested.adapter = childSortSublistAdapter
        holder.rvNested.setRecycledViewPool(viewPool)
//




    }
    class ViewHolder(binding: PickupVanListChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val stationname = binding.stationName
        val count = binding.countNumber
        val rvNested = binding.passengerListChild

    }

    override fun onDataSend(type: Int, file: Any) {
        val code = type
        listener.onDataSend(code,file)

    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {

    }

}