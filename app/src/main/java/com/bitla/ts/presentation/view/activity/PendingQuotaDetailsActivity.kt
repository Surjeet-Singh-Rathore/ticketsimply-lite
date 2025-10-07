package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.phase2.adapter.child.PendingQuotaAdapter
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response.PassengerDetail
import com.bitla.ts.phase2.dashboard_pojo.dashboardAllModels.pending_quota_model.response.PendingQuotaResponse
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.sharedPref.*
import toast

class PendingQuotaDetailsActivity : BaseActivity() {

    private var privileges: PrivilegeResponseModel? = null
    private lateinit var binding: ActivityPendingQuotaDetailsBinding
    private var  pendingQuotaPosition = 0

    override fun initUI() {
        binding = ActivityPendingQuotaDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()
        binding.toolbar.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    @SuppressLint("SetTextI18n")
    private fun getPref() {
        privileges = getPrivilegeBase()
        if (intent.hasExtra(getString(R.string.pending_quota))) {
            pendingQuotaPosition = intent.getIntExtra(getString(R.string.pending_quota),0)
        }

        if (PreferenceUtils.getString(getString(R.string.pending_quota_model_dashboard))?.isNotEmpty() == true) {
            val jsonString = PreferenceUtils.getString(getString(R.string.pending_quota_model_dashboard))
            val pendingQuotaResponseModel = stringToJson<PendingQuotaResponse>(jsonString)
            val resultList = pendingQuotaResponseModel.result
            if (!resultList.isNullOrEmpty() &&
                resultList[0].services != null &&
                resultList[0].services.size > pendingQuotaPosition) {

                val service = resultList[0].services[pendingQuotaPosition]
                val passengerDetailModel: MutableList<PassengerDetail> = service.passengerDetails

                binding.toolbar.tvCurrentHeader.text = "${service.serviceNo} - ${service.origin} - ${service.destination}"
                setPendingQuotaAdapter(passengerDetailModel)
            } else {
                toast(getString(R.string.something_went_wrong))
            }
        } else {
            toast(getString(R.string.something_went_wrong))
        }

    }

    private fun setPendingQuotaAdapter(passengerDetail: MutableList<PassengerDetail>) {

        val layoutManager = LinearLayoutManager(
            /* context = */ this,
            /* orientation = */ LinearLayoutManager.VERTICAL,
            /* reverseLayout = */ false
        )
        val adapter = PendingQuotaAdapter(
            context = this,
            items = passengerDetail,
            privileges
        )

        binding.rvPendingQuotaDetails.layoutManager = layoutManager
        binding.rvPendingQuotaDetails.adapter = adapter
    }
}