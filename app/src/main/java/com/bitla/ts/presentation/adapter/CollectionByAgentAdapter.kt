package com.bitla.ts.presentation.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildCollectionAgentBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.collection_details.CollectionSummary
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.common.getUserRole
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLogin
import gone
import timber.log.Timber
import toast
import visible

class CollectionByAgentAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var agentList: List<CollectionSummary>,
    val privileges: PrivilegeResponseModel?
) :
    RecyclerView.Adapter<CollectionByAgentAdapter.ViewHolder>(), OnItemClickListener {
    private val viewPool = RecyclerView.RecycledViewPool()
    private var currencyFormat = ""
    private var loginModelPref: LoginModel = LoginModel()
    private var tripSheetCollectionOptionsInTSAppReservationChart = false
    private var country: String = ""
    private var isAgentLogin: Boolean = false
    private var role: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildCollectionAgentBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return agentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: CollectionSummary = agentList[position]
//        val searchModel: PassengerDetail = passengerDetail[position]
        //holder.tvDateTime.text = searchModel.name

        if (privileges != null) {

            privileges?.let {
                Timber.d(
                    "collectionTest:: ${it.currencyFormat}, ${
                        getCurrencyFormat(
                            context,
                            it.currencyFormat
                        )
                    }"
                )

                currencyFormat = getCurrencyFormat(context, it.currencyFormat)
                if (it.currency.isNotEmpty()) {
                    holder.seatamount.text = "${it.currency} ${
                        (searchModel.total_amount).convert(currencyFormat)
                    }"
                } else {
                    holder.seatamount.text = (searchModel.total_amount).convert(currencyFormat)

                }
            }

            if(privileges.isAgentLogin != null) {
                isAgentLogin = privileges.isAgentLogin
            }

            loginModelPref = getLogin()
            role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, context)

            tripSheetCollectionOptionsInTSAppReservationChart = if (role == context.getString(R.string.role_field_officer)) {
                privileges.boLicenses?.tripSheetCollectionOptionInTSAppReservationChart ?: false
            } else {
                privileges.tsPrivileges?.tripSheetCollectionOptionsInTSAppReservationChart
                    ?: false
            }

            if(!privileges.country.isNullOrEmpty()) {
                country = privileges.country
            }

        } else {
            context.toast(context.getString(R.string.server_error))
        }

        holder.agentName.text = searchModel.booking_source
        holder.seatCount.text = searchModel.total_seats.toString()
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val collectionChildAdapter = CollectionChildByAgentAdapter(
            context = context,
            this,
            searchModel.passenger_details,
            privileges,
            tripSheetCollectionOptionsInTSAppReservationChart
        )
        layoutManager.initialPrefetchItemCount = searchModel.passenger_details.size
        holder.rvNestedItems.layoutManager = layoutManager
        holder.rvNestedItems.adapter = collectionChildAdapter
        holder.rvNestedItems.setRecycledViewPool(viewPool)
        if(tripSheetCollectionOptionsInTSAppReservationChart && country.equals("india", true)) {
            holder.fromToTitle.visible()
//            adjustHeaderWeights(holder, true)
        } else {
            holder.fromToTitle.gone()
//            adjustHeaderWeights(holder, false)
        }
    }

    private fun adjustHeaderWeights(holder: ViewHolder, isFromToVisible: Boolean) {
        val parentLayout = holder.fareTitle.parent as LinearLayout
        parentLayout.weightSum = if (isFromToVisible) 5f else 4f

        updateHeaderWeight(holder.fareTitle, 1f)
        updateHeaderWeight(holder.nameTitle, 2.5f)
        updateHeaderWeight(holder.seatNoTitle, 1f)
    }

    private fun updateHeaderWeight(view: View, weight: Float) {
        val params = view.layoutParams as LinearLayout.LayoutParams
        params.weight = weight
        view.layoutParams = params
    }

    class ViewHolder(binding: ChildCollectionAgentBinding) : RecyclerView.ViewHolder(binding.root) {
        val rvNestedItems = binding.rvNestedItems
        val agentName = binding.tvAgentName
        val seatCount = binding.seatCount
        val seatamount = binding.seatAmount
        val fromToTitle = binding.tvFromToTitle
        val fareTitle = binding.tvFareTitle
        val nameTitle = binding.tvNameTitle
        val seatNoTitle = binding.tvSeatNoTitle

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