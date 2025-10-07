package com.bitla.ts.presentation.adapter.RouteManager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.DialogAnyClickListener
import com.bitla.ts.databinding.AdapterServiceListRouteBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.route_list.RouteListData
import gone
import visible


class SearchListRouteAdapter(
    private val context: Context,
    private var routeList: ArrayList<RouteListData>,
    private val listener: DialogAnyClickListener,
    val privileges: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<SearchListRouteAdapter.ViewHolder>(){

    var filteredRouteList: ArrayList<RouteListData> = routeList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterServiceListRouteBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.moreIV.setOnClickListener {
            listener.onAnyClickListener(1,"moreIV",position)
        }
        holder.binding.editIV.setOnClickListener {
            listener.onAnyClickListener(1,"editIV",routeList[position].id.toInt())
        }


        if(privileges?.modifyRoutesInTsApp == true){
            holder.moreIV.visible()
            holder.editIV.visible()
        }else{
            holder.moreIV.gone()
            holder.editIV.gone()
        }

        var route = routeList[position]
        holder.binding.serviceNameAndTime.text = route.number
        holder.binding.originDestName.text =  route.departureTime + ", " +route.originName + " to " + route.destinationName
        holder.binding.busTypeTV.text = route.busType
        if(route.toDate.isNotEmpty()){
            holder.binding.fromToDateTV.text = route.fromDate + " to " + route.toDate
        }else{
            holder.binding.fromToDateTV.text = route.fromDate + " to End date not confirmed"
        }
        holder.binding.stagesTV.text = route.stagesCount.toString() + " stages"


    }

    class ViewHolder(binding: AdapterServiceListRouteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val rootView = binding.rootView
        val moreIV = binding.moreIV
        val editIV = binding.editIV
        val binding = binding

    }

    fun updateList(newList: ArrayList<RouteListData>) {
        routeList = newList
        notifyDataSetChanged()
    }
}