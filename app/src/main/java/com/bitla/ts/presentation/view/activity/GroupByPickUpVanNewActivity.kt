package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.ActivityGroupByPickUpVanNewBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.pickUpVanChart.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.view_reservation.*
import com.bitla.ts.presentation.adapter.NewPickUpVanChartAdapter
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.*
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import visible
import gone
import isNetworkAvailable
import noNetworkToast
import toast

class GroupByPickUpVanNewActivity : BaseActivity(), DialogSingleButtonListener,
    DialogButtonAnyDataListener {

    private lateinit var binding: ActivityGroupByPickUpVanNewBinding
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()

    private lateinit var boardedSwitch: SwitchCompat
    private lateinit var statusText: TextView
    private var schedule_id = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String = ""
    private var privilegeResponse: PrivilegeResponseModel? = null
    private var role: String = ""
    private var phoneNumber: String? = ""
    private var pickupVanRespHash: ArrayList<RespHash> = arrayListOf()

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var vanChartAdapter: NewPickUpVanChartAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initUI() {
        binding = ActivityGroupByPickUpVanNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            edgeToEdge(binding.root)
        }

        getPref()
        swipeRefreshLayout()
        vanChartObserver()
        updateBoardedStatusObserver()

        val travelDate = intent.getStringExtra("travel_date")
        val pickupVanNumber = intent.getStringExtra("pickup_van_number")
        val coachNumber = intent.getStringExtra("coach_number")

        val convertedDate = travelDate?.let { getNextDate2(it) }

        val descData = if (coachNumber.isNullOrEmpty()) {
            "$convertedDate | $pickupVanNumber"
        } else {
            "$convertedDate | $pickupVanNumber | $coachNumber"
        }

        binding.updatesDetailsToolbar.imageOptionLayout.gone()
        binding.updatesDetailsToolbar.textHeaderTitle.text = getString(R.string.pick_up_van_chart)
        binding.updatesDetailsToolbar.headerTitleDesc.text = descData
        binding.updatesDetailsToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        if (isNetworkAvailable()) getVanListApi() else noNetworkToast()
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        if (intent.hasExtra("schedule_id")) {
            schedule_id = intent.getIntExtra("schedule_id", 0)
        }

        if (getPrivilegeBase() != null) {
            privilegeResponse = getPrivilegeBase()
            privilegeResponse?.let {
                val isAgentLogin = it.isAgentLogin ?: false
                role = getUserRole(loginModelPref, isAgentLogin, this)
            }
        }
    }

    private fun swipeRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            getVanListApi()
            binding.groupByPnrVanChartList.gone()
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
        initUI()
    }

    private fun getVanListApi() {
        val vanChartRequest = PickUpVanRequest(
            loginModelPref.api_key,
            schedule_id,
            locale
        )
        pickUpChartViewModel.getPickUpVanChartApi(
            vanChartRequest
        )
    }

    private fun vanChartObserver() {
        pickUpChartViewModel.pickUpVanResponse.observe(this) {
            binding.includeProgress.progressBar.gone()
            binding.refreshLayout.isRefreshing = false

            val gson = Gson()
            try {
                val response = gson.fromJson(it, ViewReservationResponseModel::class.java)
                when (response.code) {
                    200 -> {
                        if (response.respHash.isNullOrEmpty()) {
                            binding.NoResult.visible()
                            binding.groupByPnrVanChartList.gone()
                        } else {
                            pickupVanRespHash = response.respHash
                            binding.NoResult.gone()
                            binding.groupByPnrVanChartList.visible()
                            setVanListAdapter()
                        }
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        binding.NoResult.visible()
                        binding.noResultText.text = response.result?.message
                    }
                }
            } catch (e: Exception) {
                toast(getString(R.string.something_went_wrong))
            }
        }
    }

    private fun setVanListAdapter() {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.groupByPnrVanChartList.layoutManager = layoutManager
        vanChartAdapter = NewPickUpVanChartAdapter(
            this,
            role,
            pickupVanRespHash,
            privilegeResponse,
            this,
            boardedSwitchAction = { switchView: SwitchCompat, status: TextView, seatNumber: String?, pnr: String, name: String, dialogBox: Boolean ->
                boardedSwitch = switchView
                statusText = status

                if (dialogBox) {
                    DialogUtils.vanStatus(
                        this,
                        buttonLeftText = getString(R.string.cancel),
                        buttonRightText = getString(R.string.update),
                        oldStatus = status.text.toString(),
                        changeStatus = { status1 ->
                            updateBoardedStatusApi(pnr, seatNumber ?: "", status1)
                        }
                    )
                } else {
                    updateBoardedStatusApi(pnr, seatNumber ?: "", "7")
                }
            }
        )
        binding.groupByPnrVanChartList.adapter = vanChartAdapter
    }

    private fun updateBoardedStatusApi(
        pnrNumber: String,
        seatNumber: String,
        status: String,
    ) {
        pickUpChartViewModel.vanChartStatusApi(
            apiKey = loginModelPref.api_key,
            pnrNumber = pnrNumber.replace("\"", ""),
            seatNumber = seatNumber.replace("\"", ""),
            vanChart = true,
            locale = locale,
            vanChartStatusChangeRequest = VanChartStatusChangeRequest(
                status = status,
                remarks = "status Changed"
            )
        )
    }

    private fun updateBoardedStatusObserver() {
        pickUpChartViewModel.updateBoardedStatusResponse.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (::statusText.isInitialized) {
                            when (it.status) {
                                "7" -> {
                                    statusText.text = getString(R.string.boarded_status)
                                    boardedSwitch.isChecked = true
                                }

                                "6" -> {
                                    statusText.text = getString(R.string.yet_to_board)
                                    boardedSwitch.isChecked = false
                                }

                                "8" -> {
                                    statusText.text = getString(R.string.no_show)
                                    boardedSwitch.isChecked = false
                                }
                            }
                        }
                    }

                    401 -> {
                        showUnauthorisedDialog()
                    }

                    else -> {
                        this.toast(it.result.message)
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    override fun onSingleButtonClick(str: String) {}

    override fun onDataSend(type: Int, file: Any) {
        phoneNumber = file as String
        val perms = arrayOf("android.permission.CALL_PHONE")

        val permsRequestCode = 200
        requestPermissions(perms, permsRequestCode)
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            200 -> {
                permissions.forEachIndexed { index, permission ->
                    if (permission == Manifest.permission.CALL_PHONE) {
                        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                            if (phoneNumber != "") {
                                phoneNumber = phoneNumber!!.replace("\"", "")
                                val intent =
                                    Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber))
                                startActivity(intent)
                            } else {
                                toast(getString(R.string.phone_number_is_not_visible))
                            }
                        } else {
                            toast(getString(R.string.call_permission_denied))
                        }
                    }
                }
            }
        }
    }
}