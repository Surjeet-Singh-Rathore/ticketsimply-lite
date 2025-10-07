package com.bitla.ts.presentation.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildSelectMultipleSeatBinding
import com.bitla.ts.domain.pojo.SpinnerItems

open class SelectUserTypeAdapter(
    private val context: Context,
    private var userList: MutableList<SpinnerItems>,
    private val onItemClickListener: OnItemClickListener,
) :

    RecyclerView.Adapter<SelectUserTypeAdapter.ViewHolder>() {

    private var mSelectedItemsIds: SparseBooleanArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildSelectMultipleSeatBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val userListData: SpinnerItems = userList[position]
        holder.checkSeatNumber.text = userListData.value
        holder.checkSeatNumber.isChecked = mSelectedItemsIds[position]

        holder.checkSeatNumber.setOnClickListener {
            onItemClickListener.onClick(holder.checkSeatNumber, position)

//            holder.checkSeatNumber.isChecked = !holder.checkSeatNumber.isChecked
//            if (holder.checkSeatNumber.isChecked) {
//                onItemCheckListener.onItemCheck(passengerDetailSeatData)
//            } else {
//                onItemCheckListener.onItemUncheck(passengerDetailSeatData)
//            }
//            checkCheckBox(position, !mSelectedItemsIds.get(position))
        }
    }

    class ViewHolder(binding: ChildSelectMultipleSeatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkSeatNumber = binding.checkSeatNumber
    }

}