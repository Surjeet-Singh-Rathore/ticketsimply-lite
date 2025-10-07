package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildNotificationDetailsOuterCardBinding
import com.bitla.ts.domain.pojo.notification_details_phase_3.response.Data

class NotificationDetailsOuterCardAdapter(
    private val context: Context,
    private val mList: List<com.bitla.ts.domain.pojo.notificationDetails.Result>,
) : RecyclerView.Adapter<NotificationDetailsOuterCardAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildNotificationDetailsOuterCardBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = mList.get(position)

        holder.label.text= currentItem.upper_block_label
        holder.description.text= currentItem.upper_block_short_desc

        var layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)

        currentItem.data.forEach {
            if (it.view_type.equals("upper", true)){
                if (it.is_grid){
                    layoutManager = GridLayoutManager(context, 2)
                }else{
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }

                holder.rvUpperCard.layoutManager = layoutManager
                val notificationDetailsInnerCardAdapter =
                    NotificationDetailsInnerCardAdapter(context, it.data)
                holder.rvUpperCard.adapter = notificationDetailsInnerCardAdapter
            }else{
                if (it.is_grid){
                    layoutManager = GridLayoutManager(context, 2)
                }else{
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
                holder.rvLowerCard.layoutManager = layoutManager
                val notificationDetailsInnerCardAdapter =
                    NotificationDetailsInnerCardAdapter(context, it.data)
                holder.rvLowerCard.adapter = notificationDetailsInnerCardAdapter

            }

        }

        val temp = arrayListOf<com.bitla.ts.domain.pojo.notificationDetails.Data>()
        temp.addAll(currentItem.data)
//        if((position % 2) == 0) { // When position is even
//            holder.parentCard.background.setTint(ContextCompat.getColor(context, R.color.color_light_purple));
//        } else { //When position is odd
//            holder.parentCard.background.setTint(ContextCompat.getColor(context, R.color.white));
//        }


    }

    class ViewHolder(binding: ChildNotificationDetailsOuterCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val rvUpperCard = binding.rvCardLayoutUpper
        val rvLowerCard = binding.rvCardLayoutLower
        val label = binding.label
        val description = binding.description

        val parentCard = binding.parentCard
    }
}