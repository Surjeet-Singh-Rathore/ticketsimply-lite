package com.bitla.ts.presentation.adapter.SortByAdaper

import android.content.*
import android.view.*
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.utils.sharedPref.*
import gone
import timber.log.*
import visible

class SelectAllotServiceAdapter(
    private val context: Context,
    private var searchList: List<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>,
    private val onItemClickListener: OnItemClickListener,
    private val onItemPassData: OnItemPassData,
    private val isFromReservation: Boolean
) :
    RecyclerView.Adapter<SelectAllotServiceAdapter.ViewHolder>() {
    //    private var tag: String = ChildStageAdapterBinding::class.java.simpleName
//    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildSelectServiceBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val searchModel: com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service =
            searchList.get(position)

        if (searchModel.allService != null && searchModel.allService?.isNotEmpty()!!) {
            holder.layoutAllService.visible()
            holder.apiLayout.gone()

            holder.tvAllService.text = searchModel.allService

            holder.layoutAllService.setOnClickListener {
                onItemClickListener.onClick(holder.tvAllService, position)
            }

        } else {
            holder.layoutAllService.gone()
            holder.apiLayout.visible()
        }


        val oldreservationId = PreferenceUtils.getPreference(
            PREF_RESERVATION_ID, 0L
        ).toString()
//        val oldreservationId = PreferenceUtils.getString("reservationid")!!

//        val oldTravelDate = PreferenceUtils.getString("TicketDetail_Traveldate")!!
//        val newTravelDate = PreferenceUtils.getString("shiftPassenger_selectedDate")!!
//        val oldRouteId = PreferenceUtils.getString("ticketDetails_RouteID")!!.toInt()
        holder.serviceName.gone()
        holder.serviceDateTime.gone()
        holder.serviceSeatAvailable.gone()

        holder.serviceName.text = "${searchModel.origin}-${searchModel.destination}"
        holder.serviceDateTime.text = "${searchModel.travelDate} at ${searchModel.departureTime} "
        holder.serviceSeatAvailable.text =
            "${searchModel.availableSeats}/${searchModel.totalSeats} ${
                context.getString(
                    R.string.available
                )
            }"
        Timber.d("traveledated  : ${oldreservationId} : ${searchModel.reservationId}")

        holder.serviceNumber.text = "${searchModel.number}"
        if (oldreservationId == "null" || isFromReservation) {
            holder.currentServiceText.gone()

        } else {
            if (oldreservationId.toInt() == searchModel.reservationId?.toInt()) {
                holder.currentServiceText.visible()
            } else {
                holder.currentServiceText.gone()
            }
        }
//        if(oldTravelDate == newTravelDate){
//            if ( oldRouteId == searchModel.id){
//
//
//        }else{
//            holder.currentServiceText.gone()
//
//        }


        holder.apiLayout.setOnClickListener {
//            var nameString= ""
//
//            if (oldreservationId.toInt() == searchModel.reservationId) {
//                nameString = "${searchModel.number} ${holder.currentServiceText.text} ${holder.serviceName.text}"
//            } else {
//                nameString = "${searchModel.number} ${holder.serviceName.text}"
//            }
//            holder.serviceName.tag = "click"
            val res = searchModel.reservationId!!


//            lastSelectedPosition = adapterPosition
//            notifyDataSetChanged()
            PreferenceUtils.setPreference(
                PREF_RESERVATION_ID, res
            )
//            onItemClickListener.onClickOfItem(holder.serviceName.text.toString(), position)
//            onItemPassData.onItemData(holder.apiLayout, nameString, searchModel.number!!)
            onItemClickListener.onClick(holder.serviceName, position)
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
        val layoutAllService = binding.layoutAllService
        val tvAllService = binding.tvAllService
    }

}