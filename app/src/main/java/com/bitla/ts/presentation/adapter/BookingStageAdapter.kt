package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ItemSelectBookingBinding
import com.bitla.ts.domain.pojo.booking.StageData
import gone
import visible


class BookingStageAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var menuList: List<StageData>,
    private val isCrossIconVisible: Boolean
) :
    RecyclerView.Adapter<BookingStageAdapter.ViewHolder>() {
    private var TAG: String = BookingStageAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSelectBookingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged", "LogNotTimber")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu: StageData = menuList[position]
        holder.title.text = menu.title

        if (menu.isRemovable)
            holder.imageview.visible()
        else
            holder.imageview.gone()

        if (isCrossIconVisible)
            holder.imageview.visible()
        else
            holder.imageview.gone()

        if (position == 0)
            holder.imageview.gone()

        //Old Booking Flow


//        if (menu.isSelected) {
//            holder.container.background =
//                context.resources.getDrawable(R.drawable.button_selected_bg)
//            holder.container.backgroundTintList = ColorStateList.valueOf(
//                context.resources.getColor(
//                    R.color.colorPrimary
//                )
//            )
//            holder.title.setTextColor(context.resources.getColor(R.color.white))
//            holder.imageview.imageTintList =
//                ColorStateList.valueOf((context.resources.getColor(R.color.white)))
//        } else {
//            holder.container.background =
//                context.resources.getDrawable(R.drawable.button_default_bg)
//            holder.container.backgroundTintList = ColorStateList.valueOf(
//                context.resources.getColor(
//                    R.color.grey_four_tint
//                )
//            )
//            holder.title.setTextColor(context.resources.getColor(R.color.colorBlackShadow))
//            holder.imageview.imageTintList =
//                ColorStateList.valueOf((context.resources.getColor(R.color.colorBlackShadow)))
//        }



        //New Booking Flow

        if (menu.isSelected) {
            holder.container.background =
                context.resources.getDrawable(R.drawable.layout_rounded_shape_border_primary_light_color)

            holder.title.setTextColor(context.resources.getColor(R.color.colorPrimary))
            holder.imageview.imageTintList =
                ColorStateList.valueOf((context.resources.getColor(R.color.colorPrimary)))
        } else {
            holder.container.background =
                context.resources.getDrawable(R.drawable.layout_rounded_shape_border_primary_color)
            holder.title.setTextColor(context.resources.getColor(R.color.colorPrimary))
            holder.imageview.imageTintList =
                ColorStateList.valueOf((context.resources.getColor(R.color.colorPrimary)))
        }






        holder.container.setOnClickListener {
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

        holder.imageview.setOnClickListener {
            holder.imageview.tag = context.getString(R.string.delete_recent_search)
            onItemClickListener.onClick(holder.imageview, position)
        }
    }

    class ViewHolder(binding: ItemSelectBookingBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.textTitle
        val imageview = binding.imageClose
        val container = binding.container
    }
}