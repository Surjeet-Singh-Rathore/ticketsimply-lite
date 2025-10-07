package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildShiftListDoneBinding

class ShiftListAdapter(
    private val context: Context,
    private var oldSeatList: List<String>,
    private var newSeatList: List<String>,
    private var boardingPoint: List<String>,
    private var droppingPoint: List<String>,
    private var pnr: List<String>,
    private var extraSeat: Boolean,
    private var passengerNameList: List<String>


) :
    RecyclerView.Adapter<ShiftListAdapter.ViewHolder>() {
    //    private var tag: String = ChildStageAdapterBinding::class.java.simpleName
//    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildShiftListDoneBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return oldSeatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        if (oldSeatList.contains(",")) {
//            val oldSeatListArray = oldSeatList.split(",")
//            val newSeatListArray = newSeatList.split(",")

        val oldSearchModel: String = oldSeatList[position]
        val newSearchModel: String = newSeatList[position]
        holder.oldSeat.text = oldSearchModel
        if (extraSeat) {
            holder.newSeat.text = "EX-${newSearchModel}"
        } else {
//                holder.oldSeat.text = oldSearchModel
            holder.newSeat.text = newSearchModel
        }

//        } else {
//            holder.oldSeat.text = oldSeatList
//
//            if (extraSeat){
//                holder.newSeat.text= "EX-${newSeatList}"
//            }else{
//                holder.newSeat.text = newSeatList
//            }
//
//
//        }
        if (pnr.size > 1) {

            holder.pnrNumber.text = pnr[position]
        } else {
            holder.pnrNumber.text = pnr[0]
        }
        holder.passengerName.text = passengerNameList[position]
        holder.originDestination.text = "${boardingPoint[position]}- ${droppingPoint[position]}"

    }

    class ViewHolder(binding: ChildShiftListDoneBinding) : RecyclerView.ViewHolder(binding.root) {
        val oldSeat = binding.oldSeatNumber
        val newSeat = binding.newSeatnumber
        val pnrNumber = binding.pnrNumber
        val passengerName = binding.passengerName
        val originDestination = binding.originDestination

    }

}