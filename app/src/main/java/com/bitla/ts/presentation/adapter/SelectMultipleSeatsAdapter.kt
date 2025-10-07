package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemCheckListener
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail


open class SelectMultipleSeatsAdapter(
    private val context: Context,
    private var passengerDetailSeatList: MutableList<PassengerDetail?>?,
    private val onItemCheckListener: OnItemCheckListener
) : RecyclerView.Adapter<SelectMultipleSeatsAdapter.ViewHolder>() {

    private var mSelectedItemsIds: SparseBooleanArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildSelectMultipleSeatBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerDetailSeatList?.size ?: 0
    }

//    val passengerDetailSeatData: PassengerDetail? = passengerDetailSeatList?.get(position)
//
//    if (passengerDetailSeatData != null) {
//        holder.checkSeatNumber.text = passengerDetailSeatData.seatNumber
//    }
//
//
//    if (passengerDetailSeatList?.size == 1) {
//        context.toast("${passengerDetailSeatList?.size}")
//        holder.checkSeatNumber.isChecked = true
//        selectedFirstSeatNo = passengerDetailSeatList!![position]?.seatNumber.toString()
//    } else {
//    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val passengerDetailSeatData: PassengerDetail? = passengerDetailSeatList?.get(position)
        if (passengerDetailSeatData != null) {
            holder.checkSeatNumber.text = passengerDetailSeatData.seatNumber
        }

        if (passengerDetailSeatList?.size == 1) {
            holder.checkSeatNumber.isChecked = true
        } else {
            holder.checkSeatNumber.isChecked = mSelectedItemsIds[position]
        }

        holder.checkSeatNumber.setOnClickListener {
//            onItemClickListener.onClick(holder.checkSeatNumber, position)

//            holder.checkSeatNumber.isChecked = !holder.checkSeatNumber.isChecked
            if (holder.checkSeatNumber.isChecked) {
                onItemCheckListener.onItemCheck(passengerDetailSeatData)
            } else {
                onItemCheckListener.onItemUncheck(passengerDetailSeatData)
            }
            checkCheckBox(position, !mSelectedItemsIds.get(position))
        }
    }

    class ViewHolder(binding: ChildSelectMultipleSeatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkSeatNumber = binding.checkSeatNumber
    }

    /**
     * Check the Checkbox if not checked
     */
    @SuppressLint("NotifyDataSetChanged")
    fun checkCheckBox(position: Int, value: Boolean) {
        if (value) mSelectedItemsIds.put(position, true)
        else {
            mSelectedItemsIds.delete(position)
            notifyDataSetChanged()

        }
    }
}