package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.ChildBulkShiftListAdapterBinding
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetail
import timber.log.Timber

class BulkShiftListAdapter(
    private val context: Context,
    private var searchList: List<PassengerDetail>,
    private val onItemClickListener: OnItemClickListener,
    private val onItemPassData: OnItemPassData

) :
    RecyclerView.Adapter<BulkShiftListAdapter.ViewHolder>() {
    //    private var tag: String = ChildStageAdapterBinding::class.java.simpleName
//    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ChildBulkShiftListAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val searchModel: PassengerDetail = searchList.get(position)
//        val oldTravelDate = PreferenceUtils.getString("TicketDetail_Traveldate")!!
//        val newTravelDate = PreferenceUtils.getString("shiftPassenger_selectedDate")!!
//        val oldRouteId = PreferenceUtils.getString("ticketDetails_RouteID")!!.toInt()
        holder.serviceName.setText("${searchModel.boardingCity}-${searchModel.droppingCity}")
        holder.oldSeatNumber.text = searchModel.seatNumber
        holder.passenger_name.text = searchModel.passengerName
//        holder.newSeatNumber.setText(searchModel.passengerDetails!!.newSeat)
        holder.servicePnr.setText(searchModel.pnrNumber)

        var list = arrayListOf<String>()
        var new = ""

        if (holder.newSeatNumber.text.isNullOrEmpty()) {
            holder.newSeatNumber.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    searchList.get(position).newSeat = new
                    Timber.d("adapter124 : $new")


                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    new = holder.newSeatNumber.text.toString()
                    Timber.d("adapter12 : $new")
//                    onItemClickListener.onClickOfItem(new,searchModel.number!!.toInt())
                    onItemPassData.onItemData(holder.newSeatNumber, new, searchModel.seatNumber)
                }
            })
        }


//        if (holder.newSeatNumber.text?.isNotEmpty() == true){
//            val num = holder.newSeatNumber.text?: String
//
//
//        }else {
//            for (i in 0..searchList.size.minus(1)) {
//                val num = holder.newSeatNumber.text ?: String
//                Timber.d("adapter123 : $num")
//
//            }
//        }


//        li
//        var list = arrayListOf<String>()

//        holder.newSeatNumber.setOnClickListener {
////            for(i in 0..searchList.size.minus(1)){
//                if (holder.newSeatNumber.text?.isNotEmpty() == true){
//                    val num = holder.newSeatNumber.text?: String
//                    list.add(num.toString())
//                    Timber.d("adapter12 : $list")
//
//                }else{
//                    for(i in 0..searchList.size.minus(1)){
//                        val num = holder.newSeatNumber.text?: String
//                        Timber.d("adapter123 : $num")
//
//                    }
//                }
////            }
//
//        }


//        Timber.d("adapter12 : $list")
//        holder.newSeatNumber.setOnClickListener {
//            val num = holder.newSeatNumber.text?: String
//
//                if (num == null){
//                    print("hello")
//                }else{
//                    list.add(num.toString())
//                }
//
//            Timber.d("adapter12 : $list")
//
//        }


        holder.serviceName.setOnClickListener {
        }
        //holder.tvDateTime.text = searchModel.name

    }

    class ViewHolder(binding: ChildBulkShiftListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val serviceName = binding.routeOriginDestination
        val servicePnr = binding.pnrNumber
        val oldSeatNumber = binding.oldSeatNumber
        val newSeatNumber = binding.etNewSeatnumber
        val passenger_name = binding.passengerNamer

//init {
//    binding.etNewSeatnumber.setOnClickListener {
////            for(i in 0..searchList.size.minus(1)){
//        if (holder.newSeatNumber.text?.isNotEmpty() == true){
//            val num = holder.newSeatNumber.text?: String
//            list.add(num.toString())
//            Timber.d("adapter12 : $list")
//
//        }else{
//            for(i in 0..searchList.size.minus(1)){
//                val num = holder.newSeatNumber.text?: String
//                Timber.d("adapter123 : $num")
//
//            }
//        }
////            }

//    }
//
    }


//    }

}