package com.bitla.ts.presentation.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemNotificationClickListener
import com.bitla.ts.databinding.ChildNotificationBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.fetch_notification.Data
import com.bitla.ts.domain.pojo.fetch_notification.NotificationDetail
import gone
import visible

class NotificationParentAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var notificationCountList: List<NotificationDetail>
) :
    RecyclerView.Adapter<NotificationParentAdapter.ViewHolder>(), OnItemClickListener,
    OnItemNotificationClickListener {
    private val viewPool = RecyclerView.RecycledViewPool()

    private var filterBy = ""
    private var isRead = false
    private var allTypeNotification = true
    private var isDayBeforeYesterdayEmpty = false
    private var isYesterdayEmpty = false
    private var isTodayEmpty = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildNotificationBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notificationCountList.size
    }

    fun updateFilter(filterType: String) {
        filterBy = filterType
        isTodayEmpty = false
        isYesterdayEmpty = false
        isDayBeforeYesterdayEmpty = false
        //.filter { it.notification_type.trim() == filterBy.trim() },
        notifyDataSetChanged()
    }

    fun readUnreadFilter(isMarkRead: Boolean?,allTypeNoti: Boolean = false) {
        isRead = isMarkRead ?: false
        isTodayEmpty = false
        isYesterdayEmpty = false
        isDayBeforeYesterdayEmpty = false
        allTypeNotification = allTypeNoti
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationDetail: NotificationDetail = notificationCountList[position]

        holder.tvDay.text = notificationDetail.header_title
        holder.tvNotificationCount.text = notificationDetail.unread_notification_count
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        //Timber.d("filterBy ${notificationDetail.data.size}")

        val filterList = mutableListOf<Data>()
        notificationDetail.data.forEach {
            if (filterBy.isEmpty() || it.notification_type == filterBy.replace(
                    " ",
                    ""
                ) || filterBy == "All"
            )
                if(allTypeNotification){
                    filterList.add(it)
                }else{
                    if (it.is_mark_as_read == isRead) {
                        filterList.add(it)
                    }
                }

        }

        if (filterList.isNotEmpty()) {
            holder.layoutMain.visible()
            holder.layoutNoData.root.gone()
            holder.tvNotificationCount.text = filterList.size.toString()
        } else {
            when (position) {
                0 -> {
                    isDayBeforeYesterdayEmpty = true
                    holder.layoutMain.gone()
                    holder.layoutNoData.root.gone()
                }
                1 -> {
                    isYesterdayEmpty = true
                    holder.layoutMain.gone()
                    holder.layoutNoData.root.gone()

                }
                2 -> {
                    isTodayEmpty = true
                    holder.layoutMain.gone()
                    holder.layoutNoData.root.gone()
                }
            }
        }

        if (notificationCountList.size == 1) {
            if (isDayBeforeYesterdayEmpty) {
                onItemClickListener.onClickOfItem(
                    context.getString(R.string.no_notification),
                    position
                )
            }
        }

        if (notificationCountList.size == 2) {
            if (isDayBeforeYesterdayEmpty && isYesterdayEmpty) {
                onItemClickListener.onClickOfItem(
                    context.getString(R.string.no_notification),
                    position
                )
            }
        } else if (notificationCountList.size == 3) {
            if (isDayBeforeYesterdayEmpty && isYesterdayEmpty && isTodayEmpty) {

                onItemClickListener.onClickOfItem(
                    context.getString(R.string.no_notification),
                    position
                )

            }
        }

      /*  val notificationChildAdapter = NotificationChildAdapter(
            context = context,
            filterList,
            this,
        )
        layoutManager.initialPrefetchItemCount = notificationDetail.data.size
        holder.rvNestedItems.layoutManager = layoutManager
        holder.rvNestedItems.adapter = notificationChildAdapter
        holder.rvNestedItems.setRecycledViewPool(viewPool)*/

      //  notificationChildAdapter?.filter?.filter(filterBy)

        holder.imgMore.setOnClickListener {
            holder.imgMore.tag = context.getString(R.string.more_actions)
            onItemClickListener.onClick(view = holder.imgMore, position = position)
        }
    }

    class ViewHolder(binding: ChildNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        val rvNestedItems = binding.rvNestedItems
        val tvDay = binding.tvDay
        val tvNotificationCount = binding.tvNotificationCount
        val imgMore = binding.imgMore
        val layoutMain = binding.layoutMain
        val layoutNoData = binding.layoutNoData
        val tvNoData = binding.layoutNoData.tvNoData

    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    override fun onClick(view: View?, position: Int?, view2: View?, isMarkRead: Boolean?) {

    }
}