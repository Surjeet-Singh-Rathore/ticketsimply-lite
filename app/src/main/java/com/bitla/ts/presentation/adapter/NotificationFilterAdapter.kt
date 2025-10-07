package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ItemFilterNotificationBinding
import com.bitla.ts.domain.pojo.fetch_notification.request.NotificationFilter
import com.bumptech.glide.Glide
import timber.log.Timber

class NotificationFilterAdapter(
    private val context: Context,
    private var notificationFilter: List<NotificationFilter>,
    private var onItemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<NotificationFilterAdapter.ViewHolder>() {

    companion object {
        var tag: String? = NotificationFilterAdapter::class.simpleName
    }

    private var lastSelectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFilterNotificationBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notificationFilter.size
    }

    fun updatePosition(position: Int) {
        lastSelectedPosition = position
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationNavigationData: NotificationFilter = notificationFilter[position]

        holder.tvFilterNotificationTitle.text = notificationNavigationData.label
        //  holder.imgFilterNotification.setImageResource(notificationNavigationData.)


        if (notificationNavigationData.image.isNotEmpty()) {
            Glide.with(context)
                .load(notificationNavigationData.image)
                .fitCenter()
                .into(holder.imgFilterNotification)
        } else {
            try {
                if (position == 0)
                    holder.imgFilterNotification.setImageDrawable(context.resources.getDrawable(R.drawable.ic_notification_all))
                if (position == 1)
                    holder.imgFilterNotification.setImageDrawable(context.resources.getDrawable(R.drawable.ic_mis))
                if (position == 2)
                    holder.imgFilterNotification.setImageDrawable(context.resources.getDrawable(R.drawable.ic_booking_cancellation))
                if (position == 3)
                    holder.imgFilterNotification.setImageDrawable(context.resources.getDrawable(R.drawable.ic_notification_block))
                if (position == 4)
                    holder.imgFilterNotification.setImageDrawable(context.resources.getDrawable(R.drawable.ic_notification_general))
            } catch (e: Exception) {
                holder.imgFilterNotification.setImageDrawable(context.resources.getDrawable(R.drawable.ic_notification_general))
                Timber.d("exceptionMsg ${e.message}")
            }
        }

        if (position == lastSelectedPosition) {
            /*holder.containerNotification.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )  */
            holder.containerNotification.background = ContextCompat.getDrawable(context,R.drawable.bg_stroke_blue_solid_light_blue)

            /*holder.tvFilterNotificationTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )*/
         //   holder.tvFilterNotificationTitle.background = ContextCompat.getDrawable(context,R.drawable.bg_white_round_solid)

        } else {
            /*holder.containerNotification.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
*/
            holder.containerNotification.background = ContextCompat.getDrawable(context,R.drawable.bg_white_round_solid)

          //  holder.tvFilterNotificationTitle.background = ContextCompat.getDrawable(context,R.drawable.bg_stroke_blue_solid_light_blue)

          /*  holder.tvFilterNotificationTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorBlackShadow
                )
            )*/
        }

        holder.containerNotification.setOnClickListener {
            holder.containerNotification.tag = tag
            onItemClickListener.onClick(holder.containerNotification, position)
        }
    }



    inner class ViewHolder(binding: ItemFilterNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tvFilterNotificationTitle = binding.tvFilterNotificationTitle
        val imgFilterNotification = binding.imgFilterNotification
        val containerNotification = binding.containerNotification
    }
}