package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemCheckListener
import com.bitla.ts.databinding.ChildReleaseTicketPassengersListBinding
import com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail
import gone
import visible


class ReleaseTicketPassengersListAdapter(
    private val context: Context,
    private var passengerDetailSeatList: MutableList<PassengerDetail?>?,
    private val onItemCheckListener: OnItemCheckListener
) :
    RecyclerView.Adapter<ReleaseTicketPassengersListAdapter.ViewHolder>() {

    private var mSelectedItemsIds: SparseBooleanArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildReleaseTicketPassengersListBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return passengerDetailSeatList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val passengerDetailSeatData: PassengerDetail? = passengerDetailSeatList?.get(position)

        holder.tvName.text = passengerDetailSeatData?.name
        holder.tvSeatNo.text = passengerDetailSeatData?.seatNumber

        if (passengerDetailSeatData?.mobile?.isNotBlank() == true) {
            holder.tvMobile.visible()
            holder.tvMobile.text = passengerDetailSeatData.mobile
        } else {
            holder.tvMobile.gone()
        }
        holder.releaseTicketPassengerCheckBox.isChecked = mSelectedItemsIds[position]

        holder.releaseTicketPassengerCheckBox.setOnClickListener {

            if (holder.releaseTicketPassengerCheckBox.isChecked) {
                onItemCheckListener.onItemCheck(passengerDetailSeatData)
            } else {
                onItemCheckListener.onItemUncheck(passengerDetailSeatData)
            }
            checkCheckBox(position, !mSelectedItemsIds.get(position))
        }
    }

    class ViewHolder(binding: ChildReleaseTicketPassengersListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val releaseTicketPassengerCheckBox = binding.releaseTicketPassengerCheckBox
        val tvName = binding.tvName
        val tvSeatNo = binding.tvSeatNo
        val tvMobile = binding.tvMobile
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