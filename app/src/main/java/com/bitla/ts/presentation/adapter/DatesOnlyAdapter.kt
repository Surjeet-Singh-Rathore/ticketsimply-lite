package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildNextDatesBinding
import com.bitla.ts.domain.pojo.booking.StageData
import gone
import visible
import java.util.*


class DatesOnlyAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var menuList: List<StageData>
) :
    RecyclerView.Adapter<DatesOnlyAdapter.ViewHolder>() {
//    private var TAG: String = DatesOnlyAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildNextDatesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu: StageData = menuList[position]
        val date = menu.title.split(" ")
        holder.tvDate.text = date[0]
        holder.tvMonth.text = date[1].uppercase(Locale.getDefault())
        if (position == 0)
            holder.layoutCalender.visible()
        else
            holder.layoutCalender.gone()

        holder.layoutToday.gone()

        if (menu.isSelected) {
            holder.container.background =
                context.resources.getDrawable(R.drawable.button_selected_bg)
            holder.container.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.colorPrimary
                )
            )
            holder.tvDate.setTextColor(context.resources.getColor(R.color.white))
            holder.tvMonth.setTextColor(context.resources.getColor(R.color.white))
        } else {
            holder.container.background =
                context.resources.getDrawable(R.drawable.button_default_bg)
            holder.container.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.white
                )
            )
            holder.tvDate.setTextColor(context.resources.getColor(R.color.colorBlackShadow))
            holder.tvMonth.setTextColor(context.resources.getColor(R.color.colorBlackShadow))
        }
        holder.container.setOnClickListener {

            if (menuList[position].layoutType == "DATES" || menuList[position].layoutType == "BOOKING") {
                holder.container.tag = menuList[position].layoutType
                onItemClickListener.onClickOfItem(
                    "${holder.tvDate.text}",
                    position
                )

                onItemClickListener.onClick(
                    holder.container,
                    position
                )

            }
            for (i in 0..menuList.size.minus(1)) {
                menuList[i].isSelected = false
            }
            menuList[position].isSelected = true
            notifyDataSetChanged()
        }

        holder.layoutCalender.setOnClickListener {
            holder.layoutCalender.tag = context.getString(R.string.open_calender)
            onItemClickListener.onClick(
                holder.layoutCalender,
                position
            )
        }
    }

    class ViewHolder(binding: ChildNextDatesBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvDate = binding.tvDate
        val tvMonth = binding.tvMonth
        val layoutToday = binding.layoutToday
        val layoutCalender = binding.layoutCalender
        val container = binding.container
    }
}