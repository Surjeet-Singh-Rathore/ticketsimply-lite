package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.ChildBulkShiftListAdapterBinding
import com.bitla.ts.domain.pojo.multiple_shift_passenger.request.Data
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetail
import timber.log.Timber

class AutoShiftAdapter(
    private val context: Context,
    private var oldList: List<PassengerDetail>,
    private var newList: List<Data>,
    private var onItemPassData: OnItemPassData

) :
    RecyclerView.Adapter<AutoShiftAdapter.ViewHolder>() {
    //    private var tag: String = ChildStageAdapterBinding::class.java.simpleName
//    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ChildBulkShiftListAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val oldList: PassengerDetail = oldList.get(position)
        val newList: Data = newList.get(position)
        holder.serviceName.setText("${oldList.boardingCity}-${oldList.droppingCity}")
        holder.oldSeatNumber.text = oldList.seatNumber
        holder.passenger_name.text = oldList.passengerName
        holder.newSeatNumber.setText(newList.new_seat_number)
        holder.servicePnr.setText(oldList.pnrNumber)
        var new = ""

        Timber.d("adapter12 :1234")

        holder.newSeatNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onItemPassData.onItemData(
                    holder.newSeatNumber,
                    newList.new_seat_number,
                    newList.old_seat_number
                )

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                new = holder.newSeatNumber.text.toString()
                Timber.d("adapter12 : $new")
//                    onItemClickListener.onClickOfItem(new,searchModel.number!!.toInt())
                onItemPassData.onItemData(
                    holder.newSeatNumbLayout,
                    new,
                    newList.old_seat_number
                )
            }
        })
        onItemPassData.onItemData(
            holder.newSeatNumbLayout,
            newList.new_seat_number,
            newList.old_seat_number
        )
//


    }

    class ViewHolder(binding: ChildBulkShiftListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val serviceName = binding.routeOriginDestination
        val servicePnr = binding.pnrNumber
        val oldSeatNumber = binding.oldSeatNumber
        val newSeatNumber = binding.etNewSeatnumber
        val newSeatNumbLayout = binding.seatnoLayoutNew
        val passenger_name = binding.passengerNamer

    }

}