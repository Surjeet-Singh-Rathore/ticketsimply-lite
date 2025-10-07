package com.bitla.ts.presentation.adapter

import android.content.*
import android.view.*
import androidx.recyclerview.widget.*
import com.bitla.ts.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.utils.sharedPref.*
import gone
import visible

class SelectServiceAdapter(
    private val context: Context,
    private var searchList: MutableList<Result>,
    private val onItemClickListener: OnItemClickListener,
    private val onItemPassData: OnItemPassData,
) :
    RecyclerView.Adapter<SelectServiceAdapter.ViewHolder>() {
//    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildSelectServiceBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun getItemCount(): Int {
        return searchList.size
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        val oldreservationId = PreferenceUtils.getString("reservationid")!!
//        val oldTravelDate = PreferenceUtils.getString("TicketDetail_Traveldate")!!
//        val newTravelDate = PreferenceUtils.getString("shiftPassenger_selectedDate")!!
//        val oldRouteId = PreferenceUtils.getString("ticketDetails_RouteID")!!.toInt()
        
        val searchModel: Result = searchList[position]
        val oldReservationId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
//        val parentTravelId = PreferenceUtils.getString("parent_travel_id") ?: ""
        
        val serviceName = "${searchModel.origin}-${searchModel.destination}"
        holder.serviceName.text = serviceName
        holder.serviceNumber.text = searchModel.number
        
        val serviceDateTime = "${searchModel.dep_date} at ${searchModel.dep_time} "
        holder.serviceDateTime.text = serviceDateTime
        
        val serviceSeatAvailable = "${searchModel.available_seats}/${searchModel.total_seats} ${
            context.getString(R.string.available)
        }"
        holder.serviceSeatAvailable.text = serviceSeatAvailable
        if (oldReservationId?.toInt() == searchModel.reservation_id.toInt()) {
            holder.currentServiceText.visible()
        } else {
            holder.currentServiceText.gone()
        }
        
        
        holder.apiLayout.setOnClickListener {
            var nameString = ""
            
            if (oldReservationId == searchModel.reservation_id) {
                nameString =
                    "${searchModel.number} ${holder.currentServiceText.text} ${holder.serviceName.text}"
            } else {
                nameString = "${searchModel.number} ${holder.serviceName.text}"
            }
            holder.serviceName.tag = "click"
            val res = searchModel.reservation_id


//            lastSelectedPosition = adapterPosition
//            notifyDataSetChanged()
            
            onItemClickListener.onClickOfItem(holder.serviceName.text.toString(), position)
            onItemPassData.onItemData(holder.apiLayout, nameString, searchModel.number)
            onItemClickListener.onClick(holder.serviceName, res.toInt())
            PreferenceUtils.setPreference(
                "ApiNumberSelected",
                "${searchModel.number}?${searchModel.dep_date}"
            )
            
        }
        //holder.tvDateTime.text = searchModel.name
        
    }
    
    class ViewHolder(binding: ChildSelectServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        val serviceName = binding.serviceName
        val serviceDateTime = binding.serviceDateTime
        val serviceSeatAvailable = binding.serviceSeatAvailable
        val serviceNumber = binding.serviceNumber
        val currentServiceText = binding.currentService
        val apiLayout = binding.apiLayout
    }
    
}