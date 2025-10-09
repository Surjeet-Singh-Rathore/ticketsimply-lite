package com.bitla.restaurant_app.presentation.view.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bitla.restaurant_app.R
import com.bitla.restaurant_app.databinding.FragmentReportSectionBinding
import com.bitla.restaurant_app.presentation.utils.Constants.STORAGE_PERMISSION_CODE
import com.bitla.restaurant_app.presentation.utils.DownloadPdf
import com.bitla.restaurant_app.presentation.utils.PreferenceUtils
import com.bitla.restaurant_app.presentation.utils.getDateYMD
import com.bitla.restaurant_app.presentation.utils.getTodayDate
import com.bitla.restaurant_app.presentation.utils.gone
import com.bitla.restaurant_app.presentation.utils.isNetworkAvailable
import com.bitla.restaurant_app.presentation.utils.toast
import com.bitla.restaurant_app.presentation.utils.visible
import com.bitla.restaurant_app.presentation.view.MainActivity
import com.bitla.restaurant_app.presentation.viewModel.RestaurantViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ReportSectionFragment : Fragment() {

    private var binding: FragmentReportSectionBinding? = null
    private val viewModel by viewModels<RestaurantViewModel>()

    private var day = 0
    private var month = 0
    private var year = 0
    private var isRestaurantClicked: Boolean = false
    private var isDownloadPdf: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportSectionBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showToolBar(getString(R.string.reports))

        setCalendarFields()

        setClickListener()

        getSelectedResult()

        setReportObserver()

        (activity as MainActivity).showBottomBar()

    }

    private fun setClickListener() {
        binding?.selectService?.setOnClickListener {
            isRestaurantClicked = false
            val action =
                ReportSectionFragmentDirections.actionReportSectionToRestaurantServiceList(false)
            findNavController().navigate(action)
        }

        binding?.viewReportBtn?.setOnClickListener {
            if (isValidApiCall()) {
                isDownloadPdf = false
                callReportsApi()
            }
        }



        binding?.downloadReportBtn?.setOnClickListener {
            if (isValidApiCall()) {
                isDownloadPdf = true
                checkPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }


        binding?.selectRestaurant?.setOnClickListener {
            isRestaurantClicked = true
            val action =
                ReportSectionFragmentDirections.actionReportSectionToRestaurantServiceList(true)
            findNavController().navigate(action)
        }
        binding?.fromDate?.setOnClickListener {
            openFromCalendar()
        }

        binding?.toDate?.setOnClickListener {
            openToDateCalendar()
        }
    }

    private fun getSelectedResult() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("selectedResult")
            ?.observe(viewLifecycleOwner,
                Observer {
                    if (it.isNotEmpty() && it.contains("@")) {
                        if (isRestaurantClicked) {
                            viewModel.restaurantId = it.substringBefore("@")
                            viewModel.restaurantName = it.substringAfter("@")
                            binding!!.selectRestaurant.setText(viewModel.restaurantName)
                        } else {
                            viewModel.serviceId = it.substringBefore("@")
                            viewModel.serviceName = it.substringAfter("@")
                            binding!!.selectService.setText(viewModel.serviceName)
                        }
                    }

                })
    }

    private fun setReportObserver() {
        viewModel.reportsResponse.observe(viewLifecycleOwner, Observer {
            binding?.progressBarList?.gone()
            it?.getContentIfNotHandled()?.let {
                when (it.code) {
                    200 -> {
                        if (isDownloadPdf) {
                            DownloadPdf.downloadReportPdf(
                                requireContext(), it.pdfUrl
                            )
                        } else {
                            val bundle=Bundle()
                            bundle.putParcelable("reportsData",it)
                            bundle.putString("fromDate",getDateYMD(binding!!.fromDate.text.toString()))
                            bundle.putString("toDate",getDateYMD(binding!!.toDate.text.toString()))
                            bundle.putString("serviceId",viewModel.serviceId)
                            bundle.putString("restaurantId",viewModel.restaurantId)
                            findNavController().navigate(R.id.action_ReportsFragment_to_MealsReportFragment,bundle)
                        }
                    }

                    else->{
                        requireContext().toast(it.message)
                    }
                }
        }})
    }

    private fun isValidApiCall(): Boolean {
        when {
            viewModel.restaurantId.isNullOrEmpty() -> requireContext().toast(getString(R.string.restaurant_not_selected))
            else -> return true
        }
        return false
    }


    private fun checkPermission(permission: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, requireActivity())
        if (Build.VERSION.SDK_INT >= 33) {
            callReportsApi()
        } else {
            if (permissionResult) {
                callReportsApi()
            } else {
                DownloadPdf.onRequestPermissionsResult(
                    STORAGE_PERMISSION_CODE,
                    permission,
                    requireActivity()
                )
            }
        }
    }


    private fun callReportsApi() {
        if (requireContext().isNetworkAvailable()) {
            binding?.progressBarList?.visible()
            viewModel.getReportsApi(
                apiKey = PreferenceUtils.getLogin().api_key ?: "",
                "hash",
                isDownloadPdf,
                getDateYMD(binding!!.fromDate.text.toString()),
                getDateYMD(binding!!.toDate.text.toString()),
                "en",
                1,
                5,
                true,
                viewModel.restaurantId,
                viewModel.serviceId
            )
        } else {
            requireContext().toast(getString(R.string.network_not_available))
        }
    }

    private fun setCalendarFields() {
        getTodayDate().let {
            binding!!.fromDate.text = it
            binding!!.toDate.text = it
        }
    }


    fun openFromCalendar() {
        try {
            val listener =
                DatePickerDialog.OnDateSetListener {
                        _, year, monthOfYear, dayOfMonth,
                    ->
                    val selectedFromDate = "$dayOfMonth-${monthOfYear.plus(1)}-$year"
                    binding!!.fromDate.text = selectedFromDate
                    binding!!.toDate.text = ""
                    binding!!.toDate.hint = getString(R.string.please_select_to_date)
                }

            val getFromDate = binding!!.fromDate.text.toString()
            if (getFromDate.contains("-")) {
                val splitFromDate = getFromDate.split("-")
                day = splitFromDate[0].toInt()
                month = splitFromDate[1].toInt().minus(1)
                year = splitFromDate[2].toInt()
            }

            val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
            dpDialog.show()
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }


    }

    private fun openToDateCalendar() {
        val selectedFromDate = binding!!.fromDate.text.toString()
        if (selectedFromDate.contains("-")) {
            val listener =
                DatePickerDialog.OnDateSetListener {
                        _, year, monthOfYear, dayOfMonth,
                    ->

                    val selectedToDate = "$dayOfMonth-${monthOfYear.plus(1)}-$year"

                    val sdf = SimpleDateFormat("dd-MM-yyyy")
                    if (selectedFromDate.contains("-") && selectedToDate.contains("-")) {
                        val fromDate: Date = sdf.parse(selectedFromDate)
                        val toDate: Date = sdf.parse(selectedToDate)

                        val dateResult = fromDate.compareTo(toDate)
                        if (dateResult > 0) {
                            context?.toast(getString(R.string.date_compare))
                            binding!!.toDate.text = ""
                        } else {
                            binding!!.toDate.text = selectedToDate
                            binding!!.toDate.hint = getString(R.string.please_select_to_date)
                        }
                    } else
                        binding!!.toDate.text = selectedToDate
                }
            val getToDate = binding!!.toDate.text.toString()
            if (getToDate.contains("-")) {
                val splitToDate = getToDate.split("-")
                if (splitToDate.size > 2) {
                    day = splitToDate[0].toInt()
                    month = splitToDate[1].toInt().minus(1)
                    year = splitToDate[2].toInt()
                }
            }

            val maxDateCalendar = Calendar.getInstance()
            val minDateCalendar = Calendar.getInstance()
            val splitFromDate = selectedFromDate.split("-")
            if (splitFromDate.size > 2) {
                val day = splitFromDate[0].toInt()
                val month = splitFromDate[1].toInt().minus(1)
                val year = splitFromDate[2].toInt()

                maxDateCalendar.set(Calendar.DAY_OF_MONTH, day.plus(30))
                maxDateCalendar.set(Calendar.MONTH, month)
                maxDateCalendar.set(Calendar.YEAR, year)

                minDateCalendar.set(Calendar.DAY_OF_MONTH, day)
                minDateCalendar.set(Calendar.MONTH, month)
                minDateCalendar.set(Calendar.YEAR, year)
            }

//            setDateLocale("id", requireContext())
            val dpDialog = DatePickerDialog(requireContext(), listener, year, month, day)
            dpDialog.datePicker.minDate = minDateCalendar.timeInMillis
//            dpDialog.datePicker.maxDate = maxDateCalendar.timeInMillis
            dpDialog.show()
        } else
            requireContext().toast(getString(R.string.selectFromDateFirst))


    }
}