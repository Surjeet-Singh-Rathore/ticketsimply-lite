package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildNotificationDetailsInnerCardBinding
import com.bitla.ts.domain.pojo.notificationDetails.Data
import com.bitla.ts.domain.pojo.notificationDetails.DataX
import gone
import visible

class NotificationDetailsInnerCardAdapter(
    private val context: Context,
    private val mList: List<DataX>
) : RecyclerView.Adapter<NotificationDetailsInnerCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildNotificationDetailsInnerCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return NotificationDetailsInnerCardAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem= mList.get(position)
        holder.lable.text= currentItem.label
        holder.description.text= currentItem.value

        if (currentItem.label.equals("") || currentItem.value.isNullOrEmpty()){
            holder.mainLyaout.gone()
            holder.lable.gone()
            holder.description.gone()
        }else{
            holder.mainLyaout.visible()
            holder.lable.visible()
            holder.description.visible()
        }

    }


    class ViewHolder(binding: ChildNotificationDetailsInnerCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

            val lable = binding.lable
            val description = binding.description
            val mainLyaout = binding.llData


    }
}