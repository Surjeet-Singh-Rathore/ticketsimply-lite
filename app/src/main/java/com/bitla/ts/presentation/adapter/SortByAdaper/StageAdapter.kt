package com.bitla.ts.presentation.adapter.SortByAdaper

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.data.listener.OnPnrListener
import com.bitla.ts.data.listener.OnclickitemMultiView
import com.bitla.ts.databinding.ChildStageAdapterBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.view_reservation.RespHash
import gone
import setSafeOnClickListener
import timber.log.Timber
import toast
import visible

class StageAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var searchList: List<RespHash>,
    private val isParentVisible: Boolean,
    private val onItemPassData: OnItemPassData,
    private var chartType: String,
    private var onclickitemMultiView: OnclickitemMultiView,
    private val currency: String,
    private val currencyFormat: String,
    private val privilegeResponseModel: PrivilegeResponseModel?,
    private val loginModelPref: LoginModel,
    private val onPnrListener: OnPnrListener,
    private val onCallClickListener: (phoneNumber: String) -> Unit
) :

    RecyclerView.Adapter<StageAdapter.ViewHolder>(), OnItemClickListener, OnItemPassData,
    OnclickitemMultiView {
    private var childSortSublistAdapter: ChildSortSublistAdapter? = null

    //    private var tag: String = ChildStageAdapterBinding::class.java.simpleName
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildStageAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: RespHash = searchList.get(position)
        val branchName: String = searchModel.branchName ?: ""

        //holder.tvDateTime.text = searchModel.name
        if (isParentVisible) {
            holder.header.visible()
            holder.tvDateTime.text = searchModel.name
            holder.count.text = searchModel.passengerDetails?.size.toString()

            if (searchModel.pickupClosed) {
                holder.closeChat.visible()
                holder.closeChat.isClickable = false
                holder.closeChat.backgroundTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.button_default_color
                    )
                )

                holder.closeChat.text = context.getString(R.string.chart_closed)

            } else {
                if (privilegeResponseModel != null) {

                    privilegeResponseModel.let {
                        if (it.allowToClosePickupByCity) {
                            if (searchModel.pickupClosed) {
                                holder.closeChat.gone()
                            } else {
                                if (chartType == "1") {
                                    holder.closeChat.visible()
                                } else {
                                    holder.closeChat.gone()

                                }
                                holder.closeChat.backgroundTintList = ColorStateList.valueOf(
                                    context.resources.getColor(
                                        R.color.colorPrimary
                                    )
                                )
                                holder.closeChat.isClickable = true
                                holder.closeChat.setSafeOnClickListener {
                                    holder.closeChat.tag = context.getString(R.string.close_chart)
                                    onItemData(
                                        holder.closeChat,
                                        searchModel.cityId.toString(),
                                        searchModel.id.toString()
                                    )
                                }
                            }

                        } else {
                            holder.closeChat.gone()
                        }
                    }
                } else {
                    context.toast(context.getString(R.string.server_error))
                }
            }
        } else {
            holder.header.gone()
        }

        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        childSortSublistAdapter = ChildSortSublistAdapter(
            context = context,
            directRoute = false,
            searchList = searchModel.passengerDetails,
            branchName = branchName,
            onItemClickListener = onItemClickListener,
            onItemPassData = this,
            chartClosed = searchModel.pickupClosed,
            onclickitemMultiView = this,
            currency = currency,
            currencyFormat = currencyFormat,
            privilegeResponseModel = privilegeResponseModel,
            loginModelPref = loginModelPref,
            onPnrListener = onPnrListener,
            chartType = chartType,
            onCallClickListener = onCallClickListener,
        )
        layoutManager.initialPrefetchItemCount = searchModel.passengerDetails?.size!!
        holder.rvNestedItems.layoutManager = layoutManager
        holder.rvNestedItems.adapter = childSortSublistAdapter
        holder.rvNestedItems.setRecycledViewPool(viewPool)

    }

    class ViewHolder(binding: ChildStageAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvDateTime = binding.tvDateTime
        val header = binding.constraintLayout5
        val rvNestedItems = binding.rvNestedItems
        val count = binding.count
        val closeChat = binding.closeChatButton
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            onItemClickListener.onClick(view, position)
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {


    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    fun addItem(itemList:List<RespHash>){
        searchList = itemList
        notifyDataSetChanged()
    }

    override fun onItemData(view: View, str1: String, str2: String) {
        if (view.tag != null) {
            onItemPassData.onItemData(view, str1, str2)
//            onItemData(view, str1, str2)
//            onItemPassData.onItemPassData(
//                holder.layoutstatus,
//                searchModel.pnrNumber,
//                searchModel.seatNumber
//            )

        }
        Timber.d("stage adapter ${str1}, ${str2}")
    }

    override fun onItemDataMore(
        view: View,
        str1: String,
        str2: String,
        str3: String
    ) {
        onItemPassData.onItemDataMore(view, str1, str2, str3)
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
        onclickitemMultiView.onClickAdditionalData(view0,view1)

    }

    fun notifyChildAdapter(position: Int){
        childSortSublistAdapter!!.notifyItemChanged(position)
    }
}