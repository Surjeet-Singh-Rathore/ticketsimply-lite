package com.bitla.ts.utils.dialog

import android.animation.*
import android.annotation.*
import android.app.*
import android.app.AlertDialog
import android.content.*
import android.content.res.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.text.*
import android.util.*
import android.view.*
import android.widget.*
import android.widget.PopupMenu
import androidx.annotation.*
import androidx.appcompat.app.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.*
import androidx.core.content.*
import androidx.core.text.*
import androidx.core.view.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.*
import androidx.transition.*
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.BpDpService.response.PassengerDetail
import com.bitla.ts.domain.pojo.add_rate_card.fetchRouteWiseFareDetails.response.*
import com.bitla.ts.domain.pojo.add_rate_card.fetchShowRateCard.response.*
import com.bitla.ts.domain.pojo.add_rate_card.viewRateCard.response.*
import com.bitla.ts.domain.pojo.all_coach.response.*
import com.bitla.ts.domain.pojo.available_routes.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.block_unblock_reservation.ReasonList
import com.bitla.ts.domain.pojo.booking.PayGayType
import com.bitla.ts.domain.pojo.city_details.response.*
import com.bitla.ts.domain.pojo.crew_toolkit.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.destination_pair.Origin
import com.bitla.ts.domain.pojo.employees_details.response.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.my_bookings.response.Filter
import com.bitla.ts.domain.pojo.pickup_chart_crew_details.response.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.child_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.domain.pojo.recommended_seats.response.*
import com.bitla.ts.domain.pojo.redelcom.*
import com.bitla.ts.domain.pojo.service_details_response.*
import com.bitla.ts.domain.pojo.ticket_details.response.*
import com.bitla.ts.domain.pojo.ticket_details.response.Body
import com.bitla.ts.domain.pojo.ticket_details_menu.*
import com.bitla.ts.domain.pojo.update_rate_card.multistation_wise_fare.response.*
import com.bitla.ts.domain.pojo.user.*
import com.bitla.ts.domain.pojo.view_reservation.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.adapter.NewSortByAdaper.MultiSeatLuggageAdapter
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.putObject
import com.bitla.tscalender.*
import com.bumptech.glide.*
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.*
import com.google.android.material.slider.*
import com.kizitonwose.calendarview.model.*
import com.kizitonwose.calendarview.ui.*
import com.kizitonwose.calendarview.utils.*
import daysOfWeekFromLocale
import gone
import invisible
import layoutInflater
import setMaxLength
import setSafeOnClickListener
import setTextColorRes
import timber.log.*
import toast
import visible
import java.text.*
import java.time.*
import java.time.format.*
import java.util.*
import java.util.concurrent.*
import kotlin.math.*


class DialogUtils {
    companion object : SeatSelectionAdapter.Callback,
        SlyCalendarDialog.Callback, OnItemClickListener, OnItemCheckedMultipledataListner {

        var progressDialog: AlertDialog? = null
        private var otherPaymentPosition: Int = 0
        var tag: String? = DialogUtils::class.simpleName
        private lateinit var dialogPhoneBlockingBinding: DialogPhoneBlockingBinding
        private lateinit var dialogUnblockSeatsBinding: DialogUnblockSeatsBinding
        private var dialogUnblockSeatsContext: Context? = null

        lateinit var seatSelectionAdapter: SeatSelectionAdapter
        private var IsAnnouncementOn: Boolean = true
        private var filterItemName: String = "None"
        private var animationDuration: Long = 1500
        private var animAlphaHide = 0.0f
        private var animAlphaUnhide = 1.0f
        private var lastCheckedPos = 0
        private var count = 0
        private var list = arrayListOf<String>()
        private lateinit var dialogSingleListener: DialogSingleButtonListener
        private lateinit var partialPaymentListener: VarArgListener
        val seatlist = arrayListOf<String>()
        var phoneBlockHH: String = ""
        var phoneBlockMM: String = ""
        var phoneBlockAMPM: String = ""
        var phoneBlockDate: String = ""
        var finalBoardingPoint: MutableList<StageDetail> = mutableListOf()
        var finalDropingingPoint: MutableList<StageDetail> = mutableListOf()
        var finalBpSelected: BoardingPointDetail? = null
        var oldBpSelected: BoardingPointDetail? = null
        var finalDpSelected: DropOffDetail? = null
        var oldDpSelected: DropOffDetail? = null
        lateinit var doneButton: Button

        var lastPositionOnCancelButton = 0
        var flagUnblockSeat = false
        private var fromDate: String? = null
        private var toDate: String? = null
        private var dateType: String? = null
        private lateinit var privilegeResponseModel: PrivilegeResponseModel
        private var loginModelPref: LoginModel = LoginModel()
        var popupWindowX: PopupWindow? = null
        private val crewNameList : MutableList<Origin> = arrayListOf()
        private var adapterSearchBpdpBinding: AdapterSearchBpdpBinding? = null

        fun dialogChartPopup(
            context: Context,
            onItemClickListener: OnItemClickListener,
            isHideBarChart: Boolean? = true,
            isHideLineChart: Boolean? = true,
            isHidePieChard: Boolean? = false
        ) {

            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogChartPopupBinding =
                DialogChartPopupBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            if (isHideBarChart == true) {
                binding.rbBarChar.visible()
            } else {
                binding.rbBarChar.gone()
            }
            if (isHideLineChart == true) {
                binding.rbLineChart.visible()
            } else {
                binding.rbLineChart.gone()
            }
            if (isHidePieChard == true) {
                binding.rbPieChart.visible()
            } else {
                binding.rbPieChart.gone()
            }

            val selectedChartId =
                PreferenceUtils.getPreference(context.getString(R.string.selectedChartId), 0)
            Timber.d("statusSelected : $selectedChartId")

            when (selectedChartId) {
                0 -> binding.radiogroup.check(R.id.rbBarChar)
                1 -> binding.radiogroup.check(R.id.rbPieChart)
                2 -> binding.radiogroup.check(R.id.rbLineChart)

            }

            var position = 0
            binding.rbBarChar.setOnClickListener {
                position = 0
            }
            binding.rbPieChart.setOnClickListener {
                position = 1
            }
            binding.rbLineChart.setOnClickListener {
                position = 2
            }

            binding.btnChangeChart.setOnClickListener {
                builder.cancel()
                onItemClickListener.onClickOfItem("$position", position)
            }

            binding.btnCancel.setOnClickListener {
//                onItemClickListener.onClickOfItem(selectedChartId.toString(), 0)
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun dialogFilterBy(
            context: Context,
            //originCityList: MutableList<com.bitla.ts.domain.pojo.city_details.response.Result>?
        ) {

            /*val originSpinnerList = mutableListOf<SpinnerItems>()
            originCityList?.forEach {
                if(it.id!= null && it.name !=null) {
                    originSpinnerList.add(SpinnerItems(it.id, it.name))
                }
            }*/

            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogFilterByBinding =
                DialogFilterByBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.btnDark.setOnClickListener {
                builder.cancel()

                binding.btnLight.setOnClickListener {
                    builder.cancel()
                }
            }
            /*binding.etSelectOrigin.setAdapter(
                ArrayAdapter(
                    context,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    originSpinnerList
                )
            )*/

            builder.setView(binding.root)
            builder.show()
        }

        fun dialogFilterByNew(
            context: Context,
            originCityList: MutableList<com.bitla.ts.domain.pojo.city_details.response.Result>?,
            originCity: String,
            destinationCity: String,
            fromDate: String,
            toDate: String,
            isSelectDate: Boolean,
            isBranchFilter: Boolean,
            onclickitemMultiView: OnclickitemMultiView,
            buttonSingleButtonListener: DialogSingleButtonListener,
            onItemPassData: OnItemPassData,
            onViewPass: OnViewPass? = null
        ) {

            val originSpinnerList = mutableListOf<SpinnerItems>()
            originCityList?.forEach {
                if (it.id != null && it.name != null) {
                    originSpinnerList.add(SpinnerItems(it.id, it.name))
                }
            }

            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogFilterByBinding =
                DialogFilterByBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.selectOriginSubHeaderLabel.gone()
            binding.selectDestinationSubHeaderLabel.gone()

            var validationText = "Select Services"
            if (isBranchFilter) {
                binding.selectServiceSubHeaderLabel.hint = "Select Branch"
                validationText = "Select Branch"
            }
            if (isSelectDate) {
                binding.layoutFromDateUrc.gone()
                binding.layoutToDateUrc.gone()
            } else {
                binding.layoutSelectDateUrc.gone()
            }

            binding.etSelectOrigin.setText(originCity)
            binding.etSelectDestination.setText(destinationCity)
            binding.etFromDateUrc.setText(fromDate)
            binding.etToDateUrc.setText(toDate)
            binding.etSelectDateUrc.setText(fromDate)
            binding.etSelectService.setText(originCity)
            onViewPass?.onViewPass(binding.etFromDateUrc, binding.etToDateUrc)
            binding.btnDark.setOnClickListener {
                buttonSingleButtonListener.onSingleButtonClick("cancel")
                builder.dismiss()
            }
            binding.btnLight.setOnClickListener {
                /*if (binding.etSelectOrigin.text.isEmpty()) {
                    context.toast("Enter Origin")
                } else if (binding.etSelectDestination.text.isEmpty()) {
                    context.toast("Enter Destination")
                }*/
                if (binding.etSelectService.text.isEmpty()) {
                    context.toast(validationText)
                    return@setOnClickListener
                } /*else if (binding.etFromDateUrc.text.isNullOrEmpty()) {
                    context.toast("Enter From Date")
                } else if (binding.etToDateUrc.text.isNullOrEmpty()) {
                    context.toast("Enter To Date")
                }*/ else {
                    if (isSelectDate) {
                        if (binding.etSelectDateUrc.text.isNullOrEmpty()) {
                            context.toast("Enter Date")
                            return@setOnClickListener
                        }
                    } else {
                        if (binding.etFromDateUrc.text.isNullOrEmpty()) {
                            context.toast("Enter From Date")
                            return@setOnClickListener
                        } else if (binding.etToDateUrc.text.isNullOrEmpty()) {
                            context.toast("Enter To Date")
                            return@setOnClickListener
                        }
                    }
                }
                buttonSingleButtonListener.onSingleButtonClick("filter")
                builder.cancel()
            }
            binding.etSelectOrigin.setOnClickListener {
                onclickitemMultiView.onClickMuliView(
                    binding.etSelectOrigin,
                    binding.etSelectDestination,
                    binding.etFromDateUrc,
                    binding.etToDateUrc,
                    "",
                    "origin"
                )
            }

            binding.selectOriginSubHeaderLabel.setEndIconOnClickListener {
                onclickitemMultiView.onClickMuliView(
                    binding.etSelectOrigin,
                    binding.etSelectDestination,
                    binding.etFromDateUrc,
                    binding.etToDateUrc,
                    "",
                    "origin"
                )
            }

            binding.etSelectDestination.setOnClickListener {
                onclickitemMultiView.onClickMuliView(
                    binding.etSelectOrigin,
                    binding.etSelectDestination,
                    binding.etFromDateUrc,
                    binding.etToDateUrc,
                    "",
                    "destination"
                )
            }

            binding.selectDestinationSubHeaderLabel.setEndIconOnClickListener {
                onclickitemMultiView.onClickMuliView(
                    binding.etSelectOrigin,
                    binding.etSelectDestination,
                    binding.etFromDateUrc,
                    binding.etToDateUrc,
                    "",
                    "destination"
                )
            }

            binding.etSelectService.setOnClickListener {
                if (isSelectDate) {
                    onclickitemMultiView.onClickMuliView(
                        binding.etSelectService,
                        binding.etSelectDestination,
                        binding.etSelectDateUrc,
                        binding.etSelectDateUrc,
                        "",
                        "selectServices"
                    )
                } else {
                    onclickitemMultiView.onClickMuliView(
                        binding.etSelectService,
                        binding.etSelectDestination,
                        binding.etFromDateUrc,
                        binding.etToDateUrc,
                        "",
                        "selectServices"
                    )
                }

            }

            binding.selectServiceSubHeaderLabel.setEndIconOnClickListener {
                if (isSelectDate) {
                    onclickitemMultiView.onClickMuliView(
                        binding.etSelectService,
                        binding.etSelectDestination,
                        binding.etSelectDateUrc,
                        binding.etSelectDateUrc,
                        "",
                        "selectServices"
                    )
                } else {
                    onclickitemMultiView.onClickMuliView(
                        binding.etSelectService,
                        binding.etSelectDestination,
                        binding.etFromDateUrc,
                        binding.etToDateUrc,
                        "",
                        "selectServices"
                    )
                }

            }

            binding.etFromDateUrc.setOnClickListener {
                onItemPassData.onItemData(
                    binding.etFromDateUrc,
                    binding.etFromDateUrc.text.toString(),
                    ""
                )
            }

            binding.layoutFromDateUrc.setEndIconOnClickListener {
                onItemPassData.onItemData(
                    binding.etFromDateUrc,
                    binding.etFromDateUrc.text.toString(),
                    ""
                )
            }

            binding.etToDateUrc.setOnClickListener {
                onItemPassData.onItemDataMore(
                    binding.etToDateUrc,
                    binding.etFromDateUrc.text.toString(),
                    "",
                    ""
                )
            }

            binding.layoutToDateUrc.setEndIconOnClickListener {
                onItemPassData.onItemDataMore(
                    binding.etToDateUrc,
                    binding.etFromDateUrc.text.toString(),
                    "",
                    ""
                )
            }

            binding.etSelectDateUrc.setOnClickListener {
                onItemPassData.onItemData(
                    binding.etSelectDateUrc,
                    binding.etSelectDateUrc.text.toString(),
                    ""
                )
            }

            builder.setView(binding.root)
            builder.show()
        }


        fun dialogRedelcomDetails(
            context: Context,
            data: RedelcomPreferenceData
        ) {

            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogRedelcomDetailsBinding =
                DialogRedelcomDetailsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.apiKeyTV.text = "Api key : " + data.api_key
            binding.urlTV.text = "Url : " + data.redelcom_uri
            binding.terminalIdTV.text = "Terminal : " + data.terminalId
            binding.clientIDTV.text = "Client Id : " + data.client_id

            binding.cancelTV.setOnClickListener {
                builder.cancel()

            }


            builder.setView(binding.root)
            builder.show()
        }



        fun dialogServiceFilter(
            context: Context,
            dashboardServiceFilterConf: DashboardServiceFilterConf,
            onApplyFilter: ((
                fromCityId: String?,
                fromCityName: String?,
                toCityId: String?,
                toCityName: String?,
                hubId: String?,
                hubName: String?,
                isHubSelected: Boolean
            ) -> Unit)

        ): AlertDialog {
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogServiceFilterBinding =
                DialogServiceFilterBinding.inflate(LayoutInflater.from(context))

            if (dashboardServiceFilterConf.fromId == "") {
                dashboardServiceFilterConf.fromId = "-1"
            }

            if (dashboardServiceFilterConf.toId == "") {
                dashboardServiceFilterConf.toId = "-1"
            }

            var originId = dashboardServiceFilterConf.fromId
            var originName = dashboardServiceFilterConf.fromTitle
            var destinationId = dashboardServiceFilterConf.toId
            var destinationName = dashboardServiceFilterConf.toTitle
            var hubId = dashboardServiceFilterConf.hubId
            var hubName = dashboardServiceFilterConf.toTitle

            var hubsList: MutableList<HubDetails>? = mutableListOf()
            var tempHubsList = mutableListOf<String>()

            var privilegeResponse: PrivilegeResponseModel?= null
            if ((context as BaseActivity).getPrivilegeBase() != null) {
                privilegeResponse = (context as BaseActivity).getPrivilegeBase()
                privilegeResponse?.let {
                    if (!privilegeResponse.hubDetails.isNullOrEmpty()) {
                        hubsList?.addAll(privilegeResponse.hubDetails!!)
                    }
                }
            } else {
                context.toast(context.getString(R.string.server_error))
            }

            /*val allHubs = HubDetails(0, "All Hubs")
            hubsList?.add(allHubs)*/

            if (hubsList != null) {
                tempHubsList = hubsListToStringList(hubsList)
            }


            val cityListModel = PreferenceUtils.getObject<CityDetailsResponseModel>("cityListModel")

            if (dashboardServiceFilterConf.fromTitle == "All Cities") {
                dashboardServiceFilterConf.fromTitle = context.getString(R.string.all_cities)
            }

            if (dashboardServiceFilterConf.toTitle == "All Cities") {
                dashboardServiceFilterConf.toTitle = context.getString(R.string.all_cities)
            }

            val allCity = com.bitla.ts.domain.pojo.city_details.response.Result(
                -1,
                context.getString(R.string.all_cities)
            )
            cityListModel?.result?.add(0, allCity)

            val cityList = cityListModel?.result

            if (dashboardServiceFilterConf.isHub) {
                binding.layoutFromTo.gone()
                binding.layoutHub.visible()
                binding.chkHub.isChecked = true
                binding.etSelectHub.setText(dashboardServiceFilterConf.hubTitle)
            } else {
                binding.layoutFromTo.visible()
                binding.layoutHub.gone()
                binding.chkFromTo.isChecked = true
                binding.etSelectFrom.setText(dashboardServiceFilterConf.fromTitle)
                binding.etSelectTo.setText(dashboardServiceFilterConf.toTitle)
            }

            if (privilegeResponse?.hubDetails.isNullOrEmpty()) {
                binding.layoutHub.gone()
                binding.chkHub.gone()
            }

            if (cityList != null) {

                var fromList =
                    mutableListOf<com.bitla.ts.domain.pojo.city_details.response.Result>()
                var toList = mutableListOf<com.bitla.ts.domain.pojo.city_details.response.Result>()
                var fromCityAdapter: CityListAdapter2? = null
                var toCityAdapter: CityListAdapter2? = null

                if (dashboardServiceFilterConf.fromId == "-1" && dashboardServiceFilterConf.toId == "-1") { // If "All Cities" is not selected

                    cityList.forEach {
                        fromList.add(it)
                        toList.add(it)
                    }
                } else {
                    cityList.forEach {
                        if (it.id.toString() != "-1") {
                            if (it.id.toString() != dashboardServiceFilterConf.toId) {
                                fromList.add(it)
                            }

                            if (it.id.toString() != dashboardServiceFilterConf.fromId) {
                                toList.add(it)
                            }
                        } else {
                            fromList.add(it)
                            toList.add(it)
                        }
                    }
                }

                fromCityAdapter = CityListAdapter2(
                    context,
                    fromList,
                    onItemSelected = { itemId, position, itemName ->

                        binding.etSelectFrom.setText(itemName)
                        //binding.etSelectTo.setText("")
                        originId = itemId.toString()
                        originName = itemName

                        val newToCityList =
                            mutableListOf<com.bitla.ts.domain.pojo.city_details.response.Result>()

                        if (itemId != -1) { // If "All Cities" is not selected
                            cityList.forEach {
                                if (it.id != itemId) {
                                    newToCityList.add(it)
                                }
                            }
                        } else {
                            cityList.forEach {
                                newToCityList.add(it)
                            }
                        }

                        toCityAdapter?.updateList(newToCityList)
                        binding.etSelectFrom.dismissDropDown()
                    }
                )

                toCityAdapter = CityListAdapter2(
                    context,
                    toList,
                    onItemSelected = { itemId, position, itemName ->

                        //binding.etSelectFrom.setText("")
                        binding.etSelectTo.setText(itemName)
                        destinationId = itemId.toString()
                        destinationName = itemName

                        val newFromCityList =
                            mutableListOf<com.bitla.ts.domain.pojo.city_details.response.Result>()

                        if (itemId != -1) { // If "All Cities" is not selected
                            cityList.forEach {
                                if (it.id != itemId) {
                                    newFromCityList.add(it)
                                }
                            }
                        } else {
                            cityList.forEach {
                                newFromCityList.add(it)
                            }
                        }

                        fromCityAdapter.updateList(newFromCityList)
                        binding.etSelectTo.dismissDropDown()
                    }
                )
                binding.etSelectFrom.setAdapter(fromCityAdapter)
                binding.etSelectTo.setAdapter(toCityAdapter)

            }

            if (hubsList?.isNotEmpty() == true) {
                binding.etSelectHub.setAdapter(
                    ArrayAdapter(
                        context,
                        R.layout.spinner_dropdown_item,
                        R.id.tvItem,
                        tempHubsList
                    )
                )

                binding.etSelectHub.setOnItemClickListener { parent, view, position, id ->
                    val item = hubsList[position]

                    hubId = item.id.toString()
                    hubName = item.label.toString()
                    binding.etSelectHub.setText(item.label.toString(), false)

                }
            }

            binding.chkHub.setOnClickListener {
                if (binding.chkHub.isChecked.not()) {
                    binding.layoutHub.gone()
                    binding.layoutFromTo.visible()
                } else {
                    binding.layoutHub.visible()
                    binding.layoutFromTo.gone()
                }
            }

            binding.chkFromTo.setOnClickListener {

                if (binding.chkFromTo.isChecked.not()) {
                    binding.layoutFromTo.gone()
                    binding.layoutHub.visible()
                } else {
                    binding.layoutFromTo.visible()
                    binding.layoutHub.gone()
                }

            }

            binding.tvClearAll.setOnClickListener {

                originId = ""
                destinationId = ""
                hubId = ""

                binding.etSelectHub.setText("")
                binding.etSelectFrom.setText("")
                binding.etSelectTo.setText("")


            }

            binding.btnApplyFilter.setOnClickListener {


                if (binding.chkFromTo.isChecked) {
                    if (binding.etSelectFrom.text.toString() == "") {
                        context.toast(context.getString(R.string.please_select_from_city))
                    } else if (binding.etSelectTo.text.toString() == "") {
                        context.toast(context.getString(R.string.please_select_to_city))
                    } else {

                        if (originId == "-1") {
                            originId = ""
                        }

                        if (destinationId == "-1") {
                            destinationId = ""
                        }
                        onApplyFilter.invoke(
                            originId,
                            originName,
                            destinationId,
                            destinationName,
                            "",
                            "",
                            false,

                            )
                        builder.dismiss()

                    }

                } else {
                    if (binding.etSelectHub.text.toString() == "") {
                        context.toast(context.getString(R.string.please_select_hub))
                    } else {
                        onApplyFilter.invoke(
                            "",
                            "",
                            "",
                            "",
                            hubId,
                            hubName,
                            true,
                        )
                        builder.dismiss()

                    }
                }

            }

            builder.setCancelable(true)
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun dialogDateFilter(
            context: Context,
            defaultSelection: Int,
            todayDate: String,
            fromDate: String?,
            toDate: String?,
            isBeforeFromDateSelection: Boolean,
            isAfterToDateSelection: Boolean,
            isAfterFromDateSelection: Boolean,
            hideYesterdayDateFilter: Boolean,
            hideTodayDateFilter: Boolean,
            hideTomorrowDateFilter: Boolean,
            hideLast7DaysDateFilter: Boolean,
            hideLast30DaysDateFilter: Boolean,
            hideCustomDateFilter: Boolean,
            hideCustomDateRangeFilter: Boolean,
            isCustomDateFilterSelected: Boolean,
            isCustomDateRangeFilterSelected: Boolean,
            fragmentManager: FragmentManager,
            tag: String,
            onApply: ((finalFromDate: String?, finalToDate: String?, lastSelectedItem: Int, isCustomDateFilter: Boolean, isCustomDateRangeFilter: Boolean) -> Unit),
            isViewByLayoutVisible: Boolean = false,
            selectedViewFilter: String = "doj"
        ) {
            //var sortByValue = sortBy
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogRadioDateFilterBinding =
                DialogRadioDateFilterBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            val filterList = getDateFilter(
                context,
                todayDate,
                fromDate,
                toDate,
                DATE_FORMAT_Y_M_D,
                DATE_FORMAT_D_M_YY,
                isCustomDateFilterSelected,
                isCustomDateRangeFilterSelected
            )

            if (hideYesterdayDateFilter) {
                val index = filterList.indexOfFirst {
                    it.id == 0
                }
                if (index != -1) {
                    filterList.removeAt(index)
                }
            }

            if (hideTodayDateFilter) {
                val index = filterList.indexOfFirst {
                    it.id == 1
                }
                if (index != -1) {
                    filterList.removeAt(index)
                }
            }

            if (hideTomorrowDateFilter) {
                val index = filterList.indexOfFirst {
                    it.id == 2
                }
                if (index != -1) {
                    filterList.removeAt(index)
                }
            }

            if (hideLast7DaysDateFilter) {
                val index = filterList.indexOfFirst {
                    it.id == 3
                }
                if (index != -1) {
                    filterList.removeAt(index)
                }
            }

            if (hideLast30DaysDateFilter) {
                val index = filterList.indexOfFirst {
                    it.id == 4
                }
                if (index != -1) {
                    filterList.removeAt(index)
                }
            }

            if (hideCustomDateFilter) {
                val index = filterList.indexOfFirst {
                    it.id == 5
                }
                if (index != -1) {
                    filterList.removeAt(index)
                }

            }

            if (hideCustomDateRangeFilter) {
                val index = filterList.indexOfFirst {
                    it.id == 6
                }
                if (index != -1) {
                    filterList.removeAt(index)
                }

            }
            if (selectedViewFilter == "doj") {
                binding.dojRB.isChecked = true
                binding.doiRB.isChecked = false
            } else {
                binding.dojRB.isChecked = false
                binding.doiRB.isChecked = true
            }

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvDatesAdapter.layoutManager = layoutManager

            var tempToDate: String? = null
            var tempFromDate: String? = null
            var lastSelectedPosition = defaultSelection
            var tempIsCustomDateFilterSelected = isCustomDateFilterSelected
            var tempIsCustomDateRangeFilterSelected = isCustomDateRangeFilterSelected
            val radioDateFilterAdapter = RadioDateFilterAdapter(
                context = context,
                list = filterList,
                defaultSelection = defaultSelection,
                todayDate = todayDate,
                fromDate = fromDate,
                toDate = toDate,
                isBeforeFromDateSelection = isBeforeFromDateSelection,
                isAfterFromDateSelection = isAfterFromDateSelection,
                isAfterToDateSelection = isAfterToDateSelection,
                hideCustomDateFilter = hideCustomDateFilter,
                hideCustomDateRangeFilter = hideCustomDateRangeFilter,
                isCustomDateFilterSelected = tempIsCustomDateFilterSelected,
                isCustomDateRangeFilterSelected = tempIsCustomDateRangeFilterSelected,
                fragmentManager = fragmentManager,
                tag = tag,
                onDatesSelected = { newSelectedFromDate, newSelectedToDate, lastSelectedItem, isCustomDateFilter, isCustomDateRangeFilter ->
                    tempFromDate = newSelectedFromDate
                    tempToDate = newSelectedToDate
                    lastSelectedPosition = lastSelectedItem
                    tempIsCustomDateFilterSelected = isCustomDateFilter
                    tempIsCustomDateRangeFilterSelected = isCustomDateRangeFilter

                }

            )

            if (isViewByLayoutVisible) {
                binding.viewByLL.visible()
            } else {
                binding.viewByLL.gone()
            }

            binding.rvDatesAdapter.adapter = radioDateFilterAdapter

            binding.btnCancel.setOnClickListener {
                builder.cancel()
            }


            binding.btnApply.setOnClickListener {
                //singleButtonListener.onSingleButtonClick(sortByValue)
                val diff = getDaysDifference(
                    tempFromDate ?: todayDate,
                    tempToDate ?: todayDate,
                    DATE_FORMAT_Y_M_D
                )
                if (binding.viewByLL.visibility == View.VISIBLE) {
                    tempFromDate += "/${binding.viewByRG.checkedRadioButtonId}"
                }
                if (diff < 31) {
                    onApply.invoke(
                        tempFromDate,
                        tempToDate,
                        lastSelectedPosition,
                        tempIsCustomDateFilterSelected,
                        tempIsCustomDateRangeFilterSelected,
                    )
                    builder.cancel()
                } else {
                    context.toast("Date Range Should be less than or equal to 31 Days")
                }
            }

            builder.setView(binding.root)
            builder.show()

        }


        fun twoButtonDialog(
            context: Context,
            title: String,
            message: String,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonListener: DialogButtonListener,
        ) {

            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: DialogTwoButtonsBinding =
                DialogTwoButtonsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTitle.text = title
            binding.tvContent.text = message
            binding.btnLeft.text = buttonLeftText
            binding.btnRight.text = buttonRightText
            binding.btnLeft.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onLeftButtonClick()
                //finish()
            }
            binding.btnRight.tag = context.getString(R.string.cancel_dialog_go_back)
            binding.btnRight.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onRightButtonClick()
                //finish()
            }
            //builder.setView(dialogLayout)
            builder.setView(binding.root)
            builder.show()
        }

        fun unAuthorizedDialog(
            context: Context,
            message: String,
            singleButtonListener: DialogSingleButtonListener
        ) {

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogUnauthorizedBinding =
                DialogUnauthorizedBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvContent.text = message
            binding.btnOk.tag = context.getString(R.string.unauthorized)
            binding.btnOk.setOnClickListener {
                builder.cancel()
                singleButtonListener.onSingleButtonClick(context.getString(R.string.unauthorized))
            }
            builder.setView(binding.root)
            builder.show()
        }


//        fun updatePassengersDialog(
//            context: Context,
////            passName: String,
////            passAge: String,
////            passEmail: String,
////            passPhone: String,
////            passGender: String,
////        dialogButtonTagListener: DialogButtonTagListener
//        ) {
//            val builder = AlertDialog.Builder(context).create()
//            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
//            val binding: DialogUpdatePersonalDetailsBinding =
//                DialogUpdatePersonalDetailsBinding.inflate(LayoutInflater.from(context))
//            builder.setCancelable(false)
//
//            binding.etName.setText(passName)
//            binding.etAge.setText(passAge)
//            binding.etEmail.setText(passEmail)
//            binding.etPhoneNumber.setText(passPhone)
//            binding.autoCompleteGender.setText(passGender)
//
//
//            binding.autoCompleteGender.setAdapter(
//                ArrayAdapter(
//                    context,
//                    R.layout.spinner_dropdown_item,
//                    R.id.tvItem,
//                    context.resources.getStringArray(R.array.genderArray)
//                )
//            )
//
//            binding.btnSaveDetails.setOnClickListener {
//                passName = binding.etName.text.toString()
//                passAge = binding.etAge.text.toString()
//                passPhone = binding.etPhoneNumber.text.toString()
//                passGender = binding.etName.text.toString()
//
//                val passengerDetailData = UpdateData(
//                    passIsSingelSeat, passPhone, passName, passAge, passGender, "", ""
//                )
//                updateDataList.add(passengerDetailData)
//                builder.cancel()
//
//                Timber.d("Udata$passengerDetailData")
//            }
//            binding.tvCancel.setOnClickListener {
//                builder.cancel()
//            }
//
//            builder.setView(binding.root)
//            builder.show()
//        }

        fun twoButtonDialogUpdate(
            context: Context,
            title: String,
            message: String,
            messageTextColor: Int,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonListener: DialogButtonListener,
        ) {

            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: DialogTwoButtonsBinding =
                DialogTwoButtonsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvContent.setTextColor(messageTextColor)
            binding.tvTitle.text = title
            binding.tvContent.text = message
            binding.btnLeft.text = buttonLeftText
            binding.btnRight.text = buttonRightText
            binding.btnLeft.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onLeftButtonClick()
                //finish()
            }
            binding.btnRight.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onRightButtonClick()
                //finish()
            }
            //builder.setView(dialogLayout)
            builder.setView(binding.root)
            builder.show()
        }


        // Block Seats dialog
        fun blockSeatsDialog(
            showMsg: Boolean,
            context: Context,
            title: String,
            message: String,
            srcDest: String,
            journeyDate: String,
            noOfSeats: String,
            seatNo: String,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonListener: DialogButtonListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogBlockSeatsBinding =
                DialogBlockSeatsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            if (showMsg) {
                binding.tvMessage.visible()
                binding.viewBottom.visible()
            } else {
                binding.tvMessage.gone()
                binding.viewBottom.gone()
            }

            binding.tvHeader.text = title
            binding.tvMessage.text = message
            binding.tvHeaderText.text = srcDest
            binding.tvSubtitle.text = journeyDate
            binding.tvNoSeats.text = noOfSeats
            binding.tvSelectedSeatNo.text = seatNo
            binding.btnDark.text = buttonLeftText
            binding.btnLight.text = buttonRightText
            binding.btnDark.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onLeftButtonClick()
                //finish()
            }
            binding.btnLight.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onRightButtonClick()
                //finish()


            }
            builder.setView(binding.root)
            // builder.setView(dialogLayout)
            builder.show()
        }

        fun unblockSeatsDialog(
            context: Context?,
            title: String,
            message: String,
            srcDest: String,
            journeyDate: String,
            noOfSeats: String,
            seatNo: String,
            buttonLeftText: String,
            buttonRightText: String,
            isSelectAllPressed: Boolean,
            seatNumberPressed: String,
            dialogButtonUnblockSeatListener: DialogButtonUnblockSeatListener,
        ) {

            var unBlockTypeList: MutableList<SpinnerItems> = mutableListOf()
            var spinnerBlockItems = SpinnerItems(0, context!!.getString(R.string.none))
            unBlockTypeList.add(spinnerBlockItems)
            spinnerBlockItems = SpinnerItems(0, context.getString(R.string.permanent))
            unBlockTypeList.add(spinnerBlockItems)
            spinnerBlockItems = SpinnerItems(0, context.getString(R.string.custom))
            unBlockTypeList.add(spinnerBlockItems)

            var selectedSeatsList: MutableList<String> = mutableListOf()
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            dialogUnblockSeatsContext = context
            dialogUnblockSeatsBinding =
                DialogUnblockSeatsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            dialogUnblockSeatsBinding.tvHeader.text = title
            dialogUnblockSeatsBinding.tvMessage.text = message
            dialogUnblockSeatsBinding.tvHeaderText.text = srcDest
            dialogUnblockSeatsBinding.tvSubtitle.text = journeyDate
            dialogUnblockSeatsBinding.tvNoSeats.text = noOfSeats

            if ((context as BaseActivity).getPrivilegeBase() != null) {
                val privilegeResponse = (context as BaseActivity).getPrivilegeBase()
                if (privilegeResponse?.country.equals("Indonesia", true)) {
                    dialogUnblockSeatsBinding.unblockHeading.gone()
                    dialogUnblockSeatsBinding.textInputUnblockType.gone()
                }

            }

            if (isSelectAllPressed) {
                dialogUnblockSeatsBinding.tvSelectedSeatNo.text = seatNo
            } else {
                dialogUnblockSeatsBinding.tvSelectedSeatNo.text = seatNumberPressed
            }
            dialogUnblockSeatsBinding.btnDark.text = buttonLeftText
            dialogUnblockSeatsBinding.btnLight.text = buttonRightText
            if (isSelectAllPressed) {
                dialogUnblockSeatsBinding.selectSeatsLayout.gone()
            } else {
                dialogUnblockSeatsBinding.selectSeatsLayout.visible()
                if (selectedSeatsList.contains(seatNumberPressed).not())
                    selectedSeatsList.add(seatNumberPressed)
                dialogUnblockSeatsBinding.autoCompleteSelectSeats.setText(seatNumberPressed)
                var seatNumbers = ""

                if (selectedSeatsList.isNotEmpty()) {
                    selectedSeatsList.forEach {
                        seatNumbers += " $it,"
                    }
                    seatNumbers.trim()
                    if (seatNumbers.length > 0 && (seatNumbers.get(seatNumbers.length - 1)
                            .toString() == ",")
                    ) {
                        seatNumbers = seatNumbers.substring(0, seatNumbers.length - 1)
                    }
                    dialogUnblockSeatsBinding.autoCompleteSelectSeats.setText(seatNumbers, false)

                } else {
                    seatNumbers = ""
                    dialogUnblockSeatsBinding.autoCompleteSelectSeats.setText(seatNumbers, false)
                }

            }
            dialogUnblockSeatsBinding.autoCompleteUnblockType.setAdapter(
                ArrayAdapter<SpinnerItems>(
                    context,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    unBlockTypeList
                )
            )
            dialogUnblockSeatsBinding.autoCompleteUnblockType.setOnItemClickListener { parent, view, position, id ->
                if (dialogUnblockSeatsBinding.autoCompleteUnblockType.text.toString() == context.getString(
                        R.string.permanent
                    )
                ) {
                    dialogUnblockSeatsBinding.linearLayout1.gone()
                    dialogUnblockSeatsBinding.unblockTimeDuration.gone()
                } else if (dialogUnblockSeatsBinding.autoCompleteUnblockType.text.toString() == context.getString(
                        R.string.custom
                    )
                ) {
                    dialogUnblockSeatsBinding.linearLayout1.visible()
                    dialogUnblockSeatsBinding.unblockTimeDuration.visible()
                }
            }
            dialogUnblockSeatsBinding.tvFromDate.setOnClickListener {
                dateType = context.getString(R.string.fromDate)
                if (dialogUnblockSeatsBinding.tvToDate.text != context.getString(R.string.toDate)) {
                    dialogUnblockSeatsBinding.tvToHint.gone()
                    dialogUnblockSeatsBinding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)
                    dialogUnblockSeatsBinding.tvToDate.text = context.getString(R.string.toDate)
                    val scale = context.resources?.displayMetrics?.density ?: 0.0f
                    val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                    dialogUnblockSeatsBinding.tvToDate.setPadding(
                        paddingtLeftRightinDp,
                        0,
                        paddingtLeftRightinDp,
                        0
                    )
                }

                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show((context as AppCompatActivity).supportFragmentManager, tag)
                fromDate = dialogUnblockSeatsBinding.tvFromDate.text.toString()
                flagUnblockSeat = true
            }

            dialogUnblockSeatsBinding.tvToDate.setOnClickListener {
                dateType = dialogUnblockSeatsContext?.getString(R.string.toDate)
                val fromDate: String = dialogUnblockSeatsBinding.tvFromDate.text.toString()
                val toDate1: String = dialogUnblockSeatsBinding.tvToDate.text.toString()

                if (fromDate == dialogUnblockSeatsContext?.getString(R.string.fromDate)) {
                    dialogUnblockSeatsContext?.toast(context.getString(R.string.validate_from_date))
                } else {

                    if (fromDate != dialogUnblockSeatsContext?.getString(R.string.fromDate) && toDate1 != dialogUnblockSeatsContext?.getString(
                            R.string.toDate
                        )
                    ) // both the dates already selected
                    {
                        SlyCalendarDialog()
                            .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setEndDate(stringToDate(toDate1, DATE_FORMAT_D_M_Y))
                            .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(
                                (context as AppCompatActivity).supportFragmentManager,
                                BlockActivity.TAG
                            )
                    } else if (fromDate != dialogUnblockSeatsContext?.getString(R.string.fromDate)) // only from date selected
                    {
                        SlyCalendarDialog()
                            .setStartDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setMinDate(stringToDate(fromDate, DATE_FORMAT_D_M_Y))
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(
                                (context as AppCompatActivity).supportFragmentManager,
                                BlockActivity.TAG
                            )
                    } else {
                        SlyCalendarDialog()
                            .setSingle(false)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(
                                (context as AppCompatActivity).supportFragmentManager,
                                BlockActivity.TAG
                            )
                    }
                }
                toDate = dialogUnblockSeatsBinding.tvToDate.text.toString()

            }
            dialogUnblockSeatsBinding.btnDark.setOnClickListener {
                builder.cancel()
                dialogButtonUnblockSeatListener.onLeftButtonClick()
                //finish()
            }
            dialogUnblockSeatsBinding.btnLight.setOnClickListener {
                //builder.cancel()
                var selectionType =
                    dialogUnblockSeatsBinding.autoCompleteUnblockType.text.toString()
                var fromDate: String?
                var toDate: String?
                if (selectionType == context.getString(R.string.custom)) {
                    fromDate = dialogUnblockSeatsBinding.tvFromDate.text.toString()
                    toDate = dialogUnblockSeatsBinding.tvToDate.text.toString()
                    if (fromDate == context.getString(R.string.fromDate) || fromDate?.trim()
                            .isNullOrEmpty()
                    ) {
                        context.toast(context.getString(R.string.validate_from_date))
                        return@setOnClickListener
                    }

                    if (toDate == context.getString(R.string.toDate) || toDate?.trim()
                            .isNullOrEmpty()
                    ) {
                        context.toast(context.getString(R.string.validate_to_date))
                        return@setOnClickListener
                    }
                } else if (selectionType == context.getString(R.string.permanent)) {
                    fromDate = ""
                    toDate = ""
                    selectionType = "apply_all"
                } else {
                    fromDate = ""
                    toDate = ""
                    selectionType = ""
                }
                if (isSelectAllPressed) {
                    dialogButtonUnblockSeatListener.onRightButtonClick(
                        seatNo.trim(),
                        selectionType,
                        fromDate,
                        toDate,
                        remarks = dialogUnblockSeatsBinding.textInputEditTextRemarks.text.toString()
                    )
                    builder.cancel()
                } else {
                    var selectedSeats =
                        dialogUnblockSeatsBinding.autoCompleteSelectSeats.text.toString()
                    if (selectedSeats.isNotEmpty() && selectedSeats != null) {
                        selectedSeats = selectedSeats.replace(" ", "")
                        dialogButtonUnblockSeatListener.onRightButtonClick(
                            selectedSeats,
                            selectionType,
                            fromDate,
                            toDate,
                            remarks = dialogUnblockSeatsBinding.textInputEditTextRemarks.text.toString()
                        )
                        builder.cancel()
                    }
                }

            }

            dialogUnblockSeatsBinding.autoCompleteSelectSeats.setAdapter(
                SelectSeatsAdapter(context,
                    R.layout.spinner_dropdown_item_witch_checkbox,
                    R.id.tvItem,
                    seatNo.split(",").toMutableList(),
                    selectedSeatsList,
                    object : SelectSeatsAdapter.ItemClickListener {
                        override fun onSelected(position: Int, item: String) {
                            if (selectedSeatsList.contains(item).not())
                                selectedSeatsList.add(item)

                            dialogUnblockSeatsBinding.tvNoSeats.text =
                                selectedSeatsList.size.toString()
                            var seatNumbers = ""
                            if (selectedSeatsList.isNotEmpty()) {
                                /*if (selectedSeatsList.size > 3) {
                                    for (i in 0..3) {
                                        seatNumbers += " ${selectedSeatsList[i]},"
                                    }
                                } else {
                                    selectedSeatsList.forEach {
                                        seatNumbers += " $it,"
                                    }
                                }*/

                                selectedSeatsList.forEach {
                                    seatNumbers += " $it,"
                                }

                                seatNumbers.trim()
                                if (seatNumbers.length > 0 && (seatNumbers.get(seatNumbers.length - 1)
                                        .toString() == ",")
                                ) {
                                    seatNumbers = seatNumbers.substring(0, seatNumbers.length - 1)
                                    dialogUnblockSeatsBinding.autoCompleteSelectSeats.setText(
                                        seatNumbers,
                                        false
                                    )
                                }
                                //dialogUnblockSeatsBinding.autoCompleteSelectSeats.setText(seatNumbers,false)
                                dialogUnblockSeatsBinding.tvSelectedSeatNo.text = seatNumbers

                            } else {
                                seatNumbers = ""
                                dialogUnblockSeatsBinding.autoCompleteSelectSeats.setText(
                                    seatNumbers,
                                    false
                                )
                                dialogUnblockSeatsBinding.tvSelectedSeatNo.text = ""
                            }
                            invalidateUnblockSeatsCount(selectedSeatsList)
                        }

                        override fun onDeselect(position: Int, item: String) {
                            if (selectedSeatsList.contains(item))
                                selectedSeatsList.remove(item)
                            /*dialogUnblockSeatsBinding.autoCompleteSelectSeats.setText(
                                selectedSeatsList.firstOrNull().toString().replace("null", "")
                            )*/

                            var seatNumbers = ""
                            dialogUnblockSeatsBinding.tvNoSeats.text =
                                selectedSeatsList.size.toString()
                            if (selectedSeatsList.isNotEmpty()) {
                                if (selectedSeatsList.size > 3) {
                                    for (i in 0..3) {
                                        seatNumbers += " ${selectedSeatsList[i]},"
                                    }
                                } else {
                                    selectedSeatsList.forEach {
                                        seatNumbers += " $it,"
                                    }
                                }
                                seatNumbers.trim()
                                if (seatNumbers.length > 0 && (seatNumbers.get(seatNumbers.length - 1)
                                        .toString() == ",")
                                ) {
                                    seatNumbers = seatNumbers.substring(0, seatNumbers.length - 1)
                                    dialogUnblockSeatsBinding.autoCompleteSelectSeats.setText(
                                        seatNumbers,
                                        false
                                    )
                                }
                                //dialogUnblockSeatsBinding.autoCompleteSelectSeats.setText(seatNumbers,false)
                                dialogUnblockSeatsBinding.tvSelectedSeatNo.text = seatNumbers

                            } else {
                                seatNumbers = ""
                                dialogUnblockSeatsBinding.autoCompleteSelectSeats.setText(
                                    seatNumbers.replace("null", ""),
                                    false
                                )
                                dialogUnblockSeatsBinding.tvSelectedSeatNo.text = ""
                            }
                            invalidateUnblockSeatsCount(selectedSeatsList)
                        }

                    })
            )
            dialogUnblockSeatsBinding.autoCompleteUnblockType.setText(
                dialogUnblockSeatsBinding.autoCompleteUnblockType.adapter.getItem(
                    0
                ).toString(), false
            )

            builder.setView(dialogUnblockSeatsBinding.root)
            // builder.setView(dialogLayout)
            builder.show()
        }

        fun cancelTicketsDialog(
            context: Context,
            title: String,
            message: String,
            srcDest: String,
            journeyDate: String,
            ticketCancellationPercentage: String,
            seatNo: String,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonListener: DialogButtonListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogCancelTicketBinding =
                DialogCancelTicketBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvHeader.text = title
//            binding.tvHeader.setTextColor(context.getColor(R.color.black))

            binding.tvMessage.text = message
            binding.tvHeaderText.text = srcDest
            binding.tvSubtitle.text = journeyDate
            binding.tvTicketCancellationPercentage.text = ticketCancellationPercentage
            binding.tvSelectedSeatNo.text = seatNo
            binding.btnDark.text = buttonLeftText
            binding.btnLight.text = buttonRightText
            binding.btnDark.setOnClickListener {
                builder.dismiss()
                dialogButtonListener.onLeftButtonClick()
                //finish()
            }
            binding.btnLight.setOnClickListener {
                builder.dismiss()
                dialogButtonListener.onRightButtonClick()
                //finish()
            }
            builder.setView(binding.root)
            // builder.setView(dialogLayout)
            builder.show()
        }

        fun deletePassengerDialog(
            context: Context,
            title: String,
            seatNo: String,
            srcDest: String,
            busDetails: String,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonListener: DialogButtonListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: DialogDeletePassengerBinding =
                DialogDeletePassengerBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvHeader.text = title
            binding.tvSourceDestination.text = srcDest
            binding.tvBusDetails.text = busDetails
            binding.tvSeatNumber.text = seatNo
            binding.btnLeft.text = buttonLeftText
            binding.btnRight.text = buttonRightText

            binding.btnLeft.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onLeftButtonClick()
            }

            binding.btnRight.setOnClickListener {
                dialogButtonListener.onRightButtonClick()
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun enableDisableView(v: View, enabled: Boolean) {
            if (v is ViewGroup) {
                val vg = v
                for (i in 0 until vg.childCount) {
                    enableDisableView(vg.getChildAt(i), enabled)
                }
            }
            v.isEnabled = enabled
        }


        fun showNetworkError(offlineView: ViewGroup, onlineView: ViewGroup) {

            onlineView.apply {
                animate()
                    .alpha(animAlphaUnhide)
            }

            val transition: Transition = Fade().apply {
                duration = animationDuration
                addTarget(offlineView)
            }
            TransitionManager.beginDelayedTransition(offlineView, transition)

            offlineView.visible()
        }

        fun showNetworkBackOnline(offlineView: ViewGroup, onlineView: View) {
            onlineView.apply {
                visible()
                animate()
                    .alpha(animAlphaHide)
                    .setDuration(animationDuration)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            onlineView.gone()
                        }
                    })
            }
            offlineView.gone()
        }

        fun UpdateRcDialoge(
            context: Context,
            title: String,
            message: String,
            increaseby: String,
            increaseOrDecreaseByLabel: String,
            fromDate: String,
            toDate: String,
            srcDest: String,
            journeyDate: String,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonListener: DialogButtonListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
//            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogeupdateRcBinding =
                DialogeupdateRcBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvHeader.text = title
            binding.tvMessage.text = message
            binding.tvHeaderText.text = srcDest
            binding.tvSubtitle.text = journeyDate
            binding.increaseBy.text = increaseby
            binding.increaseOrDecreaseByLabel.text = increaseOrDecreaseByLabel
            binding.tvFromDate.text = fromDate
            binding.tvToDate.text = toDate

            binding.btnDark.text = buttonLeftText
            binding.btnLight.text = buttonRightText

            binding.btnDark.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onLeftButtonClick()
                builder.cancel()
            }

            binding.btnLight.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onRightButtonClick()
//                finish()
            }
            builder.setView(binding.root)
            builder.show()
        }

//        fun announcementMessage(
//            context: Context,
//            title: String,
//            message: String,
//            buttonRightText: String,
//            dialogButtonListener: DialogButtonListener
//        ) {
//            var announcement: TextToSpeech? = null
//            announcement = TextToSpeech(context) { i ->
//                if (i == TextToSpeech.SUCCESS) {
//                    val result = announcement!!.setLanguage(Locale.US)
//                    if (result == TextToSpeech.LANG_MISSING_DATA
//                        || result == TextToSpeech.LANG_NOT_SUPPORTED
//                    ) {
//                        context.toast("Language Not Supported")
//                    } else {
////                        mButtonSpeak!!.isEnabled = true
//                    }
//                } else {
//                    context.toast("TextToSpeech Failed")
//                }
//            }
//            val builder = AlertDialog.Builder(context).create()
//            val inflater = LayoutInflater.from(context)
//            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
//            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
//            val binding: DialogAnnouncementMsgBinding =
//                DialogAnnouncementMsgBinding.inflate(LayoutInflater.from(context))
//            builder.setCancelable(false)
//            binding.header.text = title
//            binding.msgContext.text = message
//            binding.btnStopPlaying.text = buttonRightText
//
//            binding.imgAnnouncementPlay.setOnClickListener {
//                dialogButtonListener.onLeftButtonClick()
//
//                val toSpeak = binding.msgContext.text.toString()
//                if (IsAnnouncementOn) {
//                    binding.imgAnnouncementPlay.setImageResource(R.drawable.ic_pause)
//                    announcement.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
//                    binding.progressBar.max = 1000
//                    ObjectAnimator.ofInt(binding.progressBar, "progress", 1000)
//                        .setDuration(10000)
//                        .start()
//                    IsAnnouncementOn = false
//                } else {
//                    binding.imgAnnouncementPlay.setImageResource(R.drawable.ic_play)
//                    announcement.stop()
//                    IsAnnouncementOn = true
//                    ObjectAnimator.ofInt(binding.progressBar, "progress", 0)
//                        .setDuration(0)
//                        .start()
//
//                }
//            }
//
//            binding.btnStopPlaying.setOnClickListener {
//                dialogButtonListener.onRightButtonClick()
//
//                builder.cancel()
//                announcement.stop()
//                announcement.shutdown()
//
//
//            }
//
//            builder.setView(binding.root)
//            // builder.setView(dialogLayout)
//            builder.show()
//        }


        fun lockChartDialog(
            context: Context,
            title: String,
            message: String,
            tripRoute: String,
            journeyDetails: String,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonTagListener: DialogButtonTagListener,
            view: View,
        ) {
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogBlockChartBinding =
                DialogBlockChartBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvHeader.text = title
            binding.tvMessage.text = message
            binding.tvHeaderText.text = tripRoute
            binding.tvSubtitle.text = journeyDetails
            binding.btnDark.text = buttonLeftText
            binding.btnLight.text = buttonRightText
            binding.btnDark.setOnClickListener {
                dialogButtonTagListener.onLeftButtonClick(view)
                builder.dismiss()
                builder.cancel()
            }
            binding.btnLight.setOnClickListener {
                dialogButtonTagListener.onRightButtonClick(view)
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun closeReservationCancle(
            context: Context,
            title: String,
            message: String,
            tripRoute: String,
            journeyDetails: String,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonMultipleView: DialogButtonMultipleView,
            view1: View,
            view2: View,
            view3: View,
            view4: View,
            resId: String
        ) {
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogBlockChartBinding =
                DialogBlockChartBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.btnLight.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            binding.tvHeader.text = title
            binding.tvHeader.setTextColor(context.getColor(R.color.black))
            binding.tvMessage.text = message
            binding.tvMessage.setTextColor(context.getColor(R.color.colorPrimary))

            binding.tvHeaderText.text = tripRoute
            binding.tvSubtitle.text = journeyDetails
            binding.btnDark.text = buttonLeftText
            binding.btnLight.text = buttonRightText
            binding.tvMessage.setTextColor(context.getColor(R.color.colorPrimary))
            binding.btnDark.setOnClickListener {


                dialogButtonMultipleView.onLeftButtonClick(view1, view2, view3, view4, resId)
                builder.dismiss()
            }
            binding.btnLight.setOnClickListener {
                dialogButtonMultipleView.onRightButtonClick(view1, view2, view3, view4, resId, "")
                builder.dismiss()
            }
            builder.setView(binding.root)
            builder.show()
        }


        fun closeReservation(
            context: Context,
            title: String,
            message: String,
            tripRoute: String,
            journeyDetails: String,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonMultipleView: DialogButtonMultipleView,
            view1: View,
            view2: View,
            view3: View,
            view4: View,
            resId: String,
            serviceBlockReasonsList: MutableList<ReasonList>

        ) {
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            //builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            builder.window?.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        context,
                        R.color.transparent_tint_color
                    )
                )
            )  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)

            val binding: DialogeCloseReservationBinding =
                DialogeCloseReservationBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvHeader.text = title
            binding.tvMessage.text = message
            binding.tvHeaderText.text = tripRoute
            binding.tvSubtitle.text = journeyDetails
            binding.btnDark.text = buttonLeftText
            binding.btnLight.text = buttonRightText

            binding.btnLight.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.colorShadow)
            binding.btnLight.isEnabled = false

            val privileges = (context as BaseActivity).getPrivilegeBase() as PrivilegeResponseModel
            val reasonsList : MutableList<String> = mutableListOf()
            serviceBlockReasonsList.forEach {
                reasonsList.add(it.value)
            }
            var blockingReasonId = ""
            if (privileges.tsPrivileges?.allowServiceBlockingReasonsList == true) {
                binding.tvRemarks.setAdapter(ArrayAdapter(context, R.layout.spinner_dropdown_item, R.id.tvItem, reasonsList))
                binding.layoutRemarksList.visible()
                binding.tvRemarks.setOnItemClickListener { _, _, position, _ ->
                    blockingReasonId = serviceBlockReasonsList[position].id
                    binding.btnLight.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.colorRed2)
                    binding.btnLight.isEnabled = true
                    binding.tvRemarks.clearFocus()
                }
            } else {
                binding.layoutRemarksList.gone()
                binding.etRemarks.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        if (text?.trim()?.length == 0) {
                            binding.btnLight.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.colorShadow)
                            binding.btnLight.isEnabled = false
                        } else {
                            binding.btnLight.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.colorRed2)
                            binding.btnLight.isEnabled = true
                        }
                    }

                    override fun afterTextChanged(p0: Editable?) {}
                })
            }

            binding.btnDark.setOnClickListener {
                dialogButtonMultipleView.onLeftButtonClick(view1, view2, view3, view4, resId)
                builder.cancel()
            }
            binding.btnLight.setOnClickListener {
                if (binding.etRemarks.text.isNullOrEmpty() && privileges.tsPrivileges?.allowServiceBlockingReasonsList != true) {
                    context.toast("please enter remarks")
                } else {
                    val remark = blockingReasonId + "," + binding.etRemarks.text.toString()
                    dialogButtonMultipleView.onRightButtonClick(
                        view1,
                        view2,
                        view3,
                        view4,
                        resId,
                        remark
                    )
                    builder.cancel()
                }

            }

            builder.setView(binding.root)
            builder.show()
        }


        fun oneTouchDialog(
            context: Context,
            message: String,
            dialogSingleButtonListener: DialogSingleButtonListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogOneTouchBinding =
                DialogOneTouchBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvContent.text = message
            binding.btnOkay.setOnClickListener {
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick()
                //finish()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun oneTouchDialogSuccess(
            context: Context,
            message: String,
            dialogSingleButtonListener: DialogSingleButtonListener
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background

            val binding: DialogOneTouchSuccessBinding =
                DialogOneTouchSuccessBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvContent.text = message

            binding.btnOkay.setOnClickListener {
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick()
//                finish()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun releaseTicketDialog(
            context: Context,
            pnrNo: String,
            doj: String,
            dialogSingleButtonListener: DialogSingleButtonListener,
            isSingleBlockUnblock: Boolean? = null,
            serviceBlockReasonsList: MutableList<ReasonList> = mutableListOf()
        ) {
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            val binding: DialogReleaseTicketsBinding =
                DialogReleaseTicketsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.tvPnrNo.text = pnrNo
            binding.tvDOJ.text = doj


            val privileges = (context as BaseActivity).getPrivilegeBase() as PrivilegeResponseModel
            var blockingReasonId = ""
            if (isSingleBlockUnblock != null && isSingleBlockUnblock) {
                binding.tvTitle.text = context.getString(R.string.single_block_reservation)
                binding.btnReleaseTicket.text = context.getString(R.string.submit)
                binding.tvPnrNo.gone()
                binding.tvDOJ.gone()

                binding.layoutRemarksList.gone()
                binding.btnReleaseTicket.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.colorShadow)
                binding.btnReleaseTicket.isEnabled = false

                val reasonsList : MutableList<String> = mutableListOf()
                serviceBlockReasonsList.forEach {
                    reasonsList.add(it.value)
                }

                if (privileges.tsPrivileges?.allowServiceBlockingReasonsList == true) {
                    binding.tvRemarks.setAdapter(ArrayAdapter(context, R.layout.spinner_dropdown_item, R.id.tvItem, reasonsList))
                    binding.layoutRemarksList.visible()
                    binding.tvRemarks.setOnItemClickListener { _, _, position, _ ->
                        blockingReasonId = serviceBlockReasonsList[position].id
                        binding.btnReleaseTicket.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.button_highlight_color)
                        binding.btnReleaseTicket.isEnabled = true
                        binding.tvRemarks.clearFocus()
                    }
                } else {
                    binding.layoutRemarksList.gone()
                    binding.etRemarks.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            if (text?.trim()?.length == 0) {
                                binding.btnReleaseTicket.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.colorShadow)
                                binding.btnReleaseTicket.isEnabled = false
                            } else {
                                binding.btnReleaseTicket.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.button_highlight_color)
                                binding.btnReleaseTicket.isEnabled = true
                            }
                        }

                        override fun afterTextChanged(p0: Editable?) {}
                    })
                }
            }

            binding.btnReleaseTicket.setOnClickListener {
                var remarks = binding.etRemarks.text.toString()
                if (isSingleBlockUnblock != null && isSingleBlockUnblock) {
                    remarks = "$blockingReasonId,$remarks|${context.getString(R.string.block)}"
                    dialogSingleButtonListener.onSingleButtonClick(str = remarks)
                } else
                    dialogSingleButtonListener.onSingleButtonClick(str = remarks)

//                if (remarks.isEmpty())
//                    context.toast(context.getString(R.string.enter_remarks))
//                else {
                builder.cancel()
//                    dialogSingleButtonListener.onSingleButtonClick(str = remarks)
//                }
            }
            binding.tvCancel.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun sendSmsDialog(
            title: String,
            message: String,
            btnLeftText: String,
            btnRightText: String,
            source: String,
            destination: String,
            coachNumber: String,
            context: Context,
            dialogButtonListener: DialogButtonListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            val binding: DialogSendSmsBinding =
                DialogSendSmsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            val srcDest = "$source - $destination"
            binding.tvContent.text = srcDest
            binding.tvSubContent.text = coachNumber
            binding.tvTitle.text = title
            if (message.isNotEmpty()) {
                binding.tvMessageTitle.visible()
                binding.tvMessage.visible()
                binding.view2.visible()
                binding.tvMessage.text = message
            }
            binding.btnLeft.text = btnLeftText
            binding.btnRight.text = btnRightText

            binding.btnLeft.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onLeftButtonClick()

            }
            binding.btnRight.setOnClickListener {
                builder.cancel()
                dialogButtonListener.onRightButtonClick()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun tripEnded(
            context: Context,
            singleButtonListener: DialogSingleButtonListener
        ) {

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogUnauthorizedBinding =
                DialogUnauthorizedBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvContent.text = context.getString(R.string.trip_complete_msg)
            binding.tvTitle.text = context.getString(R.string.trip_complete)
            binding.btnOk.tag = context.getString(R.string.trip_complete)
            binding.btnOk.setOnClickListener {
                builder.cancel()
                singleButtonListener.onSingleButtonClick("trip_complete")
            }
            builder.setView(binding.root)
            builder.show()
        }


        fun rapidBookingDialog(
            boardingPoint: String,
            droppingPoint: String,
            position: Int = 0,
            context: Context,
            varArgListener: VarArgListener,
        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogRapidBookingBinding =
                DialogRapidBookingBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
//            binding.etNoOfTickets.setMaxLength(2)
            binding.etBoardingAt.setText(boardingPoint)
            binding.etDropOffAt.setText(droppingPoint)
            binding.tvCancel.setOnClickListener {
                varArgListener.onButtonClick(
                    context.getString(R.string.cancel),
                    ""
                )
                builder.cancel()
            }
            binding.btnConfirm.setOnClickListener {
                when {
                    binding.etBoardingAt.text?.isEmpty() == true -> context.toast(
                        context.getString(
                            R.string.validate_boarding_point
                        )
                    )

                    binding.etDropOffAt.text?.isEmpty() == true -> context.toast(context.getString(R.string.validate_drop_off))
                    binding.etNoOfTickets.text?.isEmpty() == true -> context.toast(
                        context.getString(
                            R.string.validate_no_of_tickets
                        )
                    )

                    else -> {
//                        builder.cancel()
                        varArgListener.onButtonClick(
                            context.getString(R.string.confirm),
                            binding.etNoOfTickets.text.toString(),
                            position
                        )
                    }
                }
            }

            binding.etBoardingAt.setOnClickListener {
                if (binding.etBoardingAt.text.isNullOrEmpty()) {
                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                }
                if (binding.etDropOffAt.text.isNullOrEmpty()) {
                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                }

                varArgListener.onButtonClick(context.getString(R.string.boarding_at))
            }

            binding.etDropOffAt.setOnClickListener {
                if (binding.etBoardingAt.text.isNullOrEmpty()) {
                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                }
                if (binding.etDropOffAt.text.isNullOrEmpty()) {
                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                }
                varArgListener.onButtonClick(context.getString(R.string.drop_off_at))
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun editChartBpDp(
            boardingPoint: String,
            droppingPoint: String,
            context: Context,
            varArgListener: VarArgListener,
        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogEditBpDpBinding =
                DialogEditBpDpBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.etBoardingAt.setText(boardingPoint)
            binding.etDropOffAt.setText(droppingPoint)
            binding.tvCancel.setOnClickListener {
                varArgListener.onButtonClick(
                    context.getString(R.string.cancel),
                    ""
                )
                builder.cancel()
            }
            binding.btnConfirm.setOnClickListener {
                when {
                    binding.etBoardingAt.text?.isEmpty()!! -> context.toast(context.getString(R.string.validate_boarding_point))
                    binding.etDropOffAt.text?.isEmpty()!! -> context.toast(context.getString(R.string.validate_drop_off))
//                    binding.etNoOfTickets.text?.isEmpty()!! -> context.toast(context.getString(R.string.validate_no_of_tickets))
                    else -> {
                        builder.cancel()
                        varArgListener.onButtonClick(
                            context.getString(R.string.edit_chart),
                            0
                        )
                    }
                }
            }

            binding.etBoardingAt.setOnClickListener {
                if (binding.etBoardingAt.text.isNullOrEmpty()) {
                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                }
                if (binding.etDropOffAt.text.isNullOrEmpty()) {
                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                }
                varArgListener.onButtonClick(context.getString(R.string.boarding_at))
            }

            binding.etDropOffAt.setOnClickListener {
                if (binding.etBoardingAt.text.isNullOrEmpty()) {
                    PreferenceUtils.removeKey(PREF_BOARDING_STAGE_DETAILS)
                }
                if (binding.etDropOffAt.text.isNullOrEmpty()) {
                    PreferenceUtils.removeKey(PREF_DROPPING_STAGE_DETAILS)
                }
                varArgListener.onButtonClick(context.getString(R.string.drop_off_at))
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }


        fun creditDebitDialog(
            context: Context,
            varArgListener: VarArgListener,
        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogCreditDebitCardBinding =
                DialogCreditDebitCardBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.btnGoBack.setOnClickListener {
                val tag = binding.btnGoBack.text
                varArgListener.onButtonClick(
                    tag
                )
                builder.cancel()
            }
            binding.btnConfirm.setOnClickListener {
                when {
                    binding.etCreditDebitCard.text?.isEmpty()!! -> context.toast(context.getString(R.string.enter_last_4_digits_of_credit_debit_card))
                    binding.etCreditDebitCard.text?.toString()?.length!! < 4 -> context.toast(
                        context.getString(R.string.creditdebit_card_length_should_be_4)
                    )

                    else -> {
                        val tag = context.getString(R.string.credit_debit)
                        builder.cancel()
                        varArgListener.onButtonClick(tag, binding.etCreditDebitCard.text.toString())
                    }
                }
            }

            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun showSeatsDialog(
            ticketNo: String,
            srcDest: String,
            busType: String,
            bookedSeat: String,
            seatNo: String,
            context: Context,
        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            val binding: PopupLayoutBookingSeatDetailsBinding =
                PopupLayoutBookingSeatDetailsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTicketNo.text = ticketNo
            binding.tvSrcDest.text = srcDest
            binding.tvBusType.text = busType
            binding.tvBookedSeats.text = bookedSeat
            binding.tvSeatNo.text = seatNo
            binding.textOkay.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        @SuppressLint("SuspiciousIndentation")
        fun showServiceSummaryDialog(
            ticketNo: String,
            srcDest: String,
            busType: String,
            bookedSeat: String,
            seatNo: String,
            releasedBy: String,
            context: Context,
        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            val binding: PopupLayoutServiceSummaryDetailsBinding =
                PopupLayoutServiceSummaryDetailsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTicketNo.text = ticketNo
            binding.tvSrcDest.text = srcDest
            binding.tvBusType.gone()
            binding.tvBookedSeats.text = bookedSeat
            binding.tvSeatNo.text = seatNo
            if (!releasedBy.isEmpty()) {
                binding.tvReleasedBy.visible()
                binding.tvReleasedBy.text =
                    "${context.getString(R.string.released_by)}: ${releasedBy}"
            }

            binding.textOkay.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun luggageDialogBox(
            context: Context,
            pnr: String,
            name: String,
            seatNumber: String,
            boardedStatus: String,
            age: String,
            gender: String,
            dialogueLuggage: ((amount: String, quantity: String, item: String) -> Unit)

        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            val binding: LuggageDialogBoxBinding =
                LuggageDialogBoxBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.passName.text = "$name ($gender,$age)"
            binding.seatNumber.text = seatNumber
            when (boardedStatus) {
                "0" -> binding.seatStatus.text = context.getString(R.string.yet_to_board)
                "1" -> binding.seatStatus.text = context.getString(R.string.unboarded_status)
                "2" -> binding.seatStatus.text = context.getString(R.string.boarded_status)
                "3" -> binding.seatStatus.text = context.getString(R.string.no_show)
                "4" -> binding.seatStatus.text = context.getString(R.string.missing_status)
                "5" -> binding.seatStatus.text = context.getString(R.string.dropped_off)
            }


            binding.cancel.setOnClickListener {
                builder.dismiss()
            }

            binding.btnUpdate.setOnClickListener {

                val amount = binding.etAmount.text.toString()
                val quantity = binding.etQutanity.text.toString()
                val item = binding.etItemName.text.toString()

                if (amount == "" || amount == " " || quantity == "" || quantity == " " || item == "" || item == " ") {
                    context.toast("Please enter all details correctly")
                } else {
                    dialogueLuggage.invoke(amount, quantity, item)
                    builder.dismiss()
                }


            }


//            binding.textOkay.setOnClickListener {
//                builder.cancel()
//            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun luggageDialogBoxMultiSeat(
            context: Context,
            name: String,
            age: String,
            sex: String,
            pnrStatus: String,
            seatList: List<String>,
            dialogueLuggage: (selectedSeats: String, amount: String, quantity: String, item: String) -> Unit
        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            val binding: LuggageDialogBoxBinding =
                LuggageDialogBoxBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            if(seatList.size == 1) {
                binding.multiSeatDialogCL.gone()
                binding.passengerDetailsLL.visible()
                binding.passName.text = "$name ($sex,$age)"
                binding.seatNumber.text = seatList[0]
                when (pnrStatus) {
                    "0" -> binding.seatStatus.text = context.getString(R.string.yet_to_board)
                    "1" -> binding.seatStatus.text = context.getString(R.string.unboarded_status)
                    "2" -> binding.seatStatus.text = context.getString(R.string.boarded_status)
                    "3" -> binding.seatStatus.text = context.getString(R.string.no_show)
                    "4" -> binding.seatStatus.text = context.getString(R.string.missing_status)
                    "5" -> binding.seatStatus.text = context.getString(R.string.dropped_off)
                    "9" -> binding.seatStatus.text = context.getString(R.string.check_in)
                }
            } else {
                binding.multiSeatDialogCL.visible()
                binding.passengerDetailsLL.gone()
            }

            val adapter = MultiSeatLuggageAdapter(context, seatList)
            val layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = com.google.android.flexbox.JustifyContent.FLEX_START
            }
            binding.multiSeatSelectRV.layoutManager = layoutManager
            binding.multiSeatSelectRV.adapter = adapter

            if (seatList.size > 10) {
                val heightInDp = 150
                val scale = context.resources.displayMetrics.density
                val heightInPx = (heightInDp * scale).toInt()

                binding.multiSeatSelectRV.layoutParams.height = heightInPx
                binding.multiSeatSelectRV.isNestedScrollingEnabled = true
            } else {
                binding.multiSeatSelectRV.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.multiSeatSelectRV.isNestedScrollingEnabled = false
            }
            binding.multiSeatSelectRV.requestLayout()

            binding.cancel.setOnClickListener {
                builder.dismiss()
            }

            binding.btnUpdate.setOnClickListener {
                val selectedSeats = adapter.getSelectedSeats()
                val amount = binding.etAmount.text.toString().trim()
                val quantity = binding.etQutanity.text.toString().trim()
                val item = binding.etItemName.text.toString().trim()
                val selectedSeatsString: String = if(seatList.size == 1) {
                    seatList.joinToString(",")
                } else {
                    selectedSeats.joinToString(",")
                }

                when {
                    (selectedSeats.isEmpty() && seatList.size != 1) -> {
                        context.toast(context.getString(R.string.please_select_at_least_one_seat))
                    }
                    amount.isBlank() || quantity.isBlank() || item.isBlank() -> {
                        context.toast(context.getString(R.string.please_enter_all_details_correctly))
                    }
                    else -> {
                        dialogueLuggage.invoke(selectedSeatsString, amount, quantity, item)
                        builder.dismiss()
                    }
                }
            }

            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun dialogUpdateLuggageIntl(
            context: Context,
            pnrNumber: String,
            luggageDesc: String,
            singleButtonListener: DialogSingleButtonListener
        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogLuggageOptionBinding = DialogLuggageOptionBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.etLuggageDesc.setText(luggageDesc)

            binding.tvCancelDialogBtn.setOnClickListener {
                builder.dismiss()
            }

            binding.btnUpdateLuggage.setOnClickListener {
                val desc = binding.etLuggageDesc.text.toString()
                if (desc.isEmpty()) {
                    context.toast(context.getString(R.string.enterLuggageDesc))
                } else {
                    PreferenceUtils.putString("luggageOptionData", "$desc $pnrNumber")
                    singleButtonListener.onSingleButtonClick("luggageOption")
                    builder.dismiss()
                }
            }

            builder.setView(binding.root)
            builder.show()
            return builder
        }


        fun luggageIndiaDialogBox(
            context: Context,
            pnr: String,
            name: String,
            seatNumber: String,
            boardedStatus: String,
            age: String,
            gender: String,
            singleButtonListener: DialogSingleButtonListener

        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            val binding: LuggageDialogBoxBinding =
                LuggageDialogBoxBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.passName.text = "$name ($gender,$age)"
            binding.seatNumber.text = seatNumber
            when (boardedStatus) {
                "0" -> binding.seatStatus.text = context.getString(R.string.yet_to_board)
                "1" -> binding.seatStatus.text = context.getString(R.string.unboarded_status)
                "2" -> binding.seatStatus.text = context.getString(R.string.boarded_status)
                "3" -> binding.seatStatus.text = context.getString(R.string.no_show)
                "4" -> binding.seatStatus.text = context.getString(R.string.missing_status)
                "5" -> binding.seatStatus.text = context.getString(R.string.dropped_off)
            }


            binding.cancel.setOnClickListener {
                builder.dismiss()
            }

            binding.btnUpdate.setOnClickListener {

                val amount = binding.etAmount.text.toString()
                val quantity = binding.etQutanity.text.toString()
                val item = binding.etItemName.text.toString()

                if (amount == "" || amount == " " || quantity == "" || quantity == " " || item == "" || item == " ") {
                    context.toast("Please enter all details correctly")
                } else {
                    PreferenceUtils.putString("cargoDetails", "${amount},${quantity},${item}")
                    singleButtonListener.onSingleButtonClick("luggage")
                    builder.dismiss()
                }
            }

//
//            binding.textOkay.setOnClickListener {
//                builder.cancel()
//            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }


        fun phoneBlockingDialog(
            context: Context,
            varArgListener: VarArgListener,
            isPermanentPhoneBooking: Boolean,
            removePreSelectionOptionInTheBooking: Boolean? = null,
            hours: String? = null,
            minutes: String? = null,
            amOrpm: String? = null,
            selectedDate: String? = null,
            isPhoneBlockedDateChanged: Boolean? = null,
            isBima: Boolean = false
        ) {
            val builder = AlertDialog.Builder(context).create()
            dialogPhoneBlockingBinding =
                DialogPhoneBlockingBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            phoneBlockDate = selectedDate.toString()
            dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
            dialogPhoneBlockingBinding.btnUse.isEnabled = true

            if ((context as BaseActivity).getPrivilegeBase() != null) {
                privilegeResponseModel = (context as BaseActivity).getPrivilegeBase() as PrivilegeResponseModel
            }


//            Timber.d("testX--- $hours, $minutes, $amOrpm $isBima")

            loginModelPref = PreferenceUtils.getLogin()
            val role = getUserRole(
                loginModelPref,
                isAgentLogin = privilegeResponseModel.isAgentLogin,
                context
            )

            if (role.contains(context.getString(R.string.role_agent), true)
                && privilegeResponseModel.country.equals("India", true)
                && privilegeResponseModel.allowToDoPhoneBlocking == true
            ) {
                dialogPhoneBlockingBinding.chkPermanentBlocking.gone()
                dialogPhoneBlockingBinding.layoutHhMm.visible()

            } else {
                if (isPermanentPhoneBooking && isBima == false) {
                    dialogPhoneBlockingBinding.chkPermanentBlocking.visible()
                    dialogPhoneBlockingBinding.layoutDate.visible()
                } else {
                    dialogPhoneBlockingBinding.chkPermanentBlocking.gone()
                    dialogPhoneBlockingBinding.chkPermanentBlocking.isChecked = false
                    dialogPhoneBlockingBinding.layoutHhMm.visible()
                }
            }

            if (dialogPhoneBlockingBinding.layoutHhMm.isVisible) {
                if (hours.toString().toInt() != 0 || minutes.toString().toInt() != 0) {
                    phoneBlockHH = String.format("%02d", hours.toString().toInt())
                    phoneBlockMM = String.format("%02d", minutes.toString().toInt())
                    phoneBlockAMPM = amOrpm.toString()

                    dialogPhoneBlockingBinding.apply {
                        acHH.setText(phoneBlockHH)
                        acMM.setText(phoneBlockMM)
                        acAMPM.setText(amOrpm)
                    }
                }
            }

            if (removePreSelectionOptionInTheBooking == true) {
                dialogPhoneBlockingBinding.chkPermanentBlocking.isEnabled = false
                dialogPhoneBlockingBinding.chkPermanentBlocking.isChecked = false

                phoneBlockHH = String.format("%02d", hours.toString().toInt())
                phoneBlockMM = String.format("%02d", minutes.toString().toInt())
                phoneBlockAMPM = amOrpm.toString()

                dialogPhoneBlockingBinding.apply {
                    acHH.setText(phoneBlockHH)
                    acMM.setText(phoneBlockMM)
                    acAMPM.setText(amOrpm)
                    layoutHhMm.visible()
                }

            }
//            Timber.d("phone_blocked-  ${privilegeResponseModel?.country},${privilegeResponseModel?.allowToDoPhoneBlocking}, $role")

            dialogPhoneBlockingBinding.chkPermanentBlocking.setOnClickListener { view ->

                if (hours.toString().toInt() != 0 || minutes.toString().toInt() != 0) {
                    phoneBlockHH = String.format("%02d", hours.toString().toInt())
                    phoneBlockMM = String.format("%02d", minutes.toString().toInt())
                    phoneBlockAMPM = amOrpm.toString()

                    dialogPhoneBlockingBinding.acHH.setText(phoneBlockHH)
                    dialogPhoneBlockingBinding.acMM.setText(phoneBlockMM)
                    dialogPhoneBlockingBinding.acAMPM.setText(amOrpm)
                }

                dialogPhoneBlockingBinding.layoutHhMm.visible()

                val checkBox = view as CheckBox
                if (checkBox.isChecked) {
                    dialogPhoneBlockingBinding.layoutHhMm.gone()
                    if (phoneBlockDate.isNotEmpty()) {
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = true
                    }
                    phoneBlockHH = ""
                    phoneBlockMM = ""
                    phoneBlockAMPM = ""
                } else {

                    if (dialogPhoneBlockingBinding.acHH.text.isNullOrEmpty()
                        && dialogPhoneBlockingBinding.acMM.text.isNullOrEmpty()
                        && dialogPhoneBlockingBinding.acAMPM.text.isNullOrEmpty()
                    ) {
                        phoneBlockHH = ""
                        phoneBlockMM = ""
                        phoneBlockAMPM = ""
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_default_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = false
                    } else {
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = true

                        phoneBlockHH = String.format("%02d", hours.toString().toInt())
                        phoneBlockMM = String.format("%02d", minutes.toString().toInt())
                        phoneBlockAMPM = amOrpm.toString()
                    }
                }

                setPhoneBlockTimeAdapter(context, dialogPhoneBlockingBinding)
            }


            setPhoneBlockTimeAdapter(context, dialogPhoneBlockingBinding)

//            if (removePreSelectionOptionInTheBooking == true && isPhoneBlockedDateChanged==false){
//                dialogPhoneBlockingBinding.etSelectDate.isEnabled =false
//            }

            dialogPhoneBlockingBinding.etSelectDate.setText(selectedDate.toString())
            dialogPhoneBlockingBinding.etSelectDate.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    phoneBlockDate = s.toString()

                    if (!isPermanentPhoneBooking
                        && dialogPhoneBlockingBinding.acHH.text.isNotEmpty()
                        && dialogPhoneBlockingBinding.acMM.text.isNotEmpty()
                        && dialogPhoneBlockingBinding.acAMPM.text.isNotEmpty()
                    ) {
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = true
                    } else if (isPermanentPhoneBooking && phoneBlockDate.isNotEmpty()
                        && dialogPhoneBlockingBinding.chkPermanentBlocking.isEnabled
                    ) {
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {
                }
            })

            dialogPhoneBlockingBinding.acHH.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    phoneBlockHH = s.toString()

                    if (isPermanentPhoneBooking
                        && dialogPhoneBlockingBinding.acMM.text.isNotEmpty()
                        && dialogPhoneBlockingBinding.acAMPM.text.isNotEmpty()
                        && phoneBlockDate.isNotEmpty()
                    ) {
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = true
                    } else if (!isPermanentPhoneBooking
                        && dialogPhoneBlockingBinding.acMM.text.isNotEmpty()
                        && dialogPhoneBlockingBinding.etSelectDate.text.toString().isNotEmpty()
                        && phoneBlockDate.isNotEmpty()
                    ) {
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {
                }
            })

            dialogPhoneBlockingBinding.acMM.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    phoneBlockMM = s.toString()

                    if (isPermanentPhoneBooking
                        && dialogPhoneBlockingBinding.acHH.text.isNotEmpty()
                        && dialogPhoneBlockingBinding.acAMPM.text.isNotEmpty()
                        && phoneBlockDate.isNotEmpty()
                    ) {
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = true
                    } else if (!isPermanentPhoneBooking
                        && dialogPhoneBlockingBinding.acHH.text.isNotEmpty()
                        && dialogPhoneBlockingBinding.etSelectDate.text.toString().isNotEmpty()
                        && phoneBlockDate.isNotEmpty()
                    ) {
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {
                }
            })

            dialogPhoneBlockingBinding.acAMPM.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    phoneBlockAMPM = s.toString()

                    if (isPermanentPhoneBooking
                        && dialogPhoneBlockingBinding.acAMPM.text.isNotEmpty()
                        && dialogPhoneBlockingBinding.acHH.text.isNotEmpty()
                        && dialogPhoneBlockingBinding.acMM.text.isNotEmpty()
                        && phoneBlockDate.isNotEmpty()
                    ) {
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = true
                    } else if (!isPermanentPhoneBooking
                        && dialogPhoneBlockingBinding.acAMPM.text.isNotEmpty()
                        && dialogPhoneBlockingBinding.etSelectDate.text.toString().isNotEmpty()
                        && phoneBlockDate.isNotEmpty()
                    ) {
                        dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                        dialogPhoneBlockingBinding.btnUse.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable) {
                }
            })

            dialogPhoneBlockingBinding.tvCancel.setOnClickListener {
                builder.cancel()
                varArgListener.onButtonClick(
                    context.getString(R.string.phone_blocking_cancel_btn),
                )
            }

            dialogPhoneBlockingBinding.btnUse.setOnClickListener {

//                if (isPermanentPhoneBooking && removePreSelectionOptionInTheBooking==true){
//                    if (dialogPhoneBlockingBinding.acHH.text.toString().toInt()> hours!!.toInt()){
//                        context.toast("Please select correct time")
//                    } else {
//                        builder.cancel()
//                        var blockingDate = dialogPhoneBlockingBinding.etSelectDate.text.toString()
//                        if (isPermanentPhoneBooking)
//                            blockingDate = phoneBlockDate
//                        val blockingHours = phoneBlockHH
//                        val blockingMins = phoneBlockMM
//                        val blockingAMPM = phoneBlockAMPM
//                        Timber.d("blockingDate $blockingDate blockingHours $blockingHours blockingMins $blockingMins")
//
//                        varArgListener.onButtonClick(
//                            context.getString(R.string.phone_blocking_use_btn),
//                            blockingDate,
//                            blockingHours,
//                            blockingMins,
//                            blockingAMPM,
//                            dialogPhoneBlockingBinding.chkPermanentBlocking.isChecked
//                        )
//                    }
//                } else {
//                    builder.cancel()
//                    var blockingDate = dialogPhoneBlockingBinding.etSelectDate.text.toString()
//                    if (isPermanentPhoneBooking)
//                        blockingDate = phoneBlockDate
//                    val blockingHours = phoneBlockHH
//                    val blockingMins = phoneBlockMM
//                    val blockingAMPM = phoneBlockAMPM
//                    Timber.d("blockingDate $blockingDate blockingHours $blockingHours blockingMins $blockingMins")
//
//                    varArgListener.onButtonClick(
//                        context.getString(R.string.phone_blocking_use_btn),
//                        blockingDate,
//                        blockingHours,
//                        blockingMins,
//                        blockingAMPM,
//                        dialogPhoneBlockingBinding.chkPermanentBlocking.isChecked
//                    )
//                }

                builder.cancel()
                var blockingDate = dialogPhoneBlockingBinding.etSelectDate.text.toString()
                if (isPermanentPhoneBooking)
                    blockingDate = phoneBlockDate
                val blockingHours = phoneBlockHH
                val blockingMins = phoneBlockMM
                val blockingAMPM = phoneBlockAMPM
                Timber.d("blockingDate $blockingDate blockingHours $blockingHours blockingMins $blockingMins")

                varArgListener.onButtonClick(
                    context.getString(R.string.phone_blocking_use_btn),
                    blockingDate,
                    blockingHours,
                    blockingMins,
                    blockingAMPM,
                    dialogPhoneBlockingBinding.chkPermanentBlocking.isChecked
                )
            }

            if (isPermanentPhoneBooking
                && removePreSelectionOptionInTheBooking == true
                && dialogPhoneBlockingBinding.acHH.text.isNotEmpty()
                && dialogPhoneBlockingBinding.acMM.text.isNotEmpty()
                && dialogPhoneBlockingBinding.acAMPM.text.isNotEmpty()
                && dialogPhoneBlockingBinding.etSelectDate.text?.isNotEmpty() == true
            ) {
                dialogPhoneBlockingBinding.btnUse.setBackgroundResource(R.drawable.button_selected_bg)
                dialogPhoneBlockingBinding.btnUse.isEnabled = true
            }

            dialogPhoneBlockingBinding.etSelectDate.setOnClickListener {

//                if (removePreSelectionOptionInTheBooking == true) {
//                    setDateLocale(PreferenceUtils.getlang(), context)
//                    SlyCalendarDialog()
//                        .setStartDate(stringToDate(selectedDate.toString(), DATE_FORMAT_D_M_Y))
//                        .setMinDate(stringToDate(selectedDate.toString(), DATE_FORMAT_D_M_Y))
//                        .setMaxDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
//                        .setSingle(true)
//                        .setFirstMonday(false)
//                        .setCallback(this)
//                        .show((context as AppCompatActivity).supportFragmentManager, tag)
//                } else {
//                    setDateLocale(PreferenceUtils.getlang(), context)
//                    SlyCalendarDialog()
//                        .setStartDate(stringToDate(selectedDate.toString(), DATE_FORMAT_D_M_Y))
//                        .setMinDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
//                        .setSingle(true)
//                        .setFirstMonday(false)
//                        .setCallback(this)
//                        .show((context as AppCompatActivity).supportFragmentManager, tag)
//                }

                setDateLocale(PreferenceUtils.getlang(), context)

                val minDate = stringToDate("01-01-1900", DATE_FORMAT_D_M_Y)


                SlyCalendarDialog()
                    .setStartDate(stringToDate(selectedDate.toString(), DATE_FORMAT_D_M_Y))
                    .setMinDate(minDate)
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show((context as AppCompatActivity).supportFragmentManager, tag)
            }

            builder.setView(dialogPhoneBlockingBinding.root)
            builder.show()
        }

        private fun setPhoneBlockTimeAdapter(
            context: Context,
            dialogPhoneBlockingBindingX: DialogPhoneBlockingBinding,
        ) {

            dialogPhoneBlockingBindingX.acHH.setAdapter(
                ArrayAdapter<String>(
                    context,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    context.resources.getStringArray(R.array.hourArray)
                )
            )

            dialogPhoneBlockingBindingX.acMM.setAdapter(
                ArrayAdapter<String>(
                    context,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    context.resources.getStringArray(R.array.minuteArray)
                )
            )

            dialogPhoneBlockingBindingX.acAMPM.setAdapter(
                ArrayAdapter<String>(
                    context,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    context.resources.getStringArray(R.array.amPMArray)
                )
            )
        }

        fun scanQrDialog(
            context: Context,
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: DialogScanQrBinding =
                DialogScanQrBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(true)
            builder.setView(binding.root)
            builder.show()
        }

        fun dialogShiftOption(
            context: Context,
            apiServiceNAme: String,
            apiServiceNumber: String,
            singleButtonListener: DialogSingleButtonListener,
            selectionType: Int
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: DialogShiftOptionsBinding =
                DialogShiftOptionsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            when (selectionType) {
                0 -> {
                    binding.manually.isChecked = true
                    PreferenceUtils.setPreference("shiftTypeOption", "manually")
                }

                1 -> {
                    binding.manually.isChecked = true
                    PreferenceUtils.setPreference("shiftTypeOption", "manually")
                }

                2 -> {
                    binding.autoBySeat.isChecked = true
                    PreferenceUtils.setPreference("shiftTypeOption", "seats")
                }

                3 -> {
                    binding.autoByRow.isChecked = true
                    PreferenceUtils.setPreference("shiftTypeOption", "row")
                }
            }

            binding.apiName.text = apiServiceNAme
            binding.apiNumber.text = apiServiceNumber
            binding.manually.setOnClickListener {
                PreferenceUtils.setPreference("shiftTypeOption", "manually")
            }
            binding.autoBySeat.setOnClickListener {
                PreferenceUtils.setPreference("shiftTypeOption", "seats")
            }
            binding.autoByRow.setOnClickListener {
                PreferenceUtils.setPreference("shiftTypeOption", "row")
            }

            binding.btnDark.setOnClickListener {
                PreferenceUtils.setPreference("shiftTypeOption", "")
                builder.dismiss()

            }
            binding.btnLight.setOnClickListener {
                builder.cancel()
                singleButtonListener.onSingleButtonClick()
            }





            builder.setView(binding.root)
            builder.show()
        }







        fun dialogStageDetails(
            context: Context,
            stageName: String,
            landmark: String,
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: DialogLandmarkBinding =
                DialogLandmarkBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(true)


          binding.boardingNameTV.text=stageName
            binding.landmarkValueTV.text=landmark

            binding.btnOkay.setOnClickListener {
                builder.cancel()
            }




            builder.setView(binding.root)
            builder.show()
        }












        fun statusDialog(
            context: Context,
            prn: String,
            sNumber: String,
            btnswitch: SwitchCompat,
            statusText: TextView,
            pName: String,
            btnLeftText: String,
            btnRightText: String,
            btnConfirm: ((pnr: String, passengerName: String, btnswitch: SwitchCompat, statusText: TextView, statusSelected: String, seatNumber: String) -> Unit)
        ) {
            var statusSelected = PreferenceUtils.getPreference("pickUpChartStatus", "")
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogStatusFilterBinding =
                DialogStatusFilterBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            val statusSelectListener = object : StatusSelectListener {
                override fun onStatusSelected(status: String) {
                    statusSelected = status
                }
            }

            Timber.d("StatusSelected : ${statusSelected}")
            when (statusSelected) {
                "0" -> binding.radiogroup.check(R.id.yet_to_board)
                "1" -> binding.radiogroup.check(R.id.unboarded)
                "2" -> binding.radiogroup.check(R.id.boarded)
                "3" -> binding.radiogroup.check(R.id.no_show)
                "4" -> binding.radiogroup.check(R.id.missing)
                "5" -> binding.radiogroup.check(R.id.droped_off)
                "9" -> binding.radiogroup.check(R.id.check_in_status)

            }
            val privileges = (context as BaseActivity).getPrivilegeBase()

            if (privileges?.country.equals("india", true)) {
                val appModes = privileges?.availableAppModes

                if (appModes?.boarded_status == true) {
                    binding.boarded.visible()
                } else {
                    binding.boarded.gone()
                }

                if (appModes?.unboarded_status == true) {
                    binding.unboarded.visible()
                } else {
                    binding.unboarded.gone()
                }

                if (appModes?.yet_to_board_status == true) {
                    binding.yetToBoard.visible()
                } else {
                    binding.yetToBoard.gone()
                }

                if (appModes?.no_show_status == true) {
                    binding.noShow.visible()
                } else {
                    binding.noShow.gone()
                }

                if (appModes?.missing_status == true) {
                    binding.missing.visible()
                } else {
                    binding.missing.gone()
                }

                if (appModes?.dropped_off_status == true) {
                    binding.dropedOff.visible()
                } else {
                    binding.dropedOff.gone()
                }

                if (appModes?.check_in_status != null) {
                    if (appModes.check_in_status == true) {
                        binding.checkInStatus.visible()
                    } else {
                        binding.checkInStatus.gone()
                    }
                }
            }


            binding.radiogroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.yet_to_board -> statusSelectListener.onStatusSelected("0")
                    R.id.unboarded -> statusSelectListener.onStatusSelected("1")
                    R.id.boarded -> statusSelectListener.onStatusSelected("2")
                    R.id.no_show -> statusSelectListener.onStatusSelected("3")
                    R.id.missing -> statusSelectListener.onStatusSelected("4")
                    R.id.droped_off -> statusSelectListener.onStatusSelected("5")
                    R.id.check_in_status -> statusSelectListener.onStatusSelected("9")
                }
            }



            binding.btnDark.text = btnLeftText
            binding.btnLight.text = btnRightText
            binding.btnDark.setOnClickListener {
                builder.cancel()
            }
            binding.btnLight.setOnClickListener {
                builder.cancel()
                btnConfirm.invoke(prn, pName, btnswitch, statusText, statusSelected!!, sNumber)
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun statusMultiSeatDialog(
            context: Context,
            pnr: String,
            seatNumbers: List<String>,
            btnLeftText: String,
            btnRightText: String,
            btnConfirm: ((pnr: String, seatNumbers: String, statusSelected: String) -> Unit)
        ) {
            var statusSelected = PreferenceUtils.getPreference("pickUpChartStatus", "")
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: DialogStatusFilterBinding =
                DialogStatusFilterBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            val statusSelectListener = object : StatusSelectListener {
                override fun onStatusSelected(status: String) {
                    statusSelected = status
                }
            }

            if(seatNumbers.size == 1) {
                binding.multiSeatDialogCL.gone()
            } else {
                binding.multiSeatDialogCL.visible()
            }

            val adapter = MultiSeatLuggageAdapter(context, seatNumbers)
            val layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = com.google.android.flexbox.JustifyContent.FLEX_START
            }
            binding.multiSeatSelectRV.layoutManager = layoutManager
            binding.multiSeatSelectRV.adapter = adapter

            if (seatNumbers.size > 10) {
                val heightInDp = 200
                val scale = context.resources.displayMetrics.density
                val heightInPx = (heightInDp * scale).toInt()

                binding.multiSeatSelectRV.layoutParams.height = heightInPx
                binding.multiSeatSelectRV.isNestedScrollingEnabled = true
            } else {
                binding.multiSeatSelectRV.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.multiSeatSelectRV.isNestedScrollingEnabled = false
            }
            binding.multiSeatSelectRV.requestLayout()

            Timber.d("StatusSelected : ${statusSelected}")
            when (statusSelected) {
                "0" -> binding.radiogroup.check(R.id.yet_to_board)
                "1" -> binding.radiogroup.check(R.id.unboarded)
                "2" -> binding.radiogroup.check(R.id.boarded)
                "3" -> binding.radiogroup.check(R.id.no_show)
                "4" -> binding.radiogroup.check(R.id.missing)
                "5" -> binding.radiogroup.check(R.id.droped_off)
                "9" -> binding.radiogroup.check(R.id.check_in_status)
            }

            val privileges = (context as BaseActivity).getPrivilegeBase()

            if (privileges?.country.equals("india", true)) {
                val appModes = privileges?.availableAppModes

                if (appModes?.boarded_status == true) {
                    binding.boarded.visible()
                } else {
                    binding.boarded.gone()
                }

                if (appModes?.unboarded_status == true) {
                    binding.unboarded.visible()
                } else {
                    binding.unboarded.gone()
                }

                if (appModes?.yet_to_board_status == true) {
                    binding.yetToBoard.visible()
                } else {
                    binding.yetToBoard.gone()
                }

                if (appModes?.no_show_status == true) {
                    binding.noShow.visible()
                } else {
                    binding.noShow.gone()
                }

                if (appModes?.missing_status == true) {
                    binding.missing.visible()
                } else {
                    binding.missing.gone()
                }

                if (appModes?.dropped_off_status == true) {
                    binding.dropedOff.visible()
                } else {
                    binding.dropedOff.gone()
                }

                if (appModes?.check_in_status != null) {
                    if (appModes.check_in_status == true) {
                        binding.checkInStatus.visible()
                    } else {
                        binding.checkInStatus.gone()
                    }
                }
            }

            binding.radiogroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.yet_to_board -> statusSelectListener.onStatusSelected("0")
                    R.id.unboarded -> statusSelectListener.onStatusSelected("1")
                    R.id.boarded -> statusSelectListener.onStatusSelected("2")
                    R.id.no_show -> statusSelectListener.onStatusSelected("3")
                    R.id.missing -> statusSelectListener.onStatusSelected("4")
                    R.id.droped_off -> statusSelectListener.onStatusSelected("5")
                    R.id.check_in_status -> statusSelectListener.onStatusSelected("9")
                }
            }

            binding.btnDark.text = btnLeftText
            binding.btnLight.text = btnRightText
            binding.btnDark.setOnClickListener {
                builder.cancel()
            }
            binding.btnLight.setOnClickListener {
                val selectedSeats = adapter.getSelectedSeats()
                val selectedSeatsString: String = if(seatNumbers.size == 1) {
                    seatNumbers.joinToString(",")
                } else {
                    selectedSeats.joinToString(",")
                }

                when {
                    (selectedSeats.isEmpty() && seatNumbers.size != 1) -> {
                        context.toast(context.getString(R.string.please_select_at_least_one_seat))
                    }
                    else -> {
                        builder.cancel()
                        btnConfirm.invoke(pnr, selectedSeatsString, statusSelected!!)
                    }
                }
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun statusIndiaDialog(
            context: Context,
            buttonLeftText: String,
            buttonRightText: String,
            dialogSingleButtonListener: DialogSingleButtonListener,
            dialogButtonMultipleView: DialogButtonMultipleView,
            view1: View,
            view2: View,
            view3: View,
            view4: View,
            resId: String,
            remarks: String
        ) {
            val statusSelected = PreferenceUtils.getPreference("pickUpChartStatus", "")
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogStatusFilterBinding =
                DialogStatusFilterBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            Timber.d("StatusSelected : ${statusSelected}")
            when (statusSelected) {
                "0" -> binding.radiogroup.check(R.id.yet_to_board)
                "1" -> binding.radiogroup.check(R.id.unboarded)
                "2" -> binding.radiogroup.check(R.id.boarded)
                "3" -> binding.radiogroup.check(R.id.no_show)
                "4" -> binding.radiogroup.check(R.id.missing)
                "5" -> binding.radiogroup.check(R.id.droped_off)
                "9" -> binding.radiogroup.check(R.id.check_in_status)

            }
            val privileges = (context as BaseActivity).getPrivilegeBase()

            if (privileges?.country.equals("india", true)) {
                val appModes = privileges?.availableAppModes

                if (appModes?.boarded_status == true) {
                    binding.boarded.visible()
                } else {
                    binding.boarded.gone()
                }

                if (appModes?.unboarded_status == true) {
                    binding.unboarded.visible()
                } else {
                    binding.unboarded.gone()
                }

                if (appModes?.yet_to_board_status == true) {
                    binding.yetToBoard.visible()
                } else {
                    binding.yetToBoard.gone()
                }

                if (appModes?.no_show_status == true) {
                    binding.noShow.visible()
                } else {
                    binding.noShow.gone()
                }

                if (appModes?.missing_status == true) {
                    binding.missing.visible()
                } else {
                    binding.missing.gone()
                }

                if (appModes?.dropped_off_status == true) {
                    binding.dropedOff.visible()
                } else {
                    binding.dropedOff.gone()
                }

                if (appModes?.check_in_status != null) {
                    if (appModes.check_in_status == true) {
                        binding.checkInStatus.visible()
                    } else {
                        binding.checkInStatus.gone()
                    }
                }
            }



            binding.yetToBoard.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick("0")
            }
            binding.unboarded.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick("1")
            }

            binding.boarded.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick("2")
            }

            binding.noShow.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick("3")
            }

            binding.missing.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick("4")
            }
            binding.dropedOff.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick("5")
            }
            binding.checkInStatus.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick("9")
            }



            binding.btnDark.text = buttonLeftText
            binding.btnLight.text = buttonRightText
            binding.btnDark.setOnClickListener {
                builder.cancel()
                dialogButtonMultipleView.onLeftButtonClick(view1, view2, view3, view4, resId)
            }
            binding.btnLight.setOnClickListener {
                builder.cancel()
                dialogButtonMultipleView.onRightButtonClick(
                    view1,
                    view2,
                    view3,
                    view4,
                    resId,
                    remarks
                )
            }
            builder.setView(binding.root)
            builder.show()
        }


        fun vanStatus(
            context: Context,
            buttonLeftText: String,
            buttonRightText: String,
            oldStatus: String,
            changeStatus: ((status: String) -> Unit)
        ) {
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogStatusFilterBinding =
                DialogStatusFilterBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.unboarded.gone()
            binding.missing.gone()
            binding.dropedOff.gone()
            when (oldStatus) {
                context.getString(R.string.yet_to_board) -> {
                    binding.radiogroup.check(R.id.yet_to_board)
                }

                context.getString(R.string.boarded_status) -> {
                    binding.radiogroup.check(R.id.boarded)
                }

                context.getString(R.string.no_show) -> {
                    binding.radiogroup.check(R.id.no_show)
                }
            }
            var status = ""
            binding.yetToBoard.setOnClickListener {
                status = "6"
            }
            binding.boarded.setOnClickListener {
                status = "7"
            }
            binding.noShow.setOnClickListener {
                status = "8"
            }

            binding.btnDark.text = buttonLeftText
            binding.btnLight.text = buttonRightText
            binding.btnDark.setOnClickListener {
                builder.cancel()
            }
            binding.btnLight.setOnClickListener {
                changeStatus.invoke(status)
                builder.cancel()

            }
            builder.setView(binding.root)
            builder.show()
        }


        fun filterDialog(
            context: Context,
            searchList: MutableList<Filter>,
            title: String,
            btnText: String,
            initDialog: Boolean,
            isCancelBtnVisible: Boolean,
            dialogSingleButtonListener: DialogSingleButtonListener,
            dialogSingleButtonListenerCancel: DialogSingleButtonListener,
        ) {
            if (initDialog) {
                lastCheckedPos = 0
                filterItemName = "None"
            }

            lateinit var layoutManager: RecyclerView.LayoutManager
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: DialogFilterBinding =
                DialogFilterBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTitle.text = title

            binding.btnApplyFilter.text = btnText
            if (isCancelBtnVisible)
                binding.tvCancel.visible()
            else
                binding.tvCancel.gone()

            val filterList = Filter(0, context.getString(R.string.none))
            searchList.add(0, filterList)

            layoutManager = LinearLayoutManager(
                context.applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.rvFilters.layoutManager = layoutManager
            val filterAdapter = MyBookingFilterAdapter(
                context.applicationContext,
                this,
                searchList,
                lastCheckedPos
            )
            binding.rvFilters.adapter = filterAdapter

            binding.btnApplyFilter.setOnClickListener {
                lastPositionOnCancelButton = lastCheckedPos
                builder.cancel()

                dialogSingleButtonListener.onSingleButtonClick(filterItemName)
            }
            binding.tvCancel.setOnClickListener {
                dialogSingleButtonListenerCancel.onSingleButtonClick(context.getString(R.string.cancel))
                lastCheckedPos = lastPositionOnCancelButton
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }


        fun dialogScanStatus(
            context: Context,
            searchList: ArrayList<com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail?>,
            pnr: String,
            btnText: String,
            dialogSingleButtonListener: DialogSingleButtonListener,
            onItemCheckedMultipledataListner: OnItemCheckedMultipledataListner
        ) {

            lateinit var layoutManager: RecyclerView.LayoutManager
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: DialogChageStatusBinding =
                DialogChageStatusBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvPnr.text = pnr
            var privilegeResponse: PrivilegeResponseModel?= null
            if ((context as BaseActivity).getPrivilegeBase() != null) {
                privilegeResponse = (context as BaseActivity).getPrivilegeBase()
                privilegeResponse?.let {
                    if (privilegeResponse.validateRemarksForBoardingStageInMobilityApp)
                        binding.quickRemarks.visible()
                    else
                        binding.quickRemarks.gone()

                }
            } else {
                context.toast(context.getString(R.string.opps))
            }

            builder.setOnShowListener { d: DialogInterface? ->
                builder.window!!
                    .clearFlags(
                        WindowManager
                            .LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    )
            }
            binding.btnVerify.text = btnText
            layoutManager = LinearLayoutManager(
                context.applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.rvPassengers.layoutManager = layoutManager
            val scanAdapter = ScanDialogStatusAdapter(
                context.applicationContext,
                privilegeResponse,
                searchList,
                onItemCheckedMultipledataListner,
                this
            )
            binding.rvPassengers.adapter = scanAdapter
            binding.btnVerify.setOnClickListener {
                var isRemarks = false
                if (privilegeResponse != null) {

                    privilegeResponse.let {
                        isRemarks = privilegeResponse.validateRemarksForBoardingStageInMobilityApp
                    }
                } else {
                    context.toast(context.getString(R.string.opps))
                }
                if (isRemarks) {
                    if (binding.quickRemarksEt.text.isNullOrEmpty()) {
                        context.toast("${context.getString(R.string.enter_remarks)}")
                    } else {
                        if (seatlist.isNullOrEmpty()) {
                            context.toast("nothing selected")
                        } else {
                            seatlist.clear()
                            dialogSingleButtonListener.onSingleButtonClick("scan&${binding.quickRemarksEt.text}")
                            builder.dismiss()
                        }
                    }
                } else {
                    if (seatlist.isNullOrEmpty()) {
                        context.toast("nothing selected")
                    } else {
                        seatlist.clear()
                        dialogSingleButtonListener.onSingleButtonClick("scan")
                        builder.dismiss()
                    }

                }

            }
            binding.tvCancel.setOnClickListener {
                seatlist.clear()
                builder.dismiss()
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }


        fun scanQrCodeDialog(
            context: Context,
            searchList: MutableList<Filter>,
            title: String,
            btnText: String,
            isCancelBtnVisible: Boolean,
            dialogSingleButtonListener: DialogSingleButtonListener,
            dialogSingleButtonListenerCancel: DialogSingleButtonListener,
        ) {
            if (searchList.size > 0) {
                lastCheckedPos = 0
                filterItemName = searchList[0].label.toString()
            }
            lateinit var layoutManager: RecyclerView.LayoutManager

            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: DialogFilterBinding =
                DialogFilterBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTitle.text = title

            binding.btnApplyFilter.text = btnText
            binding.tvCancel.visible()

            layoutManager = LinearLayoutManager(
                context.applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.rvFilters.layoutManager = layoutManager
            val filterAdapter = MyBookingFilterAdapter(
                context.applicationContext,
                this,
                searchList,
                lastCheckedPos
            )
            binding.rvFilters.adapter = filterAdapter

            binding.btnApplyFilter.setOnClickListener {
                builder.cancel()

                dialogSingleButtonListener.onSingleButtonClick(filterItemName)
            }
            binding.tvCancel.setOnClickListener {
                dialogSingleButtonListenerCancel.onSingleButtonClick(context.getString(R.string.cancel))

                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun otherPaymentsDialog(
            context: Context,
            otherPayments: MutableList<PayGayType>,
            dialogSingleButtonListener: DialogSingleButtonListener,
        ) {

            lateinit var layoutManager: RecyclerView.LayoutManager

            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: DialogPaymentOthersBinding =
                DialogPaymentOthersBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            layoutManager =
                LinearLayoutManager(context.applicationContext, LinearLayoutManager.VERTICAL, false)
            binding.rvOtherPayments.layoutManager = layoutManager
            val paymentOptionsAdapter =
                PaymentOptionsAdapter(context.applicationContext, this, otherPayments)
            binding.rvOtherPayments.adapter = paymentOptionsAdapter

            binding.btnConfirm.setOnClickListener {
                val tag = context.getString(R.string.other_payments_confirm)
                dialogSingleButtonListener.onSingleButtonClick("$tag-$otherPaymentPosition")
                builder.cancel()
            }
            binding.btnGoBack.setOnClickListener {
                val tag = binding.btnGoBack.text
                dialogSingleButtonListener.onSingleButtonClick(tag.toString())
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun walletUpiDialog(
            context: Context,
            walletPaymentOption: MutableList<WalletPaymentOption>,
            dialogSingleButtonListener: DialogSingleButtonListener,
            mobile: String
        ): AlertDialog {
            
            if ((context as BaseActivity).getPrivilegeBase() != null) {
                privilegeResponseModel = (context as BaseActivity).getPrivilegeBase() as PrivilegeResponseModel
            }
            
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogWalletUpiBinding = DialogWalletUpiBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
                context.applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.rvWalletUpi.layoutManager = layoutManager

            
            if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                val walletOptionAgentRechargeAdapter = WalletOptionAgentRechargeAdapter(
                    context = context.applicationContext,
                    onItemClickListener = this,
                    walletOptionList = walletPaymentOption,
                    lastSelectedPositionPayment = lastCheckedPos
                )
                binding.rvWalletUpi.adapter = walletOptionAgentRechargeAdapter
            } else {
                val walletUpiAdapter = WalletOptionAdapter(
                    context = context.applicationContext,
                    onItemClickListener = this,
                    walletOptionList = walletPaymentOption,
                    lastSelectedPositionPayment = lastCheckedPos
                )
                
                binding.rvWalletUpi.adapter = walletUpiAdapter
            }
            
            dialogSingleListener = dialogSingleButtonListener

            if (walletPaymentOption.size > 0) {
                lastCheckedPos = 0
                filterItemName = walletPaymentOption[0].name.toString()
            }
            
            binding.apply {
                etMobileNumber.setMaxLength(privilegeResponseModel.phoneNumValidationCount?: PHONE_VALIDATION_COUNT)
                tvSubTitle.visible()
                rvWalletUpi.visible()
                etMobileNumber.setText(mobile)
            }
            
            if (privilegeResponseModel.isAgentLogin && privilegeResponseModel.allowUpiForDirectPgBookingForAgents) {
                binding.btnConfirm.text = context.getString(R.string.select)
                binding.tvSubTitle.gone()
            } else{
                binding.btnConfirm.text = context.getString(R.string.confirm)
                binding.tvSubTitle.visible()
            }
            //binding.btnConfirm.setBackgroundColor(context.resources.getColor(R.color.button_default_color))
            binding.btnConfirm.setSafeOnClickListener {
                
                if (binding.btnConfirm.text == context.getString(R.string.confirm_validate)) {
                    // it.isClickable = true
                    it.setBackgroundResource(R.drawable.button_selected_bg)
                } else {
                    //it.isClickable = false
                    it.setBackgroundResource(R.drawable.button_default_bg)
                }
                
                
                when (PreferenceUtils.getString("upiSelected")) {
                    "UPI" -> {
                        dialogSingleButtonListener.onSingleButtonClick("UPI_Selected")
                    }

                    "wallet" -> {
                        val passengerMobile = binding.etMobileNumber.text.toString()
                        val passengerOtp = binding.etOtp.text.toString()
                        val tag = context.getString(R.string.wallet_upi_confirm)
                        dialogSingleButtonListener.onSingleButtonClick("$tag-$passengerMobile-$passengerOtp")
                    }
                    
                    "QR" -> {
                        dialogSingleButtonListener.onSingleButtonClick("QR")
                    }
                    "SMS" -> {
                        val passengerMobile = binding.etMobileNumber.text.toString()
                        dialogSingleButtonListener.onSingleButtonClick("SMS-$passengerMobile")
                        
                    }
                    "VPA" -> {
                        val upiId = binding.etUpiId.text.toString()
                        binding.layoutUpiId.visible()
                        dialogSingleButtonListener.onSingleButtonClick("VPA-$upiId")
                    }

                    PaymentTypes.PHONEPE_V2 -> {
                        dialogSingleListener.onSingleButtonClick(PaymentTypes.PHONEPE_V2)
                    }

                    else -> {
                        dialogSingleButtonListener.onSingleButtonClick("UPI_Selected")
                    }
                }
            }

            binding.btnGoBack.setOnClickListener {
                val tag = context.getString(R.string.wallet_go_back)
                dialogSingleButtonListener.onSingleButtonClick(tag)
                builder.cancel()
                PreferenceUtils.removeKey("upiSelected")
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun easebuzzDialog(
            context: Context,
            easebuzzPaymentOption: MutableList<WalletPaymentOption>,
            dialogSingleButtonListener: DialogSingleButtonListener,
            mobile: String
        ): AlertDialog {

            if (PreferenceUtils.getObject<PrivilegeResponseModel>(PREF_PRIVILEGE_DETAILS) != null) {
                privilegeResponseModel = PreferenceUtils.getObject<PrivilegeResponseModel>(PREF_PRIVILEGE_DETAILS)!!
            }

            val builder = AlertDialog.Builder(context).create()
            val binding = DialogEasebuzzBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
                context.applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.rvEasebuzz.layoutManager = layoutManager

            val easebuzzOptionAdapter = EasebuzzOptionAdapter(
                    context = context.applicationContext,
                    onItemClickListener = this,
                    easebuzzOptionList = easebuzzPaymentOption,
                    lastSelectedPositionPayment = lastCheckedPos
            )

            binding.rvEasebuzz.adapter = easebuzzOptionAdapter

            dialogSingleListener = dialogSingleButtonListener

            if (easebuzzPaymentOption.size > 0) {
                lastCheckedPos = 0
                filterItemName = easebuzzPaymentOption[0].name.toString()
            }

            binding.apply {
                etMobileNumber.setMaxLength(privilegeResponseModel.phoneNumValidationCount?: PHONE_VALIDATION_COUNT)
                rvEasebuzz.visible()
                etMobileNumber.setText(mobile)
            }

            binding.btnConfirm.text = context.getString(R.string.select)

            binding.btnConfirm.setSafeOnClickListener {

                when (PreferenceUtils.getString("easebuzzSelected")) {
                    "QR" -> {
                        dialogSingleButtonListener.onSingleButtonClick("QR")
                    }
                    "SMS" -> {
                        val passengerMobile = binding.etMobileNumber.text.toString()
                        dialogSingleButtonListener.onSingleButtonClick("SMS-$passengerMobile")

                    }
                    "VPA" -> {
                        val upiId = binding.etUpiId.text.toString()
                        binding.layoutUpiId.visible()
                        dialogSingleButtonListener.onSingleButtonClick("VPA-$upiId")
                    }

                    else -> {
                        dialogSingleButtonListener.onSingleButtonClick("UPI_Selected")
                    }
                }
            }

            binding.btnGoBack.setOnClickListener {
                val tag = context.getString(R.string.easebuzz_go_back)
                dialogSingleButtonListener.onSingleButtonClick(tag)
                builder.cancel()
                PreferenceUtils.removeKey("easebuzzSelected")
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        lateinit var dialogQRCode: AlertDialog
        fun upiCreateQrCodeDialog(
            context: Context,
            isFromAgentRechargePG : Boolean,
            dialogSingleButtonListener: DialogSingleButtonListener,
            isFromBranchUser: Boolean = false
        ): AlertDialog {

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogeUpiCreateQrBinding = DialogeUpiCreateQrBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            dialogSingleListener = dialogSingleButtonListener
            
            var timer: Long
            val tag = context.getString(R.string.cancel)

            if (isFromAgentRechargePG) {
                timer = 300000L
                binding.tvMessage.text = context.getString(R.string.upi_create_qr_upi_sms_desc)
                
            } else if (isFromBranchUser) {
                timer = 300000L
                binding.tvMessage.text = context.getString(R.string.branch_user_qr_msg)

            } else {
                timer = 180000L
                binding.tvMessage.text = context.getString(R.string.upi_create_qr_desc2)
            }

            val countDownTimer = object : CountDownTimer(timer, 1000) {
                override fun onFinish() {
                    dialogSingleButtonListener.onSingleButtonClick(tag)
                }

                override fun onTick(millisUntilFinished: Long) {
                    timer = millisUntilFinished

                    val m = (timer / 1000) / 60
                    val s = (timer / 1000) % 60

                    val format = String.format("%02d:%02d", m, s)

                    binding.tvTime.text = format
                }

            }
            countDownTimer.start()

            // Disable Go Back button initially and show countdown
            binding.btnGoBack.isEnabled = false
            binding.btnGoBack.text = "Go Back (30)"
            binding.btnGoBack.setBackgroundColor(ContextCompat.getColor(context, R.color.gray)) // use your grey
            binding.btnGoBack.setTextColor(ContextCompat.getColor(context, R.color.white)) // or another readable color

            val goBackTimer = object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsLeft = millisUntilFinished / 1000
                    binding.btnGoBack.text = "Go Back (${secondsLeft})"
                }

                override fun onFinish() {
                    binding.btnGoBack.isEnabled = true
                    binding.btnGoBack.text = "Go Back"
                    binding.btnGoBack.setBackgroundColor(ContextCompat.getColor(context, R.color.light_highlight_color)) // use your theme color
                    binding.btnGoBack.setTextColor(ContextCompat.getColor(context, R.color.colorAccent)) // optional
                }
            }
            goBackTimer.start()


            binding.btnConfirm.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick("qr_confirm")
            }
            binding.btnGoBack.setOnClickListener {
                dialogQRCode=builder
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick("Confirm Release")
            }
            
            builder.setView(binding.root)
            builder.show()
            return builder
        }
        
        fun upiAuthSmsAndVPADialog(
            context: Context,
            isSmsAuth: Boolean,
            dialogSingleButtonListener: DialogSingleButtonListener,
            isFromBranchUser: Boolean = false,
        ): AlertDialog {
            
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogeUpiSmsVpaBinding = DialogeUpiSmsVpaBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            
            dialogSingleListener = dialogSingleButtonListener
            
            if (isSmsAuth){
                binding.tvHeader.text = context.getString(R.string.upi_sms_auth)
            } else {
                binding.tvHeader.text = context.getString(R.string.upi_auth)
            }

            if (isFromBranchUser) {
                binding.tvMessage.text = context.getString(R.string.branch_user_qr_msg)
            } else {
                binding.tvMessage.text = context.getString(R.string.upi_create_qr_upi_sms_desc)
            }
            
            var timer = 300000L
            val tag = context.getString(R.string.cancel)
            
            val countDownTimer = object : CountDownTimer(timer, 1000) {
                override fun onFinish() {
                    dialogSingleButtonListener.onSingleButtonClick(tag)
                }
                
                override fun onTick(millisUntilFinished: Long) {
                    timer = millisUntilFinished
                    
                    val m = (timer / 1000) / 60
                    val s = (timer / 1000) % 60
                    
                    val format = String.format("%02d:%02d", m, s)
                    
                    binding.tvTime.text = format
                }
                
            }
            countDownTimer.start()


            binding.btnGoBack.isEnabled = false
            binding.btnGoBack.text = "Go Back (30)"
            binding.btnGoBack.setBackgroundColor(ContextCompat.getColor(context, R.color.gray)) // use your grey
            binding.btnGoBack.setTextColor(ContextCompat.getColor(context, R.color.white)) // or another readable color

            val goBackTimer = object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsLeft = millisUntilFinished / 1000
                    binding.btnGoBack.text = "Go Back (${secondsLeft})"
                }

                override fun onFinish() {
                    binding.btnGoBack.isEnabled = true
                    binding.btnGoBack.text = "Go Back"
                    binding.btnGoBack.setBackgroundColor(ContextCompat.getColor(context, R.color.light_highlight_color)) // use your theme color
                    binding.btnGoBack.setTextColor(ContextCompat.getColor(context, R.color.colorAccent)) // optional
                }
            }
            goBackTimer.start()
            
            binding.btnConfirm.setOnClickListener {
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick("qr_confirm")
            }
            binding.btnGoBack.setOnClickListener {
                dialogQRCode=builder
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick("Confirm Release")
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }



        fun confirmationDialog(
            context: Context,
            dialogSingleButtonListener: DialogSingleButtonListener,
        ): AlertDialog {

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogeConfirmReleaseTicketBinding = DialogeConfirmReleaseTicketBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            dialogSingleListener = dialogSingleButtonListener

            val tag = context.getString(R.string.cancel)
            
            binding.btnCheckStatus.setOnClickListener {
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick("Check Status")
                if(::dialogQRCode.isInitialized)
                    dialogQRCode.show()
            }

            binding.btnReleaseTicket.setOnClickListener {
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick(tag)
            }

            builder.setView(binding.root)
            builder.show()
            return builder
        }



        fun upiAppsPaymentConfirmationDialog(
            context: Context,
            dialogSingleButtonListener: DialogSingleButtonListener,
        ): AlertDialog {

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogeUpiSmsVpaBinding = DialogeUpiSmsVpaBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            dialogSingleListener = dialogSingleButtonListener

            binding.tvHeader.text = context.getString(R.string.upi_pay_via_upi_apps)
            binding.tvMessage.text = context.getString(R.string.upi_create_qr_upi_apps_success)


            var timer = 300000L
            val tag = context.getString(R.string.cancel)

            val countDownTimer = object : CountDownTimer(timer, 1000) {
                override fun onFinish() {
                    dialogSingleButtonListener.onSingleButtonClick(tag)
                }

                override fun onTick(millisUntilFinished: Long) {
                    timer = millisUntilFinished

                    val m = (timer / 1000) / 60
                    val s = (timer / 1000) % 60

                    val format = String.format("%02d:%02d", m, s)

                    binding.tvTime.text = format
                }

            }
            countDownTimer.start()


            binding.btnGoBack.isEnabled = false
            binding.btnGoBack.text = "Go Back (30)"
            binding.btnGoBack.setBackgroundColor(ContextCompat.getColor(context, R.color.gray)) // use your grey
            binding.btnGoBack.setTextColor(ContextCompat.getColor(context, R.color.white)) // or another readable color

            val goBackTimer = object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsLeft = millisUntilFinished / 1000
                    binding.btnGoBack.text = "Go Back (${secondsLeft})"
                }

                override fun onFinish() {
                    binding.btnGoBack.isEnabled = true
                    binding.btnGoBack.text = "Go Back"
                    binding.btnGoBack.setBackgroundColor(ContextCompat.getColor(context, R.color.light_highlight_color)) // use your theme color
                    binding.btnGoBack.setTextColor(ContextCompat.getColor(context, R.color.colorAccent)) // optional
                }
            }
            goBackTimer.start()



            binding.btnGoBack.setOnClickListener {
                dialogQRCode=builder
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick("Confirm Release")
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun phonePeV2PendingDialog(
            context: Context,
            dialogSingleButtonListener: DialogSingleButtonListener,
            title: String,
            message: String,
            description: String,
            btnText: String,
        ): AlertDialog {

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogeUpiSmsVpaBinding = DialogeUpiSmsVpaBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            dialogSingleListener = dialogSingleButtonListener

            binding.tvTime.gone()
            binding.btnConfirm.gone()
            binding.btnGoBack.visible()
            binding.tvHeader.text = title
            binding.tvHeaderText.text = message
            binding.tvMessage.text = description

            val counterTime = 10
            binding.btnGoBack.isEnabled = false
            binding.btnGoBack.text = "$btnText ($counterTime)"
            binding.btnGoBack.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
            binding.btnGoBack.setTextColor(ContextCompat.getColor(context, R.color.white))

            val goBackTimer = object : CountDownTimer(counterTime * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsLeft = millisUntilFinished / 1000
                    binding.btnGoBack.text = "$btnText (${secondsLeft})"
                }

                override fun onFinish() {
                    binding.btnGoBack.isEnabled = true
                    binding.btnGoBack.text = btnText
                    binding.btnGoBack.setBackgroundColor(ContextCompat.getColor(context, R.color.light_highlight_color))
                    binding.btnGoBack.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                }
            }
            goBackTimer.start()

            binding.btnGoBack.setOnClickListener {
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick(context.getString(R.string.cancel))
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }


        fun phonePeUPICreateQrCodeDialog(
            context: Context,
            bitmap: Bitmap?,
            transactionStatusApiCallListener: ((stopPhonePeTransactionStatusApiCall: Boolean) -> Unit)
        ): AlertDialog {

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogeUpiCreateQrBinding =
                DialogeUpiCreateQrBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.tvHeader.text = "PhonePe UPI"
            if (bitmap != null) {
                binding.qrCodeImage.setImageBitmap(bitmap)
            } else {
                binding.tvHeaderText.gone()
                binding.qrCodeImage.gone()
            }

            var timer = 180000L

            val countDownTimer = object : CountDownTimer(timer, 1000) {
                override fun onFinish() {
                    transactionStatusApiCallListener.invoke(true)
                    if (builder.isShowing || builder != null || (context as AppCompatActivity).isFinishing.not()) {
                        try {
                            builder.cancel()
                        } catch (e: Exception) {
                        }
                    }
                }

                override fun onTick(millisUntilFinished: Long) {
                    timer = millisUntilFinished

                    val m = (timer / 1000) / 60
                    val s = (timer / 1000) % 60

                    val format = String.format("%02d:%02d", m, s)

                    binding.tvTime.text = format
                }

            }
            countDownTimer.start()

            binding.btnConfirm.setOnClickListener {

            }
            binding.btnGoBack.setOnClickListener {
                transactionStatusApiCallListener.invoke(true)
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }


        fun createRedelcomPaymentDialog(
            context: Context,
            dialogSingleButtonListener: DialogSingleButtonListener,
            dialogAnyData: DialogButtonAnyDataListener
        ): AlertDialog {

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogRedelcomTimerBinding =
                DialogRedelcomTimerBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            dialogSingleListener = dialogSingleButtonListener

            var timer = 120000L

            val countDownTimer = object : CountDownTimer(timer, 1000) {
                override fun onFinish() {
                    builder.dismiss()
                    dialogSingleButtonListener.onSingleButtonClick(context.getString(R.string.timeout))
                    transactionFailedDialog(
                        context,
                        context.getString(R.string.transaction_time_out)
                    )

                }

                override fun onTick(millisUntilFinished: Long) {
                    timer = millisUntilFinished

                    val m = (timer / 1000) / 60
                    val s = (timer / 1000) % 60

                    val format = String.format("%02d:%02d", m, s)

                    binding.coundownTV.text = format
                }


            }
            countDownTimer.start()
            dialogAnyData.onDataSend(1, countDownTimer)
            dialogAnyData.onDataSend(2, builder)



            binding.btnConfirm.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick("qr_confirm")
            }
            binding.btnGoBack.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick("go_back")
                builder.cancel()
                builder.dismiss()
                countDownTimer.cancel()
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }


        fun transactionTimeoutDialog(
            context: Context
        ): AlertDialog {

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogRedelcomTransactionTimeoutBinding =
                DialogRedelcomTransactionTimeoutBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.btnGoBack.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun transactionFailedDialog(
            context: Context,
            title: String
        ) {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.booking_status))
                .setMessage(title) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    android.R.string.ok
                ) { dialog, which ->
                    dialog.dismiss()
                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        fun transactionFailedInterfaceDialog(
            context: Context,
            title: String,
            dialogAnyData: DialogButtonAnyDataListener

        ) {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.booking_status))
                .setMessage(title) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setCancelable(false)
                .setPositiveButton(
                    android.R.string.ok
                ) { dialog, which ->
                    dialog.dismiss()
                    dialogAnyData.onDataSend(3, "")
                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }


        fun cityFilterDialog(
            context: Context,
            searchList: List<String>,
            title: String,
            btnText: String,
            reset: Boolean,
            isCancelBtnVisible: Boolean,
            applyCityFilter: ((cityName: String, CitySelected: Boolean) -> Unit)
        ) {
            lateinit var layoutManager: RecyclerView.LayoutManager
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: CityFilterHubChildBinding =
                CityFilterHubChildBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTitle.text = title

            binding.btnApplyFilter.text = btnText
            if (isCancelBtnVisible)
                binding.tvCancel.visible()
            else
                binding.tvCancel.gone()
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            if (reset) {
                binding.rvCityFilters.layoutManager = layoutManager
                val filterAdapter = CityFilterHubAdapter(
                    context,
                    this,
                    searchList,
                    0
                )
                binding.rvCityFilters.adapter = filterAdapter

            } else {
                binding.rvCityFilters.layoutManager = layoutManager

                val filterAdapter = CityFilterHubAdapter(
                    context,
                    this,
                    searchList,
                    lastCheckedPos
                )
                binding.rvCityFilters.adapter = filterAdapter
            }

            binding.btnApplyFilter.setOnClickListener {
                applyCityFilter(filterItemName, true)
                builder.cancel()
            }
            binding.tvCancel.setOnClickListener {
                applyCityFilter("", false)

                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }


        fun seatSelectionDialog(
            context: Context,
            dialogSingleButtonListener: DialogSingleButtonListener,
        ) {

            val searchList = mutableListOf<SearchModel>()
            lateinit var layoutManager: RecyclerView.LayoutManager

            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            val binding: DialogFilterBinding =
                DialogFilterBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.tvTitle.text = context.getString(R.string.select_seats)
            binding.btnApplyFilter.text = context.getString(R.string.apply)

            val searchModel1 = SearchModel()
            searchModel1.name = context.getString(R.string.select_all)
            val searchModel = SearchModel()
            searchModel.name = "SL4"
            searchList.add(searchModel1)
            searchList.add(searchModel)
            searchList.add(searchModel)
            searchList.add(searchModel)

            layoutManager =
                LinearLayoutManager(
                    context.applicationContext,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            binding.rvFilters.layoutManager = layoutManager
            seatSelectionAdapter =
                SeatSelectionAdapter(context.applicationContext, this, searchList)
            binding.rvFilters.adapter = seatSelectionAdapter

            seatSelectionAdapter.setCallback(this)
            seatSelectionAdapter.selectAll(false)

            binding.btnApplyFilter.setOnClickListener {
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick()
            }
            binding.tvCancel.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }


        fun mapPassengerList(
            context: Context,
            searchList: ArrayList<PassengerDetail>,
            onItemPassData: OnItemPassData,
            onSingleButtonListener: DialogSingleButtonListener,
            privilegeResponse: PrivilegeResponseModel?
        ) {

            lateinit var layoutManager: RecyclerView.LayoutManager
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            val binding: MapPassengerListBinding =
                MapPassengerListBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            builder.setOnShowListener { d: DialogInterface? ->
                builder.window!!
                    .clearFlags(
                        WindowManager
                            .LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    )
            }
            binding.cancel.setOnClickListener {
                onSingleButtonListener.onSingleButtonClick("Cancel")
                builder.dismiss()
            }

            if (searchList.size >= 1) {
                binding.rvMapPassengerList.visible()
                binding.boardingPoint.visible()
                binding.passengerCount.visible()
            } else {
                binding.nothingToShow.visible()
                binding.rvMapPassengerList.gone()
                binding.boardingPoint.gone()
                binding.passengerCount.gone()
            }

            layoutManager =
                LinearLayoutManager(
                    context.applicationContext,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            binding.rvMapPassengerList.layoutManager = layoutManager
            val scanAdapter = MapListAdapter(
                context.applicationContext,
                searchList,
                onItemPassData,
                privilegeResponse
            )
            binding.rvMapPassengerList.adapter = scanAdapter


            builder.setView(binding.root)
            builder.show()
        }


        // Shift Passenger dialog
        fun shiftPassengerDialog(
            context: Context,
            title: String,
            message: String,
            fromHeader: String,
            fromSubtitle: String,
            toHeader: String,
            toSubtitle: String,
            newSeat: String,
            oldSeat: String,
            buttonLeftText: String,
            buttonRightText: String,
            singleButtonListener: DialogSingleButtonListener,
            isShiftToVisible:Boolean=true,
            showMergeBusDisclaimer: Boolean? = false
        ) {
            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogShiftPassengerConfirmationBinding =
                DialogShiftPassengerConfirmationBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvHeader.text = title
            binding.tvMessage.text = message
            binding.tvFromHeader.text = fromHeader
            binding.tvFromSubtitle.text = fromSubtitle
            binding.tvToHeader.text = toHeader
            binding.tvToSubtitle.text = toSubtitle
            binding.tvNewSeats.text = newSeat
            binding.tvOldseat.text = oldSeat
            binding.btnDark.text = buttonLeftText
            binding.btnLight.text = buttonRightText
            if(showMergeBusDisclaimer == true) {
                binding.tvMergeBusMessage.visible()
            } else {
                binding.tvMergeBusMessage.gone()
            }
            if(!isShiftToVisible){
                binding.tvNewSeats.gone()
                binding.shiftToTV.gone()
            }
            binding.btnDark.setOnClickListener {
                builder.cancel()
                builder.dismiss()
            }

            binding.btnLight.setOnClickListener {
                builder.cancel()
                singleButtonListener.onSingleButtonClick()
                //finish()
            }
            builder.setView(binding.root)
            builder.show()
        }


        // Successful Msg Dialog
        fun successfulMsgDialog(
            context: Context,
            title: String,
        ) {
            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogShiftingSuccessfulBinding =
                DialogShiftingSuccessfulBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvContent.text = title
            builder.setView(binding.root)
            Handler(Looper.getMainLooper()).postDelayed({
                if (context is Activity){
                    if (context.isFinishing){
                        return@postDelayed
                    }
                }
                if (builder.isShowing)
                    builder.dismiss()
            }, DELAY_MILLIS_18)
            builder.show()
        }


        fun cancelOtpLayoutDialog(
            context: Context,
            singleButtonListener: DialogSingleButtonListener,
            returnDialogInstanceListener: DialogReturnDialogInstanceListener,
            dimissAction: (view: AlertDialog) -> Unit
        ) {
            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: LayoutCancelOtpVerificationBinding =
                LayoutCancelOtpVerificationBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.btnResend.isEnabled = false
            binding.btnVerify.isEnabled = false
            binding.btnVerify.setBackgroundResource(R.drawable.button_default_bg)

            returnDialogInstanceListener.onReturnInstance(builder)

            var minutes = 0.toLong()
            if ((context as BaseActivity).getPrivilegeBase() != null) {

                val privilegeResponse = (context as BaseActivity).getPrivilegeBase()
                val configuredLoginValidityTime = privilegeResponse?.configuredLoginValidityTime

                minutes =
                    configuredLoginValidityTime?.let { TimeUnit.SECONDS.toMillis(it.toLong()) }!!

                val timer = object : CountDownTimer(minutes.toLong(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        binding.otpMsg.text =
                            "Your OTP will Expire in ${millisUntilFinished / 1000} sec"
                    }

                    override fun onFinish() {
                        binding.btnResend.isEnabled = true
                        binding.btnResend.setBackgroundResource(R.drawable.button_selected_bg)
                    }
                }
                timer.start()
            }



            binding.btnVerify.setOnClickListener {
                val otp = binding.etOtp.text.toString()
                singleButtonListener.onSingleButtonClick(otp)

//                builder.dismiss()
//                builder.cancel()
            }

            var reSendOTPCount = 0

            if (reSendOTPCount == 0) {
                binding.btnResend.setOnClickListener {
                    singleButtonListener.onSingleButtonClick("resend")
                    val timer = object : CountDownTimer(minutes.toLong(), 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            binding.otpMsg.text =
                                "Your OTP will Expire in ${millisUntilFinished / 1000} sec"
                            binding.btnResend.isEnabled = false
                            binding.btnResend.setBackgroundResource(R.drawable.button_default_bg)
                        }

                        override fun onFinish() {
                            reSendOTPCount--
                            binding.btnResend.isEnabled = true
                            binding.btnResend.setBackgroundResource(R.drawable.button_selected_bg)
                        }
                    }
                    timer.start()
                    reSendOTPCount++
                }
            }

            binding.etOtp.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) =
                    if (s.toString().isNotEmpty() && s.toString().length == 6) {
                        binding.btnVerify.isEnabled = true
                        binding.btnVerify.setBackgroundResource(R.drawable.button_selected_bg)
                    } else {
                        binding.btnVerify.isEnabled = false
                        binding.btnVerify.setBackgroundResource(R.drawable.button_default_bg)
                    }

                override fun afterTextChanged(s: Editable) {
                }
            })


            binding.btnOtpCancel.setOnClickListener {
                dimissAction.invoke(builder)
                builder.dismiss()
                builder.cancel()

            }

            builder.setView(binding.root)
            builder.show()
        }


        override fun onClickOfNavMenu(position: Int) {
        }

        override fun onClick(view: View, position: Int) {
            if (view != null) {
                Timber.d("$tag radioTag ${view.tag}")
                if (view.tag == PartialPaymentAdapter::class.java.simpleName) {
                    if (::partialPaymentListener.isInitialized) {
                        partialPaymentListener.onButtonClick(PARTIAL_PAYMENT_OPTION, position)
                    }
                } else if (view.tag == PaymentOptionsAdapter.TAG) {
                    otherPaymentPosition = position
                } else if (view.tag == WalletOptionAdapter.TAG || view.tag == WalletOptionAgentRechargeAdapter.TAG) {
                    if (::dialogSingleListener.isInitialized && dialogSingleListener != null) {
                        val tag = "${view.tag}"
                        dialogSingleListener.onSingleButtonClick()
                        dialogSingleListener.onSingleButtonClick("$tag-$position")
                    }
                } else if (view.tag == EasebuzzOptionAdapter.TAG) {
                    if (::dialogSingleListener.isInitialized && dialogSingleListener != null) {
                        val tag = "${view.tag}"
                        dialogSingleListener.onSingleButtonClick()
                        dialogSingleListener.onSingleButtonClick("$tag-$position")
                    }
                } else if (view.tag == "Boarding") {
                    finalBpSelected = BoardingPointDetail(
                        finalBoardingPoint[position].address.toString(),
                        finalBoardingPoint[position].id.toString(),
                        finalBoardingPoint[position].landmark.toString(),
                        finalBoardingPoint[position].name.toString(),
                        finalBoardingPoint[position].time.toString(),
                        finalBoardingPoint[position].distance.toString()
                    )
                    Timber.d("clicktest9911280011:${oldBpSelected?.id} : ${finalBpSelected!!.id}: ${oldDpSelected?.id}: ${finalDpSelected?.id}")
                    if (oldBpSelected?.id.toString() == finalBpSelected!!.id.toString() && oldDpSelected?.id.toString() == finalDpSelected?.id.toString()) {
                        buttonObserver(false)
                    } else {
                        buttonObserver(true)
                    }
                } else if (view.tag == "Dropping") {
                    finalDpSelected = DropOffDetail(
                        finalDropingingPoint[position].address.toString(),
                        finalDropingingPoint[position].id.toString(),
                        finalDropingingPoint[position].landmark.toString(),
                        finalDropingingPoint[position].name.toString(),
                        finalDropingingPoint[position].time.toString(),
                        finalDropingingPoint[position].distance.toString()
                    )
                    if (oldBpSelected?.id.toString() == finalBpSelected!!.id.toString() && oldDpSelected?.id.toString() == finalDpSelected?.id.toString()) {
                        buttonObserver(false)
                    } else {
                        buttonObserver(true)
                    }
                }
            }
        }

        override fun onButtonClick(view: Any, dialog: Dialog) {
        }

        @SuppressLint("ResourceAsColor")
        fun buttonObserver(enabled: Boolean) {
            if (!enabled) {
//                doneButton.setBackgroundColor(R.color.colorDimShadow6)
                doneButton.setBackgroundColor(Color.parseColor("#9b9b9b"))
                doneButton.isEnabled = false
            } else {
                doneButton.setBackgroundColor(R.color.colorPrimary)
                doneButton.setBackgroundColor(Color.parseColor("#00ADB5"))
//                doneButton.backgroundTintList= ColorStateList.valueOf(R.color.colorPrimary)
                doneButton.isEnabled = true
            }
        }

        //ExtendFare/Create Rate Card Dialog
        fun extendFareRateCard(
            context: Context,
            title: String
        ) {
            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogShiftingSuccessfulBinding =
                DialogShiftingSuccessfulBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvContent.text = title

            Handler(Looper.getMainLooper()).postDelayed({
                builder.dismiss()
                var activity = context as Activity
                activity.finish()
            }, 2000)
            builder.setView(binding.root)
            builder.show()
        }

        // Successful Seat block Dialog
        fun successfulBlockSeatDialog(
            context: Context,
            title: String,
        ) {
            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogShiftingSuccessfulBinding =
                DialogShiftingSuccessfulBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvContent.text = title

            Handler(Looper.getMainLooper()).postDelayed({
                if (!(context as Activity).isFinishing) {
                    builder.dismiss()
                }


            }, 2000)
            builder.setView(binding.root)
            builder.show()
        }


        fun updatePassengersDialog(
            context: Context,
        ) {
            // val genderItems = listOf("Male", "Female")
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogUpdatePersonalDetailsBinding =
                DialogUpdatePersonalDetailsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.autoCompleteGender.setAdapter(
                ArrayAdapter<String>(
                    context,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    context.resources.getStringArray(R.array.genderArray)
                )
            )

            binding.btnSaveDetails.setOnClickListener {
                builder.cancel()
            }
            binding.tvCancel.setOnClickListener {
                builder.cancel()
            }

            builder.setView(binding.root)
            builder.show()
        }




        fun moveToExtraSeatDialog(
            context: Context?,
            title: String,
            message: String,
            buttonLeftText: String,
            buttonRightText: String,
            seatNumber: String,
            extraSeatNumber: String,
            dialogButtonMoveSeatExtraListener: DialogButtonMoveSeatExtraListener,
            isEditable: Boolean
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogTwoButtonsMoveToExtraSeatBinding =
                DialogTwoButtonsMoveToExtraSeatBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTitle.text = title
            binding.tvContent.text = message
            binding.btnLeft.text = buttonLeftText
            binding.btnRight.text = buttonRightText
            binding.etExtraSeatNumber.setText(extraSeatNumber)
            binding.btnLeft.setOnClickListener {
                builder.cancel()
            }
            binding.etExtraSeatNumber.isEnabled = isEditable

            binding.btnRight.setOnClickListener {
//                builder.cancel()

                if (binding.etRemarks.text.toString() === "") {
                    context?.toast(context.resources.getString(R.string.enter_remarks))
                } else {
                    dialogButtonMoveSeatExtraListener.onRightButtonClick(
                        binding.etRemarks.text.toString(),
                        seatNumber,
                        binding.etExtraSeatNumber.text.toString(),
                        binding.checkboxSendSms.isChecked
                    )
                    builder.cancel()
                }


            }

            binding.etExtraSeatNumber.setText(seatNumber)
            builder.setView(binding.root)
            // builder.setView(dialogLayout)
            builder.show()
        }


        fun moveToNormalSeatDialog(
            context: Context?,
            title: String,
            message: String,
            buttonLeftText: String,
            buttonRightText: String,
            seatNumber: String,
            extraSeatNumber: String,
            dialogButtonMoveSeatExtraListener: DialogButtonMoveSeatExtraListener,
            isEditable: Boolean,
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogTwoButtonsMoveToNormalSeatBinding =
                DialogTwoButtonsMoveToNormalSeatBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTitle.text = title
            binding.tvContent.text = message
            binding.btnLeft.text = buttonLeftText
            binding.btnRight.text = buttonRightText


            val popupMenu = PopupMenu(context, binding.etExtraSeatNumber)
            for (i in 0 until getAvailableSeats().size) {
                popupMenu.menu.add(getAvailableSeats()[i])
                if (extraSeatNumber.split("-")[1].isNotEmpty())
                    if (extraSeatNumber.split("-")[1] == getAvailableSeats()[i]) {
                        binding.etExtraSeatNumber.setText(getAvailableSeats()[i])
                    }
            }



            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked
                binding.etExtraSeatNumber.setText(menuItem.title)
                true
            }

            binding.etExtraSeatNumber.setOnClickListener {
                popupMenu.show()
            }





            binding.btnLeft.setOnClickListener {
                builder.cancel()
            }
            binding.etExtraSeatNumber.isEnabled = isEditable
            binding.btnRight.setOnClickListener {
//                builder.cancel()

                when {
                    binding.etRemarks.text.toString() === "" -> {
                        context?.toast(context.resources.getString(R.string.enter_remarks))
                    }

                    binding.etExtraSeatNumber.text.toString().isEmpty() -> {
                        context?.toast(context.getString(R.string.please_select_seat_number))
                    }

                    else -> {
                        dialogButtonMoveSeatExtraListener.onRightButtonClick(
                            binding.etRemarks.text.toString(),
                            seatNumber,
                            binding.etExtraSeatNumber.text.toString(),
                            binding.checkboxSendSms.isChecked
                        )
                        builder.cancel()
                    }
                }


            }


            builder.setView(binding.root)
            // builder.setView(dialogLayout)
            builder.show()
        }


        fun editSeatFareDialog(
            context: Context?,
            title: String,
            message: String,
//            newFare: TextInputEditText,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonStringListener: DialogButtonStringListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogTwoButtonsEditFareBinding =
                DialogTwoButtonsEditFareBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTitle.text = title
            binding.tvContent.text = message
//          binding.etAmount.setText(newFare)
            binding.btnLeft.text = buttonLeftText
            binding.btnRight.text = buttonRightText

            binding.btnLeft.setOnClickListener {
                builder.cancel()
                dialogButtonStringListener.onLeftButtonClick("")
            }
            binding.btnRight.setOnClickListener {
                builder.cancel()
                val newFare: String = binding.etAmount.text.toString()
                dialogButtonStringListener.onRightButtonClick(newFare)
            }
            builder.setView(binding.root)
            // builder.setView(dialogLayout)
            builder.show()
        }


        fun bpDpDialog(
            context: Context,
            boardingPoint: MutableList<StageDetail>,
            droppingPoint: MutableList<StageDetail>,
            singleButtonListener: DialogSingleButtonListener,
            selectedBoarding: BoardingPointDetail,
            selectedDropping: DropOffDetail
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogBpDpBinding =
                DialogBpDpBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            finalBoardingPoint.addAll(boardingPoint)
            finalDropingingPoint.addAll(droppingPoint)
            oldBpSelected = selectedBoarding
            oldDpSelected = selectedDropping
            finalBpSelected = selectedBoarding
            finalDpSelected = selectedDropping
            doneButton = binding.tvDone

            setBoardingAdapter(context, binding.rvBpList, boardingPoint, selectedBoarding)
            setDroppingAdapter(context, binding.rvDpList, droppingPoint, selectedDropping)
            binding.SelectedBP.text = selectedBoarding.name
            binding.SelectedDP.text = selectedDropping.name
            binding.tvDone.isEnabled = false
            binding.tabBoarding.setOnClickListener {
                binding.viewBp.backgroundTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.colorPrimary))
                binding.viewDp.backgroundTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.white))
                binding.rvBpList.visible()
                binding.rvDpList.gone()

            }

            binding.tabDropping.setOnClickListener {
                binding.viewBp.backgroundTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.white))
                binding.viewDp.backgroundTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.colorPrimary))
                binding.rvBpList.gone()
                binding.rvDpList.visible()
            }
            binding.tvDone.setOnClickListener {
                putObject(finalBpSelected, SELECTED_BOARDING_DETAIL)
                putObject(finalDpSelected, SELECTED_DROPPING_DETAIL)
                singleButtonListener.onSingleButtonClick(context.getString(R.string.select_bp_dp))
                builder.cancel()
            }

            binding.tvCancel.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            // builder.setView(dialogLayout)
            builder.show()
        }

        fun setBoardingAdapter(
            context: Context,
            rvView: RecyclerView,
            list: MutableList<StageDetail>,
            selected: BoardingPointDetail
        ) {
            lateinit var layoutManager: RecyclerView.LayoutManager
            layoutManager = LinearLayoutManager(
                context.applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            rvView.layoutManager = layoutManager
            val filterAdapter = CoachBpDpBoardingAdapter(
                context.applicationContext,
                list,
                this,
                selected
            )
            rvView.adapter = filterAdapter
        }

        fun setDroppingAdapter(
            context: Context,
            rvView: RecyclerView,
            list: MutableList<StageDetail>,
            selected: DropOffDetail
        ) {
            lateinit var layoutManager: RecyclerView.LayoutManager
            layoutManager = LinearLayoutManager(
                context.applicationContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            rvView.layoutManager = layoutManager
            val filterAdapter = CoachBpDPDroppingListAdapter(
                context.applicationContext,
                list,
                this,
                selected
            )
            rvView.adapter = filterAdapter
        }


        fun androidDeviceIdDailog(
            context: Context,
            message: String,
            dialogSingleButtonListener: DialogSingleButtonListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogAndroidDeviceIdBinding =
                DialogAndroidDeviceIdBinding.inflate(LayoutInflater.from(context))
//            builder.setCancelable(false)
            binding.tvAndroidDeviceId.text = message
            binding.btnReLogin.setOnClickListener {
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick()
                //finish()
            }
            builder.setView(binding.root)
            builder.show()
        }


        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, SeatShiftingSuccessfulActivity::class.java))
            activity.finish()
        }


        override fun onClickOfItem(data: String, position: Int) {
            filterItemName = data
            lastCheckedPos = position
            if (seatlist.contains(filterItemName)) {
                seatlist.remove(filterItemName)
            } else {
                seatlist.add(filterItemName)
            }
        }

        override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

        }

        override fun onCheckedChanged(item: String?, isChecked: Boolean) {
            if (item == "SELECT ALL") {
                seatSelectionAdapter.selectAll(true)
            } else {
                seatSelectionAdapter.selectAll(false)
            }
        }

        override fun onCancelled() {

        }

        override fun onDataSelected(
            firstDate: Calendar?,
            secondDate: Calendar?,
            hours: Int,
            minutes: Int,
        ) {
            if (flagUnblockSeat && ::dialogUnblockSeatsBinding.isInitialized) {
                val scale = dialogUnblockSeatsContext?.resources?.displayMetrics?.density ?: 0.0f
                if (firstDate != null) {
                    if (secondDate == null) {
                        firstDate.set(Calendar.HOUR_OF_DAY, hours)
                        firstDate.set(Calendar.MINUTE, minutes)
                        dialogUnblockSeatsBinding.tvFromDate.visible()
                        dialogUnblockSeatsBinding.tvFromHint.visible()
                        dialogUnblockSeatsBinding.tvFromDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)


                        if (dateType != null && dateType == dialogUnblockSeatsContext?.getString(R.string.fromDate)) {
                            fromDate = SimpleDateFormat(
                                DATE_FORMAT_D_M_Y,
                                Locale.getDefault()
                            ).format(firstDate.time)

                            dialogUnblockSeatsBinding.tvFromDate.text = fromDate
                            val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                            val paddingtLeftRightinDp =
                                (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                            dialogUnblockSeatsBinding.tvFromDate.setPadding(
                                paddingtLeftRightinDp,
                                paddingtTopinDp,
                                paddingtLeftRightinDp,
                                0
                            )
                        } else {
                            toDate = SimpleDateFormat(
                                DATE_FORMAT_D_M_Y,
                                Locale.getDefault()
                            ).format(firstDate.time)

                            dialogUnblockSeatsBinding.tvToDate.text = toDate
                            dialogUnblockSeatsBinding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                            dialogUnblockSeatsBinding.tvToHint.visible()
                            val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                            val paddingtLeftRightinDp =
                                (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                            dialogUnblockSeatsBinding.tvToDate.setPadding(
                                paddingtLeftRightinDp,
                                paddingtTopinDp,
                                paddingtLeftRightinDp,
                                0
                            )
                        }

                    } else {
                        dialogUnblockSeatsBinding.tvToHint.visible()
                        dialogUnblockSeatsBinding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                        fromDate = SimpleDateFormat(
                            DATE_FORMAT_D_M_Y,
                            Locale.getDefault()
                        ).format(firstDate.time)
                        dialogUnblockSeatsBinding.tvFromDate.text = fromDate
                        val paddingtTopinDp = (8 * scale + 0.5f).toInt() //Here 8 means 8dp
                        val paddingtLeftRightinDp = (16 * scale + 0.5f).toInt() //Here 16 means 16dp
                        dialogUnblockSeatsBinding.tvFromDate.setPadding(
                            paddingtLeftRightinDp,
                            paddingtTopinDp,
                            paddingtLeftRightinDp,
                            0
                        )


                        toDate = SimpleDateFormat(
                            DATE_FORMAT_D_M_Y,
                            Locale.getDefault()
                        ).format(secondDate.time)
                        dialogUnblockSeatsBinding.tvToDate.text = toDate
                        dialogUnblockSeatsBinding.tvToDate.setPadding(
                            paddingtLeftRightinDp,
                            paddingtTopinDp,
                            paddingtLeftRightinDp,
                            0
                        )

                    }
                } else {
                    dialogUnblockSeatsBinding.tvFromDate.setBackgroundResource(R.drawable.header_gradient_bg_underline)
                    dialogUnblockSeatsBinding.tvToDate.setBackgroundResource(R.drawable.header_gradient_bg_underline_primary)
                    dialogUnblockSeatsBinding.tvFromDate.gone()
                    dialogUnblockSeatsBinding.tvToHint.gone()
                }

            } else {
                if (firstDate != null) {
                    if (secondDate == null) {
                        firstDate.set(Calendar.HOUR_OF_DAY, hours)
                        firstDate.set(Calendar.MINUTE, minutes)

                        val fromDate = SimpleDateFormat(
                            DATE_FORMAT_D_M_Y,
                            Locale.getDefault()
                        ).format(firstDate.time)

                        if (::dialogPhoneBlockingBinding.isInitialized) {
                            dialogPhoneBlockingBinding.etSelectDate.setText(fromDate)
                        }
                    }
                }
            }
        }

        override fun onItemChecked(
            isChecked: Boolean,
            view: View,
            data1: String,
            data2: String,
            data3: String,
            position: Int
        ) {
            Timber.d("checklict: $count , $list")
        }

        //    FCM Dialog
        fun fcmNotificationDialog(
            context: Context,
            message: String,
            dialogSingleButtonListener: DialogSingleButtonListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogFcmNotificationBinding =
                DialogFcmNotificationBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvFCMDescription.text = message
            binding.btnDismiss.setOnClickListener {
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick()
                //finish()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun crewImageDialog(
            context: Context,
            title: String,
            dialogSingleButtonListener: DialogSingleButtonListener,
            stuffImages: MutableList<StuffImage>,
        ): AlertDialog? {
            val totalSize = stuffImages.size
            var count = 0
            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogCrewImagesBinding =
                DialogCrewImagesBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTitle.text = title

            setGlideImage(context, stuffImages[0].stuff_goods_image, binding.imgMain)

            if (totalSize == 1) {
                binding.imgForward.gone()
                binding.imgBackward.gone()
            } else {
                binding.imgForward.visible()
                binding.imgBackward.gone()
            }

            val heading = "$title (${count.plus(1)}/$totalSize)"
            binding.tvTitle.text = heading

            binding.tvCancel.setOnClickListener {
                builder.cancel()
            }
            binding.tvDelete.setOnClickListener {
                val position = count.toString()
                dialogSingleButtonListener.onSingleButtonClick(position)
            }

            binding.imgBackward.setOnClickListener {
                if (count > 0) {
                    setGlideImage(
                        context,
                        stuffImages[count.minus(1)].stuff_goods_image,
                        binding.imgMain
                    )
                    --count
                    val heading = "$title (${count.plus(1)}/$totalSize)"
                    binding.tvTitle.text = heading
                }
                if (count == 0) {
                    binding.imgForward.visible()
                    binding.imgBackward.gone()
                } else {
                    binding.imgForward.visible()
                    binding.imgBackward.visible()
                }
            }

            binding.imgForward.setOnClickListener {
                if (count < totalSize.minus(1)) {
                    setGlideImage(
                        context,
                        stuffImages[count.plus(1)].stuff_goods_image,
                        binding.imgMain
                    )
                    count++
                    val heading = "$title (${count.plus(1)}/$totalSize)"
                    binding.tvTitle.text = heading
                }
                if (count == totalSize.minus(1)) {
                    binding.imgForward.gone()
                    binding.imgBackward.visible()
                } else {
                    binding.imgForward.visible()
                    binding.imgBackward.visible()
                }
            }

            builder.setView(binding.root)
            builder.show()
            return builder
        }


        //    FCM Dialog
        fun deviceRegistrationDialog(
            context: Context,
            message: String,
            isCancellable: Boolean,
            deviceId: String,
            dialogSingleButtonListener: DialogSingleButtonListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogDeviceRegistrationBinding =
                DialogDeviceRegistrationBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(isCancellable)
            binding.tvRegistrationMsg.text = message
            binding.tvDeviceId.text = deviceId
            binding.btnReLogin.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick(context.getString(R.string.re_login))
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun dialogSrcDestSelection(
            sourceList: MutableList<CitySeqOrder>,
            destinationList: MutableList<CitySeqOrder>,
            context: Context,
            varArgListener: VarArgListener,
        ): AlertDialog? {
            var selectedSrcPosition = 0
            var selectedDestPosition = 0

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogSrcDestBinding =
                DialogSrcDestBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.etSource.setText(if (sourceList.isNotEmpty()) sourceList[0].name else "")
            binding.etDestination.setText(if (destinationList.isNotEmpty()) destinationList[0].name else "")

            binding.etSource.setAdapter(
                ArrayAdapter(
                    context,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    sourceList
                )
            )

            binding.etDestination.setAdapter(
                ArrayAdapter(
                    context,
                    R.layout.spinner_dropdown_item,
                    R.id.tvItem,
                    destinationList
                )
            )

            binding.etSource.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    selectedSrcPosition = position
                }

            binding.etDestination.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    selectedDestPosition = position
                }

            binding.tvCancel.setOnClickListener {
                varArgListener.onButtonClick(
                    context.getString(R.string.cancel)
                )
                builder.cancel()
            }
            binding.btnProcceed.setOnClickListener {
                binding.dialogProgressBar.visible()

                varArgListener.onButtonClick(
                    context.getString(R.string.proceed), selectedSrcPosition, selectedDestPosition
                )
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun dialogPartialPaid(
            context: Context,
            ticketData: Body,
            paymentOptionsList: MutableList<SearchModel>,
            varArgListener: VarArgListener
        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            partialPaymentListener = varArgListener
            lateinit var layoutManager: RecyclerView.LayoutManager
            val lastSelectedPaymentPosition = 0

            val binding: DialogPartialPaidBinding =
                DialogPartialPaidBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            val nameAndAge: String =
                "${ticketData.passengerDetails?.get(0)?.name} (${ticketData.passengerDetails?.get(0)?.age})"
            binding.tvName.text = nameAndAge ?: context.getString(R.string.notAvailable)
            var gender = ticketData.passengerDetails?.get(0)?.gender
                ?: context.getString(R.string.notAvailable)
            if (gender.equals("M", true)) {
                gender = context.getString(R.string.genderM)
            } else if (gender.equals("F", true)) {
                gender = context.getString(R.string.genderF)
            }
            binding.tvGender.text = gender
            binding.tvPnr.text = ticketData.ticketNumber ?: context.getString(R.string.notAvailable)
            binding.tvSeats.text =
                ticketData.seatNumbers ?: context.getString(R.string.notAvailable)
            binding.tvMobileNumber.text = ticketData.passengerDetails?.get(0)?.mobile
                ?: context.getString(R.string.notAvailable)
            binding.tvFromAndTo.text = "${ticketData.origin} - ${ticketData.destination}"
            binding.tvBoardingDroppingStage.text =
                "${ticketData.boardingDetails?.stageName} - ${ticketData.dropOffDetails?.stageName}"

            binding.tvPaidAmt.text = ticketData.partialPaymentDetails?.paidAmount.toString()
            binding.tvRemaining.text = ticketData.partialPaymentDetails?.remainingAmount.toString()
            binding.tvTotal.text = ticketData.partialPaymentDetails?.totalAmount.toString()

            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.rvPaymentOptions.layoutManager = layoutManager
            val filterAdapter =
                PartialPaymentAdapter(
                    context,
                    this,
                    paymentOptionsList,
                    lastSelectedPaymentPosition
                )
            binding.rvPaymentOptions.adapter = filterAdapter

            binding.tvClose.setOnClickListener {
                builder.cancel()
            }
            binding.btnConfirm.setOnClickListener {
                builder.cancel()
                varArgListener.onButtonClick(PARTIAL_CONFIRM_BTN)
            }

            binding.btnRelease.setOnClickListener {
                builder.cancel()
                varArgListener.onButtonClick(PARTIAL_RELEASE_BTN)
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }


        fun dialogPartialPaidNew(
            context: Context,
            passengerName: String,
            passengerAge: String?,
            passengerGender: String?,
            passengerMobile: String?,
            ticketNumber: String?,
            seatNumbers: String?,
            boardingStage: String?,
            dropOffStage: String?,
            origin: String?,
            destination: String?,
            partialPaymentDetails: PartialPaymentDetails?,
            paymentOptionsList: MutableList<SearchModel>,
            currencySymbol: String,
            currencyFormat: String,
            varArgListener: VarArgListener
        ): AlertDialog? {
            val builder = AlertDialog.Builder(context).create()
            partialPaymentListener = varArgListener
            lateinit var layoutManager: RecyclerView.LayoutManager
            val lastSelectedPaymentPosition = 0

            val binding: DialogPartialPaidBinding =
                DialogPartialPaidBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            val nameAndAge: String =
                "${passengerName} (${passengerAge})"
            binding.tvName.text = nameAndAge ?: context.getString(R.string.notAvailable)
            var gender = passengerGender ?: context.getString(R.string.notAvailable)
            if (gender.equals("M", true)) {
                gender = context.getString(R.string.genderM)
            } else if (gender.equals("F", true)) {
                gender = context.getString(R.string.genderF)
            }
            binding.tvGender.text = gender
            binding.tvPnr.text = ticketNumber ?: context.getString(R.string.notAvailable)
            binding.tvSeats.text =
                seatNumbers ?: context.getString(R.string.notAvailable)
            binding.tvMobileNumber.text = passengerMobile
                ?: context.getString(R.string.notAvailable)
            binding.tvFromAndTo.text = "${origin} - ${destination}"
            binding.tvBoardingDroppingStage.text =
                "${boardingStage} - ${dropOffStage}"

            binding.tvPaidAmt.text =
                "$currencySymbol ${partialPaymentDetails?.paidAmount?.convert(currencyFormat) ?: ""}"
            binding.tvRemaining.text =
                "$currencySymbol ${partialPaymentDetails?.remainingAmount?.convert(currencyFormat) ?: ""}"
            binding.tvTotal.text =
                "$currencySymbol ${partialPaymentDetails?.totalAmount?.convert(currencyFormat) ?: ""}"

            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.rvPaymentOptions.layoutManager = layoutManager
            val filterAdapter =
                PartialPaymentAdapter(
                    context,
                    this,
                    paymentOptionsList,
                    lastSelectedPaymentPosition
                )
            binding.rvPaymentOptions.adapter = filterAdapter

            binding.tvClose.setOnClickListener {
                builder.cancel()
            }
            binding.btnConfirm.setOnClickListener {
                builder.cancel()
                varArgListener.onButtonClick(PARTIAL_CONFIRM_BTN)
            }

            binding.btnRelease.setOnClickListener {
                builder.cancel()
                varArgListener.onButtonClick(PARTIAL_RELEASE_BTN)
            }
            builder.setView(binding.root)
            builder.show()
            return builder
        }

        private fun invalidateUnblockSeatsCount(selectedSeatsList: MutableList<String>) {
            if (::dialogUnblockSeatsBinding.isInitialized) {
                if (selectedSeatsList.size > 4) {
                    dialogUnblockSeatsBinding.tvMoreUserType.apply {
                        visibility = View.VISIBLE
                        text = "+ ${selectedSeatsList.size - 4} more"
                    }
                } else {
                    dialogUnblockSeatsBinding.tvMoreUserType.visibility = View.GONE
                }
            }
        }

        fun phoneBlockTotalSeatsDialog(
            context: Context,
            totalSeats: Int,
            serviceHeader: String,
            seatNos: String,

            ) {
            val builder = AlertDialog.Builder(context).create()
            val binding: PhoneBlockTotalSeatsBinding =
                PhoneBlockTotalSeatsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(true)
            binding.tvHeaderText.text = serviceHeader
            binding.tvTotalSeats.text = totalSeats.toString()
            binding.tvSelectedSeatNo.text = seatNos
            binding.btnLight.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun quickBookBookingConfirmedDialog(
            isRedelcomPrintEnable: Boolean,
            ticketCountLabel: String,
            context: Context,
            dialogSingleButtonListener: DialogSingleButtonListener,
        ) {

            val builder = AlertDialog.Builder(context).create()
            val inflater = LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            // val dialogLayout = inflater.inflate(R.layout.dialog_block_seats,null)
            val binding: DialogBookingConfirmedBinding =
                DialogBookingConfirmedBinding.inflate(LayoutInflater.from(context))

            if (!isRedelcomPrintEnable) {
                binding.btnRight.gone()
            } else {
                binding.btnRight.visible()
            }
            binding.tvTicketCountLabel.text = ticketCountLabel
            Glide.with(context)
                .asGif()
                .load(R.drawable.ic_booking_confirmed_tick)
                .into(binding.imgTickIcon)

            builder.setCancelable(true)
            binding.btnRight.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClick(context.getString(R.string.apply))
                builder.cancel()
            }
            binding.btnLeft.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun quickBookBookingConfirmedDialogMOT(
            ticketCountLabel: String,
            context: Context,
            onLeftButtonClick: (() -> Unit),
            onRightButtonClick: (() -> Unit)
        ) {

            val builder = AlertDialog.Builder(context).create()

            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background

            val binding: DialogBookingConfirmedBinding =
                DialogBookingConfirmedBinding.inflate(LayoutInflater.from(context))

            binding.tvTicketCountLabel.text = ticketCountLabel
            binding.btnRight.text = context.getString(R.string.rapid_slash_mot_booking)

            Glide.with(context)
                .asGif()
                .load(R.drawable.ic_booking_confirmed_tick)
                .into(binding.imgTickIcon)

            builder.setCancelable(false)
            binding.btnRight.setOnClickListener {
                onRightButtonClick.invoke()
                builder.cancel()
            }
            binding.btnLeft.setOnClickListener {
                onLeftButtonClick.invoke()
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun dialogInsurance(
            context: Context,
            dialogSingleButtonListener: DialogSingleButtonListener,
            insuranceDetails: InsuranceDetails?,
        ) {
            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: DialogInsuranceBinding =
                DialogInsuranceBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            if (insuranceDetails?.details != null) {
                val layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.rvInsuranceInfo.layoutManager = layoutManager
                val qoalaInsuranceAdapter = QoalaInsuranceAdapter(context, insuranceDetails.details)
                binding.rvInsuranceInfo.adapter = qoalaInsuranceAdapter
            }

            binding.btnInsuranceOkay.setOnClickListener {
                builder.cancel()
                dialogSingleButtonListener.onSingleButtonClick()
            }

            builder.setView(binding.root)
            builder.show()
        }

        fun dialogPhonePePaymentOptions(
            context: Context,
            phonePeDirectUPIOptions: String?,
            onVerifyButtonClick: ((vpa: String, llProgressBar: LinearLayout, btnConfirm: Button, btnVerifyUPI: Button) -> Unit),
            onConfirmButtonClick: ((upiType: String?, userNumber: String?, vpa: String?) -> Unit),
            onCancelButtonClick: (() -> Unit),
        ) {
            if (phonePeDirectUPIOptions.isNullOrEmpty()) {
                context.toast("Phonepe Payment options are Not Available")
                return
            }

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogPhonepePaymentOptionsBinding =
                DialogPhonepePaymentOptionsBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            val phonePeUpiPaymentOptionsList = mutableListOf<PhonePeUPIPaymentOptionModel>()

            phonePeDirectUPIOptions.split(",").forEach {

                val paymentOption = it.split(":")

                if (paymentOption.size > 1) {

                    val phonePeUPIPaymentOptionModel = PhonePeUPIPaymentOptionModel(
                        paymentOption[0],
                        paymentOption[1]
                    )

                    phonePeUpiPaymentOptionsList.add(phonePeUPIPaymentOptionModel)
                }

            }

            binding.apply {

                etUpiId.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        btnVerifyUPI.visible()
                        btnConfirm.isClickable = false
                        btnConfirm.setBackgroundResource(R.drawable.button_default_bg)
                    }
                })

                phonePeUpiPaymentOptionsList.forEach {
                    when (it.phonePeUpiPaymentOptionId) {
                        "5_pay_link" -> {
                            rbPayViaSMS.visible()
                            rbPayViaSMS.tag = "5_pay_link"
                        }

                        "5" -> {
                            rbPayViaQR.visible()
                            rbPayViaQR.tag = "5"
                        }

                        "5_pay_app" -> {
                            rbPayViaPhonePeApp.visible()
                            rbPayViaPhonePeApp.tag = "5_pay_app"
                        }

                        "5_pay_upi" -> {
                            rbPayViaUPI.visible()
                            rbPayViaUPI.tag = "5_pay_upi"
                        }
                    }
                }

                rbPayViaSMS.setOnClickListener {
                    textInputMobileNumber.visible()

                    ivQRCode.gone()
                    llUPI.gone()

                    btnConfirm.isClickable = true
                    btnConfirm.setBackgroundResource(R.drawable.button_selected_bg)

                }

                rbPayViaQR.setOnClickListener {
                    textInputMobileNumber.gone()
                    llUPI.gone()

                    btnConfirm.isClickable = true
                    btnConfirm.setBackgroundResource(R.drawable.button_selected_bg)

                }

                rbPayViaUPI.setOnClickListener {
                    llUPI.visible()
                    ivQRCode.gone()
                    textInputMobileNumber.gone()

                    btnConfirm.isClickable = false
                    btnConfirm.setBackgroundResource(R.drawable.button_default_bg)
                }

                rbPayViaPhonePeApp.setOnClickListener {
                    textInputMobileNumber.visible()

                    llUPI.gone()
                    ivQRCode.gone()

                    btnConfirm.isClickable = true
                    btnConfirm.setBackgroundResource(R.drawable.button_selected_bg)

                }

                llProgressBar.setOnClickListener {

                }

                btnVerifyUPI.setOnClickListener {
                    llProgressBar.visible()
                    //builder.cancel()
                    onVerifyButtonClick.invoke(
                        etUpiId.text.toString(),
                        llProgressBar,
                        btnConfirm,
                        btnVerifyUPI
                    )
                }

                btnConfirm.setOnClickListener {

                    var selectedUpiPaymentType: String? = null
                    var userVPA: String? = null
                    var userMobile: String? = null

                    if (rbPayViaSMS.isChecked) {

                        if (etMobileNumber.text.toString() == "") {
                            context.toast("Mobile Number Cannot be blank")
                            return@setOnClickListener
                        }

                        selectedUpiPaymentType = rbPayViaSMS.tag.toString()
                        userMobile = etMobileNumber.text.toString()
                        userVPA = null


                    } else if (rbPayViaUPI.isChecked) {
                        selectedUpiPaymentType = rbPayViaUPI.tag.toString()
                        userVPA = etUpiId.text.toString()
                        userMobile = null
                    } else if (rbPayViaQR.isChecked) {
                        selectedUpiPaymentType = rbPayViaQR.tag.toString()
                        userMobile = null
                        userVPA = null
                    } else if (rbPayViaPhonePeApp.isChecked) {
                        if (etMobileNumber.text.toString() == "") {
                            context.toast("Mobile Number Cannot be blank")
                            return@setOnClickListener
                        }

                        selectedUpiPaymentType = rbPayViaPhonePeApp.tag.toString()
                        userMobile = etMobileNumber.text.toString()
                        userVPA = null
                    }

                    builder.cancel()
                    onConfirmButtonClick.invoke(
                        selectedUpiPaymentType,
                        userMobile,
                        userVPA
                    )
                }

                btnGoBack.setOnClickListener {
                    onCancelButtonClick.invoke()
                    builder.cancel()
                }
            }

            builder.setView(binding.root)
            builder.show()
        }

        fun dialogQuotaBlockTooltipInfo(
            context: Context,
            quotaTypeValue: String?,
            quotaForValue: String?,
            blockingNoValue: String?,
            seatNosValue: String?,
            remarksValue: String?,
            blockedByValue: String?,
            blockedOnValue: String?,
            genderOnValue: String?,
            privilegeResponseModel:PrivilegeResponseModel?
        ): AlertDialog? {

            val builder = AlertDialog.Builder(context).create()

            val binding: DialogQuotaBlockTooltipInfoBinding =
                DialogQuotaBlockTooltipInfoBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.apply {
                tvQuotaTypeValue.text = quotaTypeValue ?: context.getString(R.string.notAvailable)
                tvQuotaForValue.text = quotaForValue ?: context.getString(R.string.notAvailable)
                tvBlockingNoValue.text = blockingNoValue ?: context.getString(R.string.notAvailable)
                tvSeatNosValue.text = seatNosValue ?: context.getString(R.string.notAvailable)
                tvRemarksValue.text = remarksValue ?: context.getString(R.string.notAvailable)
                tvBlockedByValue.text = blockedByValue ?: context.getString(R.string.notAvailable)
                tvBlockedOnValue.text = blockedOnValue ?: context.getString(R.string.notAvailable)

                if(genderOnValue.equals("") || genderOnValue.isNullOrEmpty() || !privilegeResponseModel?.country.equals("India", true)){
                    tvGenderTitle.gone()
                    tvGenderTitleValue.gone()
                }
                else{ tvGenderTitle.visible()
                    tvGenderTitleValue.visible()
                    tvGenderTitleValue.text = genderOnValue
                }
                binding.btnConfirm.setOnClickListener {
                    builder.cancel()
                }
            }

            builder.setView(binding.root)
            builder.show()
            return builder
        }

        fun showRateCardBottomSheet(
            context: Context,
            position: Int,
            routeWiseRateCardDetailList: MutableList<RouteWiseRateCardDetail>
        ) {
            val bottomSheetDialogShowRateCard =
                BottomSheetDialog(context, R.style.BottomSheetDialog)
            val bindingShowRateCard = BottomSheetShowRateCardBinding.inflate(context.layoutInflater)
            BottomSheetShowRateCardBinding.inflate(context.layoutInflater)

            bottomSheetDialogShowRateCard.apply {
                setContentView(bindingShowRateCard.root)
                setCancelable(false)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            bindingShowRateCard.apply {
                textName.text = routeWiseRateCardDetailList[position].name
                textFutureRateCard.text = routeWiseRateCardDetailList[position].futureRateCard
                textStartDate.text = routeWiseRateCardDetailList[position].startDate
                textEndDate.text = routeWiseRateCardDetailList[position].endDate
                textCreatedBy.text = routeWiseRateCardDetailList[position].createdBy
                textUpdatedBy.text = routeWiseRateCardDetailList[position].updatedBy
                textCreatedAt.text = routeWiseRateCardDetailList[position].createdAt
                textUpdatedAt.text = routeWiseRateCardDetailList[position].updatedAt
                textCoachType.text = routeWiseRateCardDetailList[position].coachType

                btnOkay.setOnClickListener {
                    bottomSheetDialogShowRateCard.dismiss()
                }
            }

            bottomSheetDialogShowRateCard.show()
        }

        fun deleteRateCardDialog(
            rateCardDeleteMsg: String,
            context: Context,
            onLeftButtonClick: (() -> Unit),
            onRightButtonClick: (() -> Unit)
        ) {

            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogDeleteRateCardBinding =
                DialogDeleteRateCardBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.tvContent.text = rateCardDeleteMsg

            binding.btnRight.setOnClickListener {
                onRightButtonClick.invoke()
                builder.cancel()
            }
            binding.btnLeft.setOnClickListener {
                onLeftButtonClick.invoke()
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun showProgressDialog(context: Context) {
            val builder = AlertDialog.Builder(context, R.style.Style_Dialog_Rounded_littl_Corner)
            val dialogBinding = DialogProgressBarBinding.inflate(LayoutInflater.from(context))
            builder.setView(dialogBinding.root)
            progressDialog = builder.create()
            progressDialog!!.setCancelable(false)
            progressDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            progressDialog!!.show()
        }

        fun dismissProgressDialog() {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        }

        fun getProgressDialogVar(): AlertDialog? {
            return progressDialog
        }

        fun updateRemarkDialog(
            context: Context,
            onUpdateButtonClick: ((remark: String) -> Unit),
            onCancelButtonClick: (() -> Unit)
        ) {

            val builder = AlertDialog.Builder(context).create()

            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background

            val binding: DialogUpdateRemarkBinding =
                DialogUpdateRemarkBinding.inflate(LayoutInflater.from(context))

            builder.setCancelable(false)

            binding.clearTV.setOnClickListener {
                binding.textInputEditTextRemarks.setText("")
            }

            binding.btnUpdate.setOnClickListener {
                if (binding.textInputEditTextRemarks.text.toString().trim().isEmpty()) {
                    context.toast("Please enter a remark")
                } else {
                    onUpdateButtonClick.invoke(
                        binding.textInputEditTextRemarks.text.toString()
                    )
                    builder.cancel()
                }
            }

            binding.tvCancel.setOnClickListener {
                onCancelButtonClick.invoke()
                builder.cancel()
            }

            builder.setView(binding.root)
            builder.show()
        }

        fun ticketCancelDialog(
            context: Context,
            title: String,
            message: String,
            srcDest: String,
            journeyDate: String,
            ticketCancellationPercentage: String,
            seatNo: String,
            cancellationAmount: String,
            refundAmount: String,
            buttonLeftText: String,
            buttonRightText: String,
            dialogButtonTagListener: DialogButtonTagListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogCancelTicketBinding =
                DialogCancelTicketBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.apply {
                tvRefundAmount.visible()
                tvCancellationAmount.visible()
                viewBottom2.visible()
                tvCanellationAmmountText.visible()
                tvRefundText.visible()
                tvHeader.text = title
                tvMessage.text = message
                tvHeaderText.text = srcDest
                tvSubtitle.text = journeyDate
                tvTicketCancellationPercentage.text = ticketCancellationPercentage
                tvSelectedSeatNo.text = seatNo
                tvCancellationAmount.text = cancellationAmount
                tvRefundAmount.text = refundAmount
                btnDark.text = buttonLeftText
                btnLight.text = buttonRightText
            }

            if (message.isEmpty()) {
                binding.tvMessage.gone()
                binding.viewBottom2.gone()
            } else {
                binding.tvMessage.visible()
                binding.viewBottom2.visible()
            }

            binding.btnDark.setOnClickListener {
                builder.cancel()
                dialogButtonTagListener.onLeftButtonClick(binding.btnDark)
            }

            binding.btnLight.setOnClickListener {
                builder.cancel()
                dialogButtonTagListener.onRightButtonClick(binding.btnLight)
            }

            // setConfirmOtpCancelPartialTicketObserver()
            builder.setView(binding.root)
            builder.show()
        }


        fun mapErrorDialog(
            context: Context,
            message: String,
            singleButtonListener: DialogSingleButtonListener
        ) {

            val builder = AlertDialog.Builder(context).create()
            val binding: DialogUnauthorizedBinding =
                DialogUnauthorizedBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvTitle.text=context.getString(R.string.stages_configuration_required)
            binding.tvContent.text = message
            binding.btnOk.tag = context.getString(R.string.stages_configuration_required)
            binding.btnOk.text = context.getString(R.string.okay)
            binding.btnOk.setOnClickListener {
                builder.cancel()

            }
            builder.setView(binding.root)
            builder.show()
        }

        fun showCopyFareDialog(
            context: Context,
            selectedChannel: String,
            onApply: ((
                selectedCopyTo: ArrayList<String>,
            ) -> Unit)
        ) {

            val builder = AlertDialog.Builder(context).create()
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
            val binding: DialogCopyFareBinding =
                DialogCopyFareBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(true)

            when (selectedChannel) {
                context.getString(R.string.branch) -> {
                    binding.apply {
                        eBookingCB.text = context.getString(R.string.online_agent)
                        otaChannelsCB.text = context.getString(R.string.otas)
                        onlineCB.text = context.getString(R.string.e_booking)
                    }
                }

                context.getString(R.string.online_agent) -> {
                    binding.apply {
                        eBookingCB.text = context.getString(R.string.branch)
                        otaChannelsCB.text = context.getString(R.string.otas)
                        onlineCB.text = context.getString(R.string.e_booking)
                    }
                }

                context.getString(R.string.otas) -> {
                    binding.apply {
                        eBookingCB.text = context.getString(R.string.branch)
                        otaChannelsCB.text = context.getString(R.string.online_agent)
                        onlineCB.text = context.getString(R.string.e_booking)
                    }
                }

                context.getString(R.string.e_booking) -> {
                    binding.apply {
                        eBookingCB.text = context.getString(R.string.branch)
                        otaChannelsCB.text = context.getString(R.string.online_agent)
                        onlineCB.text = context.getString(R.string.otas)
                    }
                }
            }

            binding.apply {

                copyFareET.setText(selectedChannel)
                val selectedChannels: ArrayList<String> = arrayListOf()

                btnRight.setOnClickListener {
                    if (binding.onlineCB.isChecked) {
                        selectedChannels.add(binding.onlineCB.text.toString())
                    }
                    if (binding.otaChannelsCB.isChecked) {
                        selectedChannels.add(binding.otaChannelsCB.text.toString())

                    }
                    if (binding.eBookingCB.isChecked) {
//                        Timber.d("selectedCheck - ${binding.eBookingCB.text.toString()}")
                        selectedChannels.add(binding.eBookingCB.text.toString())
                    }

                    onApply.invoke(selectedChannels)
                    builder.cancel()
                }
                btnCancel.setOnClickListener {
                    builder.cancel()
                }
            }

            builder.setView(binding.root)
            builder.show()
        }


        fun showCopyAllModifyDialog(
            context: Context,
            fareDetailsList: MutableList<MultistationFareDetails>,
            dialogSingleButtonListener: DialogSingleListButtonListener,
        ) {
//            lateinit var layoutManager: RecyclerView.LayoutManager
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogCopyAllFareModifyBinding = DialogCopyAllFareModifyBinding.inflate(
                LayoutInflater.from(context)
            )
            builder.setCancelable(false)

//            layoutManager = LinearLayoutManager(
//                context.applicationContext,
//                LinearLayoutManager.VERTICAL,
//                false
//            )
//            binding.rvModifyCopyAllFare.layoutManager = layoutManager
//            var selectedFromCityList: MutableList<SpinnerItems> = mutableListOf()
//            val modifyDownSeatFareValueAdapter =
//                ModifyDownSeatFareValueAdapter(
//                    context = context.applicationContext,
//                    fareDetailsList = fareDetailsList
//                )
//            binding.rvModifyCopyAllFare.adapter = modifyDownSeatFareValueAdapter

            binding.btnApply.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClickList(fareDetailsList)
                builder.cancel()
            }
            binding.btnCancel.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun showFullHeightPinInputBottomSheet(
            activity: Activity,
            fragmentManager: FragmentManager,
            pinSize: Int = 6,
            actionName: String,
            onPinSubmitted: (String) -> Unit,
            onDismiss: (() -> Unit)? = null
        ) {
            val bottomSheetFragment = BottomAuthPinFragment.newInstance(pinSize, actionName)
            var isSubmitted = false

            bottomSheetFragment.setPinInputListener(object : BottomAuthPinFragment.PinInputListener {
                override fun onPinSubmitted(pin: String) {
                    if (!isSubmitted) {
                        isSubmitted = true
                        onPinSubmitted(pin)
                    }
                }
            })

            bottomSheetFragment.setOnDismissCallback {
                onDismiss?.invoke()
            }

            bottomSheetFragment.show(fragmentManager, "BottomAuthPinFragment")

            val dialog = bottomSheetFragment.dialog as? BottomSheetDialog
            var isSetup = false
            dialog?.setOnShowListener { dialogInterface ->
                if (!isSetup) {
                    isSetup = true
                    val bottomSheetDialog = dialogInterface as BottomSheetDialog
                    setupFullHeight(bottomSheetDialog, activity)
                }
            }

            onDismiss?.let { dismissCallback ->
                bottomSheetFragment.dialog?.setOnDismissListener {
                    dismissCallback()
                }
            }
        }

        private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog, activity: Activity) {
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                val layoutParams = it.layoutParams
                val windowHeight = getWindowHeight(activity)
                layoutParams?.height = windowHeight
                it.layoutParams = layoutParams
                it.post {
                    behavior.peekHeight = windowHeight
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        private fun getWindowHeight(activity: Activity): Int {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

        @SuppressLint("ClickableViewAccessibility")
        fun setSearchDataPopupDialog(
            context: Context,
            listX: MutableList<Employee>? = mutableListOf(),
            listXCoach: MutableList<AllCoach>? = mutableListOf(),
            viewX: View,
            dialogButtonAnyDataListener: DialogButtonAnyDataListener,
            type: Int,
        ) {
            crewNameList.clear()
            val simpleListAdapter : SimpleListAdapter?
            adapterSearchBpdpBinding = AdapterSearchBpdpBinding.inflate(LayoutInflater.from(context))
            adapterSearchBpdpBinding?.root?.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

            if (!listX.isNullOrEmpty()) {
                for (i in 0 until listX.size) {
                    val obj = Origin()
                    obj.id = listX[i].id.toString()
                    obj.name = "${listX[i].name}, ${listX[i].mobileNumber}"
                    crewNameList.add(obj)
                }
            } else {
                if (listXCoach != null) {
                    for (i in 0 until listXCoach?.size!!) {
                        val obj = Origin()
                        obj.id = listXCoach[i].id.toString()
                        obj.name = listXCoach[i].name
                        crewNameList.add(obj)
                    }
                }
            }

            simpleListAdapter = SimpleListAdapter(context, crewNameList, dialogButtonAnyDataListener, type)
            adapterSearchBpdpBinding?.searchRV?.adapter = simpleListAdapter
//            adapterSearchBpdpBinding?.searchRV?.recycledViewPool?.clear()
            adapterSearchBpdpBinding?.searchRV?.itemAnimator = null
            simpleListAdapter?.notifyDataSetChanged()


            adapterSearchBpdpBinding?.searchET?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    simpleListAdapter.filter.filter(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })


            popupWindowX = PopupWindow(
                adapterSearchBpdpBinding?.root,
                viewX.width, FrameLayout.LayoutParams.WRAP_CONTENT,
                true
            )
            val xOff = 0
            val yOff = viewX.height

            popupWindowX?.showAsDropDown(viewX)
            popupWindowX?.elevation = 10f
            adapterSearchBpdpBinding?.root?.setOnTouchListener { v: View?, event: MotionEvent? ->
                popupWindowX?.dismiss()
                true
            }

        }

        fun showCopyAllModifyAddRateCardDialog(
            context: Context,
            fareDetailsList: MutableList<FetchRouteWiseFareDetail>,
            dialogSingleButtonListener: DialogSingleListButtonListener,
        ) {
//            lateinit var layoutManager: RecyclerView.LayoutManager
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogCopyAllFareModifyBinding = DialogCopyAllFareModifyBinding.inflate(
                LayoutInflater.from(context)
            )
            builder.setCancelable(false)
            binding.btnApply.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClickListFetchFareDetails(fareDetailsList)
                builder.cancel()
            }
            binding.btnCancel.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun showCopyAllModifyEditAddRateCardDialog(
            context: Context,
            fareDetailsList: MutableList<RouteWiseFareDetail>,
            dialogSingleButtonListener: DialogSingleListButtonListener,
        ) {
//            lateinit var layoutManager: RecyclerView.LayoutManager
            val builder = AlertDialog.Builder(context).create()
            val binding: DialogCopyAllFareModifyBinding = DialogCopyAllFareModifyBinding.inflate(
                LayoutInflater.from(context)
            )
            builder.setCancelable(false)
            binding.btnApply.setOnClickListener {
                dialogSingleButtonListener.onSingleButtonClickListViewFareDetails(fareDetailsList)
                builder.cancel()
            }
            binding.btnCancel.setOnClickListener {
                builder.cancel()
            }
            builder.setView(binding.root)
            builder.show()
        }

        fun dialogCancelService(
            context: Context,
            serviceName: String,
            onCancelServiceClick: (()-> Unit)
        ) {
            val builder = AlertDialog.Builder(context).create()

            val binding: DialogMergeBusCancelServiceBinding =
                DialogMergeBusCancelServiceBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            val text = SpannableStringBuilder()
                .append(context.getString(R.string.are_you_sure_to_cancel_the_service))
                .bold {
                    append(serviceName)
                }

            binding.serviceCancelDescription.text = text


            binding.btnGoBack.setOnClickListener {
                builder.dismiss()
            }

            binding.btnCancelService.setOnClickListener {
                onCancelServiceClick.invoke()
                builder.dismiss()
            }

            builder.setView(binding.root)
            builder.show()
        }

        fun dialogServiceCancelled(
            context: Context,
            serviceName: String,
            onDialogDismiss: (()-> Unit)
        ) {
            val builder = AlertDialog.Builder(context).create()

            val binding: DialogServiceCancelledBinding =
                DialogServiceCancelledBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            val text = context.getString(R.string.has_been_cancelled, serviceName)

            binding.tvDescription.text = text

            Handler(Looper.getMainLooper()).postDelayed({

                onDialogDismiss.invoke()
                builder.dismiss()
            }, DELAY_MILLIS_24)

            builder.setView(binding.root)
            builder.show()
        }


        fun rechargeSummaryDialog(
            context: Context,
            title: String,
            name: String,
            amount: String,
            amountInWords: String,
            status: String,
            buttonSingleButtonListener: DialogSingleButtonListener,
        ) {
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        context,
                        R.color.transparent_tint_color
                    )
                )
            )  // for transparent background

            val binding: RechargeSummaryDialogBinding =
                RechargeSummaryDialogBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)
            binding.tvHeaderText.text = title
            binding.tvName.text = name
            binding.tvAmount.text = amount
            binding.tvAmountInWords.text = amountInWords
            binding.tvStatus.text = status

            binding.btnCancel.setOnClickListener {
                builder.dismiss()
            }

            binding.btnConfirm.setOnClickListener {
                buttonSingleButtonListener.onSingleButtonClick("confirm")
                builder.dismiss()
            }

            builder.setView(binding.root)
            builder.show()
        }


        fun shiftBookedSeatConfirmationDialog(
            context: Context,
            title: String,
            sourceSeat: String,
            destinationSeat: String,
            onConfirmClick: (remarks: String, isSendSms: Boolean) -> Unit
        ) {
            val builder = AlertDialog.Builder(context).create()
            LayoutInflater.from(context)
            builder.window?.setBackgroundDrawable(
                ColorDrawable(ContextCompat.getColor(context, R.color.transparent_tint_color))
            ) // for transparent background

            val binding: DragDropBookedSeatConfirmationPopupBinding =
                DragDropBookedSeatConfirmationPopupBinding.inflate(LayoutInflater.from(context))
            builder.setCancelable(false)

            binding.tvHeader.text = title
            val rawString = context.getString(R.string.shifting_from_to, "<b>$sourceSeat</b>", "<b>$destinationSeat</b>")
            val formattedText = HtmlCompat.fromHtml(rawString, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.tvSeatFromTo.text = formattedText

            binding.btnCancel.setOnClickListener {
                builder.dismiss()
            }

            binding.btnConfirm.setOnClickListener {
                val remarks = binding.remarksET.text.toString()
                val isSendSms = binding.sendSmsCB.isChecked
                onConfirmClick.invoke(remarks, isSendSms)
                builder.dismiss()
            }

            builder.setView(binding.root)
            builder.show()
        }
    }


    fun dialogTicketDetails(
        context: Context,
        primaryPassengerName: String,
        seatNumbers: String,
        pnrNumber: String,
        mobileNumber: String,
        fromAndTo: String,
        fare: String,
        printTicketCallback: ((pnrNumber: String) -> Unit),
        viewTicketCallback: ((pnrNumber: String) -> Unit)
    ) {
        val builder = AlertDialog.Builder(context).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding: DialogTicketDetailsBinding =
            DialogTicketDetailsBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)

        binding.tvClose.setOnClickListener {
            builder.cancel()
        }

        binding.tvName.text = primaryPassengerName
        binding.tvSeats.text = seatNumbers
        binding.tvPnr.text = pnrNumber
        binding.tvMobileNumber.text = mobileNumber
        binding.tvFromAndTo.text = fromAndTo
        binding.tvFare.text = fare

        binding.btnViewMoreDetails.setOnClickListener {
            viewTicketCallback.invoke(pnrNumber)
            builder.cancel()
        }

        binding.btnPrintTicket.setOnClickListener {
            printTicketCallback.invoke(pnrNumber)
        }

        builder.setView(binding.root)
        builder.show()
    }

    fun dialogCrewDetails(
        context: Context,
        crewDetailsResponse: PickupChartCrewDetailsResponse,
        privilegeResponseModel: PrivilegeResponseModel,
        callUserCallback: ((mobileNumber: String,isCall : Boolean) -> Unit)
    ) {
        val builder = AlertDialog.Builder(context).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding: DialogCrewDetailsBinding =
            DialogCrewDetailsBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)

        binding.apply {

            if (privilegeResponseModel?.country != null
                && privilegeResponseModel?.country.equals(INDIA, true)
            ) {
                conductorTV.text = context.getString(R.string.conductor)
            } else {
                conductorTV.text = context.getString(R.string.contractor)
            }

            driverOneNameTV.text = crewDetailsResponse.driver1
            driverTwoNameTV.text = crewDetailsResponse.driver2
            driverThreeNameTV.text = crewDetailsResponse.driver3
            cleanerNameTV.text = crewDetailsResponse.cleaner
            conductorNameTV.text = crewDetailsResponse.attendent
            chartOperatedByNameTV.text = crewDetailsResponse.chartOperatedBy
        }


        if (!crewDetailsResponse.driver1Contact.isNullOrEmpty()) {
            binding.driverOneNumberTV.visible()
            binding.imgDriverOneWhatsapp.visible()
            //binding.driverOneNumberTV.text = crewDetailsResponse.driver1Contact
            binding.driverOneNumberTV.setOnClickListener {
                callUserCallback.invoke(crewDetailsResponse.driver1Contact,true)
            }
            binding.imgDriverOneWhatsapp.setOnClickListener {
                callUserCallback.invoke(crewDetailsResponse.driver1Contact,false)
            }

        } else {
            binding.driverOneNumberTV.gone()
            binding.imgDriverOneWhatsapp.gone()
        }

        if (!crewDetailsResponse.driver2Contact.isNullOrEmpty()) {
            binding.driverTwoNumberTV.visible()
            binding.imgDriverTwoWhatsapp.visible()
            //binding.driverTwoNumberTV.text = crewDetailsResponse.driver2Contact

            binding.driverTwoNumberTV.setOnClickListener {
                callUserCallback.invoke(crewDetailsResponse.driver2Contact,true)

            }

            binding.imgDriverTwoWhatsapp.setOnClickListener {
                callUserCallback.invoke(crewDetailsResponse.driver2Contact,false)

            }
        } else {
            binding.driverTwoNumberTV.gone()
            binding.imgDriverTwoWhatsapp.gone()
        }

        if (!crewDetailsResponse.driver3contact.isNullOrEmpty()) {
            binding.driverThreeNumberTV.visible()
            binding.imgDriverThreeWhatsapp.visible()
           /* binding.driverThreeNumberTV.text =
                crewDetailsResponse.driver3contact*/

            binding.driverThreeNumberTV.setOnClickListener {
                callUserCallback.invoke(crewDetailsResponse.driver3contact,true)
            }

            binding.imgDriverThreeWhatsapp.setOnClickListener {
                callUserCallback.invoke(crewDetailsResponse.driver3contact,false)

            }
        } else {
            binding.driverThreeNumberTV.gone()
            binding.imgDriverThreeWhatsapp.gone()
        }

        if (crewDetailsResponse.cleanerContact.isNotEmpty()) {
            binding.cleanerNumberTV.visible()
            binding.imgCleanerWhatsapp.visible()
           // binding.cleanerNumberTV.text = crewDetailsResponse.cleanerContact

            binding.cleanerNumberTV.setOnClickListener {
                callUserCallback.invoke(crewDetailsResponse.cleanerContact,true)
            }

            binding.imgCleanerWhatsapp.setOnClickListener {
                callUserCallback.invoke(crewDetailsResponse.cleanerContact,false)

            }

        } else {
            binding.cleanerNumberTV.gone()
            binding.imgCleanerWhatsapp.gone()
        }


        if (crewDetailsResponse.attendentContact.isNotEmpty()) {
            binding.conductorNumberTV.visible()
            binding.imgConductorWhatsapp.visible()
           /* binding.conductorNumberTV.text =
                crewDetailsResponse.attendentContact*/

            binding.conductorNumberTV.setOnClickListener {
                callUserCallback.invoke(crewDetailsResponse.attendentContact,true)
            }

            binding.imgConductorWhatsapp.setOnClickListener {
                callUserCallback.invoke(crewDetailsResponse.attendentContact,false)

            }

        } else {
            binding.conductorNumberTV.gone()
            binding.imgConductorWhatsapp.gone()
        }

        binding.cancelIV.setOnClickListener {
            builder.cancel()
        }


        builder.setView(binding.root)
        builder.show()
    }





    fun dialogAddBpDpValidation(
        context: Context,
        title: String,
        message: String,
        addBpDpCallBack: ((pnrNumber: String) -> Unit),
        onCancelClick: (() -> Unit),
    ) {
        val builder = AlertDialog.Builder(context).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding: DialogTwoButtonsBinding =
            DialogTwoButtonsBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)

        binding.btnLeft.setOnClickListener {
            onCancelClick.invoke()
            builder.cancel()
        }

        binding.btnRight.setOnClickListener {
            builder.cancel()
            addBpDpCallBack.invoke("")
        }
        binding.btnLeft.text=context.getString(R.string.goBack)
        binding.btnRight.text=context.getString(R.string.proceed)
        binding.tvTitle.text=title
        //binding.tvContent.text=message

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            binding.tvContent.text =
                Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY)
        }
        else {
            binding.tvContent.text = Html.fromHtml(message)
        }
        builder.setView(binding.root)
        builder.show()

    }


    fun dialogAddBpDp(
        context: Context,
        title: String,
        hitAddBpDpApi: (pnrNumber: String,boardingTime:String,droppingTime:String) -> Unit,
        recommendedSeatsResponse: RecommendedSeatsResponse?,
        onCancelClick: (() -> Unit),
        ) {
        val builder = AlertDialog.Builder(context).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding: DialogAddBpDpBinding =
            DialogAddBpDpBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)

        recommendedSeatsResponse?.let {
            when {
                it.isDifferentBp == true && it.isDifferentDp == true -> {
                    binding.boardingLayout.visible()
                    binding.droppingLayout.visible()

                    binding.etBoardingCity.setText(it.sourceInfo?.name?:"")
                    binding.bpET.setText(it.boardingPointInfo?.name?:"")
                    binding.boardingHoursACT.setText(it.boardingPointInfo?.timeHH)
                    binding.boardingMinutesACT.setText(it.boardingPointInfo?.timeMM)


                    binding.etDroppingCity.setText(it.destinationInfo?.name?:"")
                    binding.dpET.setText(it.dropOffDetails?.name?:"")
                    binding.droppingHoursACT.setText(it.dropOffDetails?.timeHH)
                    binding.droppingMinutesACT.setText(it.dropOffDetails?.timeMM)
                }
                it.isDifferentBp==true && it.isDifferentDp==false -> {

                    binding.etBoardingCity.setText(it.sourceInfo?.name?:"")
                    binding.bpET.setText(it.boardingPointInfo?.name?:"")
                    binding.boardingHoursACT.setText(it.boardingPointInfo?.timeHH)
                    binding.boardingMinutesACT.setText(it.boardingPointInfo?.timeMM)

                    binding.etDroppingCity.setText(it.destinationInfo?.name?:"")
                    binding.dpET.setText(it.dropOffDetails?.name?:"")
                    binding.droppingHoursACT.setText(it.dropOffDetails?.timeHH)
                    binding.droppingMinutesACT.setText(it.dropOffDetails?.timeMM)



                    binding.boardingLayout.visible()
                    binding.droppingLayout.gone()
                }
                it.isDifferentDp==true && it.isDifferentBp==false -> {


                    binding.etBoardingCity.setText(it.sourceInfo?.name?:"")
                    binding.bpET.setText(it.boardingPointInfo?.name?:"")
                    binding.boardingHoursACT.setText(it.boardingPointInfo?.timeHH)
                    binding.boardingMinutesACT.setText(it.boardingPointInfo?.timeMM)

                    binding.etDroppingCity.setText(it.destinationInfo?.name?:"")
                    binding.dpET.setText(it.dropOffDetails?.name?:"")
                    binding.droppingHoursACT.setText(it.dropOffDetails?.timeHH)
                    binding.droppingMinutesACT.setText(it.dropOffDetails?.timeMM)


                    binding.boardingLayout.gone()
                    binding.droppingLayout.visible()
                }
            }


        }

        val hoursList = arrayListOf<String>()

        for (hour in 0..23) {
            val formattedHour = String.format("%02d:00", hour)
            hoursList.add(formattedHour)
        }

        binding.boardingHoursACT.setAdapter(ArrayAdapter(context, R.layout.child_city_list,R.id.city_name, hoursList))
        binding.boardingMinutesACT.setAdapter(ArrayAdapter(context,R.layout.child_city_list,R.id.city_name, generateMinuteList()))


        binding.droppingHoursACT.setAdapter(ArrayAdapter(context, R.layout.child_city_list,R.id.city_name, hoursList))
        binding.droppingMinutesACT.setAdapter(ArrayAdapter(context,R.layout.child_city_list,R.id.city_name, generateMinuteList()))


        binding.btnLeft.setOnClickListener {
            onCancelClick.invoke()
            builder.cancel()
        }

        binding.btnRight.setOnClickListener {
            builder.cancel()
            hitAddBpDpApi.invoke("",binding.boardingHoursACT.text.toString()+":"+binding.boardingMinutesACT.text.toString(),binding.droppingHoursACT.text.toString()+":"+binding.droppingMinutesACT.text.toString())
        }
        binding.btnLeft.text=context.getString(R.string.goBack)
        binding.btnRight.text=context.getString(R.string.proceed)
        binding.tvTitle.text=title
        builder.setView(binding.root)
        builder.show()
    }

}


 fun generateMinuteList(): List<String> {
    val minutes = mutableListOf<String>()
    for (minute in 0..59) {
        val formattedMinute = String.format("%02d", minute)
        minutes.add(formattedMinute)
    }
    return minutes
}

