package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildBlackedListNumberAdapterBinding
import com.bitla.ts.domain.pojo.blocked_numbers_list.BlockedNumber
import com.bitla.ts.utils.common.getDateDMY
import visible

interface OnItemAdapterClickListener{
    fun onItemClick(phoneNumber: String)
    fun getBlockedNumber(blockedBy: String,blockedOn:String)
}

class BlackListNumberAdapter(
    private val context: Context,
    private var numberList: List<BlockedNumber>,
    private val listener: OnItemAdapterClickListener,

) :
    RecyclerView.Adapter<BlackListNumberAdapter.ViewHolder>() {
    private lateinit var binding: ChildBlackedListNumberAdapterBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         binding =
            ChildBlackedListNumberAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return numberList.size
    }
    fun updateList(newSearchedList: List<BlockedNumber>) {
        numberList = newSearchedList
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        var currentItem = numberList[position]
        //var blockedNumber = listener.getBlockedNumber()

        holder.btnUnblock.setOnClickListener{
            val phoneNumber = holder.blockedNumber.text
            listener.onItemClick(phoneNumber.toString())

        }
        currentItem.blockedBy?.let { currentItem.blockedOn?.let { it1 ->
            listener.getBlockedNumber(it,
                it1
            )
        } }
        holder.blockedNumber.text= currentItem.blockedNumber
        holder.blockedBy.text= context.getString(R.string.by)+currentItem.blockedBy
        holder.blockedOn.text= "On: "+ getDateDMY( currentItem.blockedOn.toString())
        holder.reason.text = context.getString(R.string.reason_)+currentItem.remarks
    }


    class ViewHolder(binding: ChildBlackedListNumberAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val btnUnblock = binding.btnUnblock
        val blockedNumber = binding.textPhoneNumber
        val blockedOn = binding.date
        val blockedBy = binding.blockedBy
        val reason = binding.reason
    }



}