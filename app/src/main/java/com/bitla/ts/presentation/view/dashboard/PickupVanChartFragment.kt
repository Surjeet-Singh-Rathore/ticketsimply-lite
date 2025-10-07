package com.bitla.ts.presentation.view.dashboard

import android.app.*
import android.content.*
import android.os.*
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctRequest.*
import com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.view.activity.reservationOption.ViewReservationActivity
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.bitla.tscalender.*
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.text.*
import java.util.*

class PickupVanChartFragment : Fragment(), OnItemClickListener, DialogSingleButtonListener,
    OnclickitemMultiView, OnItemPassData, SlyCalendarDialog.Callback {
    private var privilegeResponse: PrivilegeResponseModel? = null
    private lateinit var binding: FragmentPickupVanChartBinding

    private var sevenDaysDate: String = getTodayDate()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var nextDateAdapter: NextDateAdapterReservation
    private lateinit var myReservationAdapter: MyReservationAdapter
    private lateinit var vanChartAdapter: VanChartAdapter
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var travelDate: String = ""
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = ""
    private var ymdDate: String = ""
    private var dateList = mutableListOf<StageData>()
    private var scheduleId = 0
    private var resID: Long? = null
    private var searchList1 =
        ArrayList<com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPickupVanChartBinding.inflate(layoutInflater)
        getDates()
        binding.progressBar.progressBar.visible()

        edgeToEdgeFromOnlyBottom(binding.root)


        getPref()

        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        Timber.d("dateCheckkk:: ${travelDate}")
        travelDate = getTodayDate()
        
        ymdDate = SimpleDateFormat("yyyy-mm-dd").format(SimpleDateFormat("dd-mm-yyyy").parse(travelDate))
        allotedDirectService(ymdDate)
        allotedObserver()
        swipeRefreshLayout()

        return binding.root
    }


    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

    }

    private fun getDates() {
        // get next seven days date with current date
        pickUpChartViewModel.getNextCalenderDates(sevenDaysDate, travelDate)
        pickUpChartViewModel.listOfDates.observe(requireActivity(), Observer {
            dateList = it
            dateList[0].isSelected = true
            setDatesAdapter()
        })

    }

    private fun isAttachedToActivity(): Boolean {
        return isVisible && activity != null
    }

    private fun setDatesAdapter() {
        try {
            if(isAttachedToActivity()){
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                binding.rvChatDates.layoutManager = layoutManager
                nextDateAdapter = NextDateAdapterReservation(requireActivity(), this, dateList)
                binding.rvChatDates.adapter = nextDateAdapter
            }
        }catch (e: Exception){
            e.printStackTrace()
            requireActivity().toast(e.message)
        }


    }


    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag == resources.getString(R.string.open_calender)) {

            if ((activity as BaseActivity).getPrivilegeBase() != null) {
                privilegeResponse = (activity as BaseActivity).getPrivilegeBase()
                privilegeResponse?.let {

                    if (privilegeResponse?.availableAppModes?.allowToShowPickupChartForPastDates == true) {
                        SlyCalendarDialog()
                            .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                            .setMinDate(stringToDate("1970-01-01", DATE_FORMAT_D_M_Y))
                            .setSingle(true)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(requireFragmentManager(), view.tag.toString())
                    } else {
                        SlyCalendarDialog()
                            .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                            .setMinDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                            .setSingle(true)
                            .setFirstMonday(false)
                            .setCallback(this)
                            .show(requireFragmentManager(), view.tag.toString())
                    }

                }
            } else {
                requireContext().toast(requireContext().getString(R.string.server_error))
            }
        }
        if (view.tag == "DATES") {

            ymdDate = inputFormatToOutput(
                dateList[position].title,
                DATE_FORMAT_MMM_DD_EEE_YYYY,
                DATE_FORMAT_Y_M_D
            ).replace("1970", getCurrentYear())

            travelDate = getDateDMY(ymdDate)!!
            binding.vanPickupChart.gone()
            binding.NoResult.gone()
            allotedDirectService(
                ymdDate
            )
        }

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }


    private fun allotedDirectService(
        travelDate: String,
    ) {
        binding.progressBar.progressBar.visible()
        val allotedDirectRequest = AllotedDirectRequest(
            is_group_by_hubs = false,
            hub_id = null,
            api_key = loginModelPref.api_key,
            travel_date = travelDate,
            page = null,
            per_page = null,
            view_mode = "conductor",
            pagination = false,
            origin = "",
            destination = "",
            locale = locale,
            isCheckingInspector = null,
            serviceFilter = null
        )

        pickUpChartViewModel.allotedServiceApiDirect(
            allotedDirectRequest,
            lock_chart_method_name
        )
    }


    private fun swipeRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            allotedDirectService(ymdDate)
            binding.vanPickupChart.gone()
        }
    }

    private fun allotedObserver() {

        pickUpChartViewModel.dataAllotedServiceDirect.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.progressBar.progressBar.gone()
                binding.refreshLayout.isRefreshing = false

                when (it.code) {
                    200 -> {
                        if (it.picku_van_services.isNullOrEmpty()) {
                            binding.NoResult.visible()
                            binding.vanPickupChart.gone()
//                                binding.noResultText.text= it.result.message
//                                setMyBookingsByHubsAdapter(dummyList)

                        } else {
                            binding.NoResult.gone()
                            setMyBookingsByHubsAdapter(it.picku_van_services)
                        }
                    }
                    401 -> {

                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        (activity as BaseActivity).showUnauthorisedDialog()

                    }
                    else -> {
                        binding.NoResult.visible()
                        binding.noResultText.text = it.result?.message
                        it.result?.message?.let { it1 -> requireContext().toast(it1) }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }

        })
    }

    private fun setMyBookingsByHubsAdapter(
        it: ArrayList<PickuVanService>,
    ) {
        binding.vanPickupChart.visible()
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.vanPickupChart.layoutManager = layoutManager
        vanChartAdapter =
            VanChartAdapter(requireActivity(), setSchedule = { schedule_id ->

                privilegeResponse = (activity as BaseActivity).getPrivilegeBase()

                if (privilegeResponse?.tsPrivileges?.groupByPickupVanChart == true) {
                    scheduleId = schedule_id ?: 0

                    val vanData = it.firstOrNull { van -> van.schedule_id == schedule_id }

                    val intent = Intent(requireContext(), GroupByPickUpVanNewActivity::class.java)
                    intent.putExtra("schedule_id", scheduleId)
                    intent.putExtra("travel_date", vanData?.travel_date ?: "")
                    intent.putExtra("pickup_van_number", vanData?.pickup_van_no ?: "")
                    intent.putExtra("coach_number", vanData?.coach_number ?: "")
                    startActivity(intent)
                } else {
                    scheduleId = schedule_id!!
                    val intent = Intent(requireContext(), PickUpListVanActivity::class.java)
                    intent.putExtra("schedule_id", scheduleId)
                    startActivity(intent)
                }
            }, it)
        binding.vanPickupChart.adapter = vanChartAdapter
    }

    override fun onSingleButtonClick(str: String) {

    }

    override fun onClickMuliView(
        view: View,
        view2: View,
        view3: View,
        view4: View,
        resID: String,
        remarks: String
    ) {

    }

    override fun onClickAdditionalData(view0: View, view1: View) {

    }

    override fun onItemData(view: View, str1: String, str2: String) {

    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {

    }

    override fun onCancelled() {

    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?,
        hours: Int,
        minutes: Int
    ) {
        if (firstDate != null) {
            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours)
                firstDate.set(Calendar.MINUTE, minutes)

                travelDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)
                sevenDaysDate = travelDate
                pickUpChartViewModel.getNextCalenderDates(sevenDaysDate, travelDate)

                ymdDate = inputFormatToOutput(
                    travelDate,
                    DATE_FORMAT_D_M_Y,
                    DATE_FORMAT_Y_M_D
                )

                binding.vanPickupChart.gone()
                binding.NoResult.gone()
                allotedDirectService(
                    ymdDate,
                )
            }
        }
    }

}