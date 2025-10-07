package com.bitla.ts.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildBulkCancelBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.view_reservation.PassengerDetail
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import timber.log.Timber


class BulkCancelAdapter(
    private val context: Context,
    private var searchList: List<PassengerDetail>,
    private var onItemClickListener: OnItemClickListener,
    private var allselected: Int,
    private val currency: String,
    private val currencyFormat: String,
    val privilegeResponseModel: PrivilegeResponseModel?

//    private var searchListselected: List<PassengerDetail>,
) :
    RecyclerView.Adapter<BulkCancelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildBulkCancelBinding.inflate(LayoutInflater.from(context), parent, false)
//        Timber.d("searchlist123", "${searchListselected.size}")

        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
//        context.toast("${searchList.size}")
        return searchList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val searchModel: PassengerDetail = searchList[position]
        Timber.d("Before Creation: ${position} : ${searchList[position].ifSelected}")
        holder.passengerName.text = searchList[position].passengerName
        holder.pnrNumber.text =
            "${context.getString(R.string.pnr)} : ${searchList[position].pnrNumber}"
        holder.passengerSeatNo.text = searchList[position].seatNumber
        holder.checkBox.isChecked = searchList[position].ifSelected
        if (searchList[position].ticketFare != null) {
            holder.collectionAmount.text =
                "$currency ${(searchList[position].ticketFare)?.convert(currencyFormat)}"
        } else {
            holder.tvCollectionHeader.gone()
            holder.collectionAmount.gone()
            holder.inclGst.gone()
        }

        holder.passengerSeatNo.backgroundTintList =
            ColorStateList.valueOf(context.resources.getColor(R.color.blue_shade_four))

        if(privilegeResponseModel?.isChileApp == true){
            holder.inclGst.gone()
        }


        when (allselected) {
            0 -> {
                holder.checkBox.isChecked = false
                searchList.forEach {
                    it.ifSelected = false
                }
                holder.parentLayout.setBackgroundColor(context.resources.getColor(R.color.white))
            }
            1 -> {
                holder.checkBox.isChecked = true
                searchList.forEach {
                    it.ifSelected = true
                }

                holder.parentLayout.setBackgroundColor(context.resources.getColor(R.color.light_highlight_color))
            }
            2 -> {
                holder.checkBox.isChecked = searchList[position].ifSelected
                if (searchList[position].ifSelected == true) {
                    holder.parentLayout.setBackgroundColor(context.resources.getColor(R.color.light_highlight_color))

                } else {
                    holder.parentLayout.setBackgroundColor(context.resources.getColor(R.color.white))
                }
            }
        }
        holder.viewTicked.setOnClickListener {
            onItemClickListener.onClickOfItem(searchList[position].pnrNumber, position)
        }
        holder.checkBox.setOnClickListener {

            allselected = 2
            searchList[position].ifSelected = holder.checkBox.isChecked
            Timber.d("OnClick: ${position} : ${searchList[position].ifSelected}")
            if (searchList[position].ifSelected == true) {
                holder.parentLayout.setBackgroundColor(context.resources.getColor(R.color.light_highlight_color))

            } else {
                holder.parentLayout.setBackgroundColor(context.resources.getColor(R.color.white))
            }
            holder.checkBox.tag = holder.checkBox.isChecked.toString()

            onItemClickListener.onClick(holder.checkBox, position)


        }
    }


    class ViewHolder(binding: ChildBulkCancelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //        light_highlight_color
        val checkBox = binding.checkboxName
        val passengerSeatNo = binding.seatNumber
        val pnrNumber = binding.pnrNumber
        val parentLayout = binding.cardParentLayout
        val passengerName = binding.passengerName
        val collectionAmount= binding.tvCollection
        val viewTicked= binding.tvViewTicked
        val inclGst= binding.includingGst
        val tvCollectionHeader= binding.tvCollectionHeader

//        init {
//            this.setIsRecyclable(true)
//        }

    }

}