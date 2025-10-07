package com.bitla.ts.presentation.adapter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemNotificationClickListener
import com.bitla.ts.databinding.ItemNotificationBinding
import com.bitla.ts.domain.pojo.fetch_notification.Data
import com.bitla.ts.fcm.ButtonReceiver
import com.bitla.ts.presentation.view.activity.notifications.NotificationDetailsPhase3Actvity
import com.bitla.ts.presentation.view.activity.notifications.NotificationsActivityNew
import com.bitla.ts.utils.constants.NOTIFICATION_DEFAULT_SOUND
import com.bitla.ts.utils.constants.NOTIFICATION_SILENT
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.skydoves.balloon.iconForm
import gone
import timber.log.Timber
import toast
import visible
import java.util.*
import kotlin.collections.ArrayList

class NotificationChildAdapter(
    private val context: Context,
    private var notificationDetailsList: ArrayList<Data>,
    private var onItemClickListener: OnItemNotificationClickListener,
) :
    RecyclerView.Adapter<NotificationChildAdapter.ViewHolder>(), Filterable {

    private lateinit var  objHolder : ViewHolder
    companion object {
        var tag: String? = NotificationChildAdapter::class.simpleName
    }

    var filterList = listOf<Data>()

    init {
        filterList = notificationDetailsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notificationDetailsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationDetailsData: Data? = notificationDetailsList[position]

        objHolder = holder

        if (notificationDetailsData?.route != null) {
            holder.tvRoute.visible()
            holder.tvRoute.text =
                "${context.getString(R.string.route)} : ${notificationDetailsData?.route}"
        } else
            holder.tvRoute.gone()

        if (notificationDetailsData?.notification_type != null) {
            holder.tvNotificationType.visible()
            if (notificationDetailsData?.notification_type != "All")
                holder.tvNotificationType.text = notificationDetailsData?.notification_type
            else
                holder.tvNotificationType.text = context.getString(R.string.general)
        } else
            holder.tvNotificationType.gone()

        if (notificationDetailsData?.doj != null) {
            holder.tvDoj.visible()
            holder.tvDoj.text = notificationDetailsData?.doj
        } else
            holder.tvDoj.gone()


        Timber.d("notificationDetailsData ${notificationDetailsData?.notification_msg}")

        if (notificationDetailsData?.notification_msg != null) {
            holder.tvNotificationTitle.visible()
            holder.tvNotificationTitle.text = notificationDetailsData?.notification_msg
        } else
            holder.tvNotificationTitle.gone()


        holder.imgNotificationNext.setOnClickListener {
            val intent = Intent(context, NotificationDetailsPhase3Actvity::class.java)
            intent.putExtra(
                context.getString(R.string.notification_id),
                notificationDetailsData?.id
            )
            context.startActivity(intent)
        }

        holder.containerNotification.tag = context.getString(R.string.notifications)
        holder.containerNotification.setOnClickListener {
            var idInt: String? =notificationDetailsData?.id
            idInt?.toInt()?.let { it1 -> cancelNotification(it1) }
            val intent = Intent(context, NotificationDetailsPhase3Actvity::class.java)
            intent.putExtra(
                context.getString(R.string.notification_id),
                notificationDetailsData?.id
            )
            context.startActivity(intent)
        }

        if (notificationDetailsData?.is_mark_as_read == true) {
            holder.containerNotification.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.flash_white
                )
            )
            holder.dotsLineNotification.visible()
        } else {
            holder.containerNotification.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
            holder.dotsLineNotification.gone()
        }


    }

    fun addData(newData: MutableList<Data>) {
        notificationDetailsList.addAll(newData)
        notifyDataSetChanged()
        hideProgress()
        (context as NotificationsActivityNew).dataLoaded()
    }

    fun showProgress(){
        if(::objHolder.isInitialized){
            objHolder.containerNotification.gone()
            objHolder.pageLoaderPB.visible()
        }

    }

    fun hideProgress(){
        if(::objHolder.isInitialized){
            objHolder.containerNotification.visible()
            objHolder.pageLoaderPB.gone()
        }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filterList = if (charSearch.isEmpty()) {
                    notificationDetailsList
                } else {
                    val resultList = mutableListOf<Data>()
                    for (row in notificationDetailsList) {
                        if (row.notification_type.lowercase(Locale.getDefault())
                                .contains(
                                    constraint.toString().lowercase(Locale.getDefault())
                                        .replace(" ", "")
                                )
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as List<Data>
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tvNotificationTitle = binding.tvNotificationDetailsTitle
        val tvRoute = binding.tvRoute
        val tvDoj = binding.tvDoj
        val tvNotificationType = binding.tvNotificationType
        val imgNotificationNext = binding.imgNotificationNext
        val containerNotification = binding.containerNotification
        val dotsLineNotification = binding.dotsLineNotification
        val pageLoaderPB = binding.pageLoaderPB
    }
    private fun cancelNotification(notificationId:Int) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(notificationId)

    }


}