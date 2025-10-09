package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.data.listener.OnclickitemMultiView
import com.bitla.ts.databinding.ChildReservationHubBinding
import com.bitla.ts.domain.pojo.alloted_services.RespHash
import com.bitla.ts.domain.pojo.alloted_services.Service
import com.bitla.ts.domain.pojo.available_routes.Result

class ReservationHubAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private val onclickitemMultiView: OnclickitemMultiView,
    private val onItemPassData: OnItemPassData,
    private var searchList: ArrayList<RespHash>,
    private var searchListSub: ArrayList<ArrayList<Service>>

) :
    RecyclerView.Adapter<ReservationHubAdapter.ViewHolder>(), OnItemClickListener,
    OnclickitemMultiView, OnItemPassData {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildReservationHubBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    @SuppressLint("RtlHardcoded")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchListSub: ArrayList<Service> = searchListSub[position]

        val searchModel: RespHash = searchList[position]


        holder.hubName.text = "${searchModel.hubName}(${searchListSub.size})"
        holder.summary.setOnClickListener {

            holder.summary.tag = "yes"

            onItemClickListener.onClick(holder.summary, position)
        }

        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
//        val driverChildAdapter = MyReservationAdapter(
//            context = context,
//            this,
//            this,
//            searchListSub,
//            this,
//            true
//        )
        layoutManager.initialPrefetchItemCount = searchListSub.size

        holder.rvCard.layoutManager = layoutManager

//        holder.rvCard.adapter = driverChildAdapter


    }

    class ViewHolder(binding: ChildReservationHubBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val hubName = binding.allservice
        val summary = binding.viewSummary
        val rvCard = binding.rvreservationPickup

    }


    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            onItemClickListener.onClick(view, position)
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }


    override fun onClickMuliView(
        view: View,
        view2: View,
        view3: View,
        view4: View,
        resID: String,
        remarks: String
    ) {
        onclickitemMultiView.onClickMuliView(view, view2, view3, view4, resID, remarks)
    }

    override fun onClickAdditionalData(view0: View, view1: View) {

    }

    override fun onItemData(view: View, str1: String, str2: String) {
        onItemPassData.onItemData(view, str1, str2)
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
        TODO("Not yet implemented")
    }


//    override fun onClickMuliView(view: View, view2: View, view3: View, view4: View,view5: View,view6: View, position: Int,int: Int) {
//        if (view.tag != null) {
//            onclickitemMultiView.onClickMuliView(view, view2, view3, view4,view5,view6, position,int)
//        }
//    }

}