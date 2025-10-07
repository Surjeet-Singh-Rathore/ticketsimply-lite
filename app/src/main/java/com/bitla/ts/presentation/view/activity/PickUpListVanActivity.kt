package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.ActivityPickUpBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.pickUpVanChart.PickUpVanRequest
import com.bitla.ts.domain.pojo.pickUpVanChart.VanChartStatusChangeRequest
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.VanChartListAdapter
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getUserRole
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.gson.JsonElement
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible

class PickUpListVanActivity : BaseActivity(), DialogSingleButtonListener,
    DialogButtonAnyDataListener {

    private var phoneNumber: String? = ""
    lateinit var binding: ActivityPickUpBinding
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = ""
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var schedule_id = 0
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var vanChartAdapter: VanChartListAdapter
    private lateinit var boardedSwitch: SwitchCompat
    private lateinit var statusText: TextView
    private var role: String = ""
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var isAgentLogin: Boolean = false

    override fun initUI() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                   showToast(it)
                }
            }
        }
        vanChartObserver()
        updateBoardedStatusObserver()
        binding.progressBar.progressBar.visible()
        getPref()
        swipeRefreshLayout()
        binding.toolbarParent.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.toolbarParent.imgSearch.gone()
        binding.toolbarParent.tvCurrentHeader.text = getString(R.string.pick_up_van_chart)

    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        if (intent.hasExtra("schedule_id")) {
            val schedule = intent.getIntExtra("schedule_id", 0)
            schedule_id = schedule
        }
        privilegeResponseModel = getPrivilegeBase()
        privilegeResponseModel?.let {
            isAgentLogin = privilegeResponseModel?.isAgentLogin ?: false
            role = getUserRole(loginModelPref, isAgentLogin = isAgentLogin, this@PickUpListVanActivity)
        }
        getVanListApi()
    }

    fun getVanListApi() {
        val vanChartRequest = PickUpVanRequest(
            loginModelPref.api_key,
            schedule_id,
            locale!!
        )
        pickUpChartViewModel.getPickUpVanChartApi(
            vanChartRequest
        )
    }

    private fun swipeRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            getVanListApi()
            binding.rvVanChart.gone()
        }
    }

    private fun vanChartObserver() {
        pickUpChartViewModel.pickUpVanResponse.observe(this) { it ->
            binding.progressBar.progressBar.gone()
            binding.refreshLayout.isRefreshing = false
            try {
                if (it.asJsonObject != null) {
                    val response = it.asJsonObject
                    val code = response.get("code").toString()
                    when (code) {
                        "200" -> {
                            val passengerDetails = response.get("passenger_details")
                            val stationList = passengerDetails.asJsonObject.keySet().toList()
                            if (stationList.isNullOrEmpty()) {
                                binding.NoResult.visible()
                            } else {
                                binding.NoResult.gone()
                                binding.rvVanChart.visible()
                                setVanListAdapter(passengerDetails, stationList)
                            }
                        }

                        "401" -> {
                            /*DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            showUnauthorisedDialog()


                        }

                        else -> {
                            binding.NoResult.visible()
                            binding.noResultText.text =
                                response.get("result").asJsonObject.get("message").toString()
                        }
                    }


                } else {
                    toast(getString(R.string.server_error))
                }
            }catch (ex:Exception){
                toast(getString(R.string.something_went_wrong))
            }

        }
    }

    private fun setVanListAdapter(it: JsonElement, keySet: List<String>) {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvVanChart.layoutManager = layoutManager
        vanChartAdapter =
            VanChartListAdapter(
                this@PickUpListVanActivity,
                role,
                it,
                keySet,
                boardedClick = { switchView, status, seatNumber, pnr, name, dialogBox ->
                    boardedSwitch = switchView!!
                    statusText = status!!
                    if (dialogBox) {
                        DialogUtils.vanStatus(
                            this,
                            buttonLeftText = getString(R.string.cancel),
                            buttonRightText = getString(R.string.update),
                            oldStatus = status.text.toString(),
                            changeStatus = { status ->
                                updateBoardedStatusApi(pnr, seatNumber!!, status)
                            }
                        )
                    } else {
                        Timber.d("vanChartStatusChack:: $pnr, $seatNumber")
                        updateBoardedStatusApi(pnr, seatNumber!!, "7")
                    }
                },
                this
            )
        binding.rvVanChart.adapter = vanChartAdapter
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
            locale = locale!!,
            vanChartStatusChangeRequest = VanChartStatusChangeRequest(
                status = status,
                remarks = "status Changed"
            )
        )
    }

    override fun onSingleButtonClick(str: String) {

    }

    private fun updateBoardedStatusObserver() {
        pickUpChartViewModel.updateBoardedStatusResponse.observe(this) {
            Timber.d("reservationblock ${it}")
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

                            if (it.status == "7") {
                                statusText.text = getString(R.string.boarded_status)
                                boardedSwitch.isChecked = true
                            }else {

                            }
                        }


                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
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


    override fun onDataSend(type: Int, file: Any) {
        val code = type
        phoneNumber = file as String
        val perms = arrayOf("android.permission.CALL_PHONE")

        val permsRequestCode = 200
        requestPermissions(perms, permsRequestCode)
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {


    }

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
                                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber))
                                startActivity(intent)
                            } else {
                                toast("Phone number error! please try later")
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