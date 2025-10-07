package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildLastBookedBinding
import com.bitla.ts.databinding.ChildMostFrequentlyBookedBinding
import com.bitla.ts.databinding.ItemSelectBookingBinding
import com.bitla.ts.domain.pojo.booking.StageData
import gone
import visible


class MostFrequentlyBookedAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var menuList: List<StageData>,
    private val isCrossIconVisible: Boolean
) :
    RecyclerView.Adapter<MostFrequentlyBookedAdapter.ViewHolder>() {
    private var TAG: String = MostFrequentlyBookedAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildMostFrequentlyBookedBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged", "LogNotTimber")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu: StageData = menuList[position]
        holder.title.text = menu.title

        holder.container.setOnClickListener {
            if (menuList.isNotEmpty()) {
                if (menuList[position].layoutType == "DATES" || menuList[position].layoutType == "BOOKING") {
                    holder.container.tag = menuList[position].layoutType

                    onItemClickListener.onClickOfItem(
                        "${holder.title.text}",
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

        }

        holder.imageArrow.setOnClickListener {
            holder.imageArrow.tag = context.getString(R.string.most_frequently_booked)
            onItemClickListener.onClick(holder.imageArrow, position)
        }
    }

    class ViewHolder(binding: ChildMostFrequentlyBookedBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.tvSrcDest
        val imageArrow = binding.imageArrow
        val container = binding.container
    }
}