package com.bitla.ts.presentation.adapter.tripCollection

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.AdapterTripCollectionBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.collection_details.trip_collection.BranchBooking
import com.bitla.ts.domain.pojo.collection_details.trip_collection.TripCollectionCategoryData
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel

class TripCollectionAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var dataList: ArrayList<TripCollectionCategoryData>?,
    val privileges: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<TripCollectionAdapter.ViewHolder>(), OnItemClickListener {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterTripCollectionBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tripCollectionData= dataList?.get(position)
        holder.collectionCategory.text=tripCollectionData?.categoryName
        val childAdapter=ChildTripCollectionAdapter(context,tripCollectionData?.collectionDetails!!,privileges)
        holder.detailsRV.adapter=childAdapter
    }


    class ViewHolder(binding: AdapterTripCollectionBinding) : RecyclerView.ViewHolder(binding.root) {
        var collectionCategory=binding.collectionCategoryTV
        var detailsRV=binding.detailsRV

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
}