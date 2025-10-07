package com.bitla.ts.presentation.view.merge_bus

import android.content.Intent
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.response_format
import com.bitla.ts.databinding.ActivityMergeBusShiftBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.view.activity.InterCityAgentActivity
import com.bitla.ts.presentation.view.merge_bus.adapter.ExactRouteServicesAdapter
import com.bitla.ts.presentation.view.merge_bus.adapter.NearByMatchesAdapter
import com.bitla.ts.presentation.view.merge_bus.pojo.ExactRouteService
import com.bitla.ts.presentation.viewModel.MergeBusSharedViewModel
import com.bitla.ts.presentation.viewModel.MergeBusShiftPassengerViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getDateDMYY
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.common.stringToDate
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D
import com.bitla.ts.utils.constants.RESTART_OCCUPANCY_GRID_ACTIVITY_REQUEST_CODE
import com.bitla.ts.utils.sharedPref.PREF_RESERVATION_ID
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.tscalender.SlyCalendarDialog
import gone
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MergeBusShiftActivity : BaseActivity(), SlyCalendarDialog.Callback {
    private lateinit var binding: ActivityMergeBusShiftBinding
    private val mergeBusShiftPassengerViewModel by viewModel<MergeBusShiftPassengerViewModel<Any?>>()
    private var loginModelPref: LoginModel = LoginModel()

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                binding.selectDestinationET.setText( result.data?.getStringExtra("mergeBusSelectedDestination"))
                mergeBusShiftPassengerViewModel.destinationIdRightCoach.value= result.data?.getStringExtra("mergeBusSelectedDestinationId")
            }
        }

    private var resultLauncherRestartOccupancyGridActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESTART_OCCUPANCY_GRID_ACTIVITY_REQUEST_CODE) {
                val intent = Intent()
                setResult(RESTART_OCCUPANCY_GRID_ACTIVITY_REQUEST_CODE, intent)
                finish()
            }
        }


    companion object {
        val TAG = MergeBusShiftActivity::class.java.simpleName
    }
    override fun initUI() {
        binding = ActivityMergeBusShiftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()
        setObserver()
        getIntentAndSetViewModel()
        setToolBar()
        setNearbyMatchesAdapter()
        setOnClickListeners()
    }

    private fun setObserver() {
        mergeBusShiftPassengerViewModel.shiftToServicesList.observe(this) {

            hideLoader()
            if (it != null) {

                when (it.code) {
                    200 -> {
                       if(!it.exactRouteServices.isNullOrEmpty()){
                           binding.exactRouteTV.text =
                               getString(R.string.exact_route_services, it.exactRouteServices?.size)

                           setExactRouteAdapter(it.exactRouteServices ?: arrayListOf())
                       }
                    }
                    401 -> {
//                        openUnauthorisedDialog()
                        super.showUnauthorisedDialog()
                    } else -> {
                        toast(it.message)
                    }
                }

            } else {
                toast(getString(R.string.server_error))
            }

        }
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
    }

    override fun isInternetOnCallApisAndInitUI() {

    }


    private fun getIntentAndSetViewModel() {

        if (intent.getStringExtra(getString(R.string.origin)) != null) {
            mergeBusShiftPassengerViewModel.origin.value =
                intent.getStringExtra(getString(R.string.origin))
        }
        if (intent.getStringExtra(getString(R.string.destination)) != null) {
            mergeBusShiftPassengerViewModel.destination.value =
                intent.getStringExtra(getString(R.string.destination))
        }
        if (intent.getStringExtra(getString(R.string.res_id)) != null) {
            mergeBusShiftPassengerViewModel.resId.value =
                intent.getStringExtra(getString(R.string.res_id))
        }

        if (intent.getStringExtra(getString(R.string.toolbarheader)) != null) {
            mergeBusShiftPassengerViewModel.toolBarHeader.value =
                intent.getStringExtra(getString(R.string.toolbarheader))
        }

        if (intent.getStringExtra(getString(R.string.date)) != null) {
            mergeBusShiftPassengerViewModel.travelDate.value =
                intent.getStringExtra(getString(R.string.date))
        }

        if (intent.getStringExtra(getString(R.string.source_id)) != null) {
            mergeBusShiftPassengerViewModel.originIdLeftCoach.value =
                intent.getStringExtra(getString(R.string.source_id))
        }

        if (intent.getStringExtra(getString(R.string.destination_id)) != null) {
            mergeBusShiftPassengerViewModel.destinationIdLeftCoach.value =
                intent.getStringExtra(getString(R.string.destination_id))

            mergeBusShiftPassengerViewModel.destinationIdRightCoach.value =
                intent.getStringExtra(getString(R.string.destination_id))

        }
        setToolBar()
        binding.selectDateET.setText(getDateDMYY(mergeBusShiftPassengerViewModel.travelDate.value.toString()))
        binding.selectDestinationET.setText(mergeBusShiftPassengerViewModel.destination.value.toString())
    }

    private fun setOnClickListeners() {
        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            finish()
        }

        binding.selectDestinationET.setOnClickListener {
            val intent = Intent(
                this,
                InterCityAgentActivity::class.java
            )
            intent.putExtra(
                getString(R.string.res_id),
                mergeBusShiftPassengerViewModel.resId.value?.toInt()

            )
            intent.putExtra(
                getString(R.string.from_city),
                mergeBusShiftPassengerViewModel.origin.value
            )

            intent.putExtra(
                getString(R.string.is_from_mergebus),
                true
            )

            PreferenceUtils.setPreference(
                PREF_RESERVATION_ID,
                mergeBusShiftPassengerViewModel.resId.value
            )
            resultLauncher.launch(intent)
        }

        binding.selectDateET.setOnClickListener {
            var minDate = stringToDate(getTodayDate(), DATE_FORMAT_D_M_Y)

            SlyCalendarDialog()
                .setStartDate(minDate)
                .setMinDate(minDate)
                .setSingle(true)
                .setFirstMonday(false)
                .setCallback(this)
                .show(supportFragmentManager, TAG)
        }


        binding.btnSearchService.setOnClickListener {
            showLoader()
            mergeBusShiftPassengerViewModel.getShiftToServicesList(loginModelPref.api_key,mergeBusShiftPassengerViewModel.originIdLeftCoach.value.toString(),mergeBusShiftPassengerViewModel.destinationIdRightCoach.value.toString(),
                response_format,mergeBusShiftPassengerViewModel.travelDate.value.toString(),PreferenceUtils.getlang(),mergeBusShiftPassengerViewModel.resId.value.toString())
        }


    }



    private fun showLoader() {
        binding.progressBar.visible()
    }

    private fun hideLoader() {
        binding.progressBar.gone()
    }
    private fun setToolBar() {
        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.shift_passengers)
        binding.updateRatecardToolbar.headerTitleDesc.text =
            mergeBusShiftPassengerViewModel.toolBarHeader.value
    }


    private fun setNearbyMatchesAdapter() {
        val adapter = NearByMatchesAdapter(this)
        binding.nearServiceRV.adapter = adapter
    }

    private fun setExactRouteAdapter(exactRouteServices: ArrayList<ExactRouteService>) {
        binding.exactRouteServiceCV.visible()
        val adapter = ExactRouteServicesAdapter(this,exactRouteServices) {

            val intent = Intent(this, MergeBusActivity::class.java)

            intent.putExtra("leftCoachReservationId", mergeBusShiftPassengerViewModel.resId.value)
            intent.putExtra("leftCoachOriginId", mergeBusShiftPassengerViewModel.originIdLeftCoach.value)
            intent.putExtra("leftCoachDestinationId", mergeBusShiftPassengerViewModel.destinationIdLeftCoach.value)

            intent.putExtra("rightCoachExactRouteService", it)

            resultLauncherRestartOccupancyGridActivity.launch(intent)
        }
        binding.serviceRV.adapter = adapter
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

                val travelDate = SimpleDateFormat(
                    DATE_FORMAT_D_M_Y,
                    Locale.getDefault()
                ).format(firstDate.time)

                val convertedDate = SimpleDateFormat(
                    DATE_FORMAT_Y_M_D,
                    Locale.getDefault()
                ).format(firstDate.time)
                mergeBusShiftPassengerViewModel.travelDate.value = convertedDate

                binding.selectDateET.setText(getDateDMYY(mergeBusShiftPassengerViewModel.travelDate.value.toString()))

            }
        }
    }

}