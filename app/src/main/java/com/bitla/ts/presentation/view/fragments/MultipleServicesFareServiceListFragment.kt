package com.bitla.ts.presentation.view.fragments

import EndlessRecyclerOnScrollListener
import android.app.Dialog
import android.content.Context
import android.os.*
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.AdapterSearchBpdpBinding
import com.bitla.ts.databinding.FragmentMultipleServicesFareServiceListBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.route_manager.CitiesListData
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.MultipleServicesManageFareActivity
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.tscalender.SlyCalendarDialog
import gone
import isNetworkAvailable
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.Timber
import toast
import visible
import java.text.SimpleDateFormat
import java.util.*

class MultipleServicesFareServiceListFragment : BaseFragment(), View.OnClickListener,
    DialogButtonAnyDataListener, SlyCalendarDialog.Callback {

    private val TAG: String = MultipleServicesFareServiceListFragment::class.java.simpleName

    private lateinit var binding: FragmentMultipleServicesFareServiceListBinding

    private val viewModel by viewModel<MultipleServicesManageFareViewModel>()
    private val routeManagerViewModel by viewModel<RouteManagerViewModel<Any?>>()

    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String = ""

    private var sourcePopupWindow: PopupWindow? = null
    private var sourceDestAdapter: SourceDestinatinAdapter? = null
    private var citiesList: ArrayList<CitiesListData> = arrayListOf()
    private var sourceCitiesList: ArrayList<CitiesListData> = arrayListOf()
    private var destinationCitiesList: ArrayList<CitiesListData> = arrayListOf()
    private var tempList: ArrayList<CitiesListData> = arrayListOf()
    private var sourceId = ""
    private var destinationId = ""

    private var selectedDate: String = ""
    private var convertedDate: String? = null

    private var availableServicesList = mutableListOf<Result>()
    private lateinit var servicesAdapter: ServiceListAdapter

    private var isAllServicesSelected = false
    private var selectedServicesCount = 0
    private var selectedServices = ""

    private var perPage = 25
    private var totalPages = 1
    private var pageNumber = 1
    private var totalServicesCount = 0
    private var isPagination = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding =
                FragmentMultipleServicesFareServiceListBinding.inflate(inflater, container, false)
            initUI()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        (activity as MultipleServicesManageFareActivity).setToolbarText("")
    }

    private fun initUI() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        handleVisibility(false)

        selectedDate = getTodayDate()
        convertedDate = getDateYMD(selectedDate)
        binding.selectDateET.setText(selectedDate)

        clickListener()

        callCitiesListApi()
        citiesListObserver()
        availableServiceListObserver()

        val onScrollListener = object : EndlessRecyclerOnScrollListener(0) {
            override fun onLoadMore() {
                if (pageNumber < totalPages) {
                    isPagination = true

                    pageNumber++
                    callAvailableServiceListApi()
                }
            }
        }

        binding.rvServices.addOnScrollListener(onScrollListener)
    }

    private fun clickListener() {
        binding.sourceET.setOnClickListener(this)
        binding.destinationET.setOnClickListener(this)
        binding.selectDateET.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.cbCheckAll.setOnClickListener(this)
        binding.btnSetFare.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sourceET -> {
                sourcePopupDialog(getString(R.string.source))
            }

            R.id.destinationET -> {
                sourcePopupDialog(getString(R.string.destination))
            }

            R.id.selectDateET -> {
                val minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)
                SlyCalendarDialog()
                    .setStartDate(stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y))
                    .setMinDate(minDate)
                    .setSingle(true)
                    .setFirstMonday(false)
                    .setCallback(this)
                    .show(requireFragmentManager(), TAG)
            }

            R.id.btnSearch -> {
                if (binding.sourceET.text.toString().isBlank()) {
                    requireContext().toast(getString(R.string.please_select_from))
                } else if (binding.destinationET.text.toString().isBlank()) {
                    requireContext().toast(getString(R.string.please_select_to))
                } else if (binding.selectDateET.text.toString().isBlank()) {
                    requireContext().toast(getString(R.string.please_select_date))
                } else {
                    isPagination = false
                    totalPages = 1
                    pageNumber = 1
                    callAvailableServiceListApi()
                }
            }

            R.id.cbCheckAll -> {
                if (binding.cbCheckAll.isChecked) {
                    availableServicesList.forEach { it.isSelected = true }
                } else {
                    availableServicesList.forEach { it.isSelected = false }
                }

                servicesAdapter.notifyDataSetChanged()
                updateSelectedServiceCount()
            }

            R.id.btnSetFare -> {
                val srcDest =
                    "${binding.sourceET.text.toString()} to ${binding.destinationET.text.toString()}"
                val bundle = Bundle().apply {
                    putBoolean("isAllServicesSelected", isAllServicesSelected)
                    putString("selectedServicesCount", "$selectedServicesCount/$totalServicesCount")
                    putString("selectedServices", selectedServices)
                    putString("sourceDestination", srcDest)
                    putString("travelDate", convertedDate)
                    putString("originId", sourceId)
                    putString("destinationId", destinationId)
                }

                requireActivity().findNavController(R.id.nav_host_fragment_multiple_services_fare)
                    .navigate(R.id.actionServiceListToManageFare, bundle)
            }
        }
    }

    private fun handleVisibility(visible: Boolean) {
        if (visible) {
            binding.servicesListCL.visible()
            binding.btnSetFare.visible()
            binding.tvNoDataAvailable.gone()
        } else {
            binding.servicesListCL.gone()
            binding.btnSetFare.gone()
        }
    }

    private fun enableDisableSetFareBtn(enable: Boolean = false) {
        if (enable) {
            binding.btnSetFare.isEnabled = true
            binding.btnSetFare.backgroundTintList = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.button_highlight_color
            )
        } else {
            binding.btnSetFare.isEnabled = false
            binding.btnSetFare.backgroundTintList = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.colorShadow
            )
        }
    }

    private fun callCitiesListApi() {
        if (requireContext().isNetworkAvailable()) {
            routeManagerViewModel.getCitiesListApi(
                loginModelPref.api_key,
                format_type,
                locale
            )
        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun citiesListObserver() {
        routeManagerViewModel.getCitiesList.observe(viewLifecycleOwner) { response ->
            try {
                when (response.code) {
                    200 -> {
                        if (response?.result != null) {
                            citiesList = response.result
                            citiesList.sortBy { it.name }

                            val city = CitiesListData()
                            city.name = getString(R.string.all)
                            city.id = ""
                            citiesList.add(0, city)

                            sourceCitiesList = citiesList
                            destinationCitiesList = citiesList
                        }
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        requireContext().toast(response.message)
                    }
                }
            } catch (e: Exception) {
                Timber.d("Exception => $e")
            }
        }
    }

    private fun sourcePopupDialog(from: String) {
        val popupBinding: AdapterSearchBpdpBinding =
            AdapterSearchBpdpBinding.inflate(LayoutInflater.from(requireContext()))

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        if (from == getString(R.string.source)) {
            sourceDestAdapter =
                SourceDestinatinAdapter(requireContext(), sourceCitiesList, this, SOURCE)
            popupBinding.searchRV.adapter = sourceDestAdapter
        } else if (from == getString(R.string.destination)) {
            sourceDestAdapter =
                SourceDestinatinAdapter(requireContext(), destinationCitiesList, this, DESTINATION)
            popupBinding.searchRV.adapter = sourceDestAdapter
        }

        popupBinding.searchET.onChange {
            sourceDestAdapter?.filter?.filter(it)
        }

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        val popupHeight = (screenHeight * 0.4).toInt()

        sourcePopupWindow = binding.fromToLayout.width.let {
            PopupWindow(popupBinding.root, it, popupHeight, true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sourcePopupWindow?.elevation = 12.0f;
        }

        sourcePopupWindow?.showAsDropDown(binding.fromToLayout)

        sourcePopupWindow?.elevation = 25f

        popupBinding.searchET.postDelayed({
            popupBinding.searchET.requestFocus()
            showKeyboard(popupBinding.searchET)
        }, 100)
    }

    private fun showKeyboard(view: AppCompatEditText) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun callAvailableServiceListApi() {
        if (requireContext().isNetworkAvailable()) {
            if (isPagination) {
                binding.progressBarBottom.visible()
            } else {
                binding.progressBar.visible()
            }

            viewModel.getAvailableServiceList(
                sourceId,
                destinationId,
                convertedDate.toString(),
                loginModelPref.api_key,
                true,
                pageNumber,
                perPage
            )
        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun availableServiceListObserver() {
        viewModel.availableServiceList.observe(requireActivity()) { response ->
            binding.progressBar.gone()
            binding.progressBarBottom.gone()
            enableDisableSetFareBtn(false)

            if (response != null) {
                when (response.code) {
                    200 -> {
                        handleVisibility(true)

                        if (response.result.size > 0) {
                            totalPages = response.number_of_pages ?: 1
                            totalServicesCount = response.total_count ?: 0

                            if (isPagination) {
                                availableServicesList.addAll(response.result)
                                if (isAllServicesSelected) {
                                    availableServicesList.forEach { it.isSelected = true }
                                }
                                servicesAdapter.notifyDataSetChanged()
                            } else {
                                availableServicesList.clear()
                                availableServicesList = response.result
                                setServiceListAdapter()
                            }

                            updateSelectedServiceCount()
                        }
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        availableServicesList.clear()
                        handleVisibility(false)
                        binding.tvNoDataAvailable.visible()

                        requireContext().toast(response.message)
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }

    private fun updateSelectedServiceCount() {
        isAllServicesSelected = true
        selectedServicesCount = 0
        selectedServices = ""

        availableServicesList.forEach {
            if (it.isSelected) {
                selectedServicesCount++
                selectedServices += it.route_id + ","
            } else {
                isAllServicesSelected = false
            }
        }

        if (isAllServicesSelected) {
            selectedServicesCount = totalServicesCount
            binding.cbCheckAll.isChecked = true
            enableDisableSetFareBtn(true)
        } else {
            binding.cbCheckAll.isChecked = false
            enableDisableSetFareBtn(selectedServicesCount > 0)
        }

        binding.selectedCount.text = "$selectedServicesCount/$totalServicesCount"
    }

    private fun setServiceListAdapter() {
        binding.rvServices.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        servicesAdapter = ServiceListAdapter(requireContext(), availableServicesList, this)
        binding.rvServices.adapter = servicesAdapter
    }

    override fun onClickOfItem(data: String, position: Int) {
        super.onClickOfItem(data, position)

        updateSelectedServiceCount()
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {
        when (type) {
            1 -> {
                val selectedData = file as CitiesListData
                var srcDestTemp = ""
                when (extra as Int) {
                    SOURCE -> {
                        srcDestTemp = binding.sourceET.text.toString()
                        binding.sourceET.setText(selectedData.name)
                        sourceId = selectedData.id

                        tempList = ArrayList(citiesList)
                        tempList.removeIf { city ->
                            city.name == selectedData.name
                        }

                        destinationCitiesList = tempList
                        sourcePopupWindow?.dismiss()
                    }

                    DESTINATION -> {
                        srcDestTemp = binding.destinationET.text.toString()
                        binding.destinationET.setText(selectedData.name)
                        destinationId = selectedData.id

                        tempList = ArrayList(citiesList)
                        tempList.removeIf { city ->
                            city.name == selectedData.name
                        }

                        sourceCitiesList = tempList
                        sourcePopupWindow?.dismiss()
                    }
                }
                if (srcDestTemp != selectedData.name) {
                    handleVisibility(false)
                }
            }
        }
    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?,
        hours: Int,
        minutes: Int,
    ) {
        if (firstDate != null && secondDate == null) {
            val dateTemp = selectedDate
            firstDate.set(Calendar.HOUR_OF_DAY, hours)
            firstDate.set(Calendar.MINUTE, minutes)

            selectedDate = SimpleDateFormat(
                DATE_FORMAT_D_M_Y,
                Locale.getDefault()
            ).format(firstDate.time)

            convertedDate = SimpleDateFormat(
                DATE_FORMAT_Y_M_D,
                Locale.getDefault()
            ).format(firstDate.time)

            binding.selectDateET.setText(selectedDate)

            if (dateTemp != selectedDate) {
                handleVisibility(false)
            }
        }
    }

    override fun onCancelled() {}

    override fun isInternetOnCallApisAndInitUI() {}

    override fun isNetworkOff() {}

    override fun onButtonClick(view: Any, dialog: Dialog) {}

    override fun onDataSend(type: Int, file: Any) {}
}