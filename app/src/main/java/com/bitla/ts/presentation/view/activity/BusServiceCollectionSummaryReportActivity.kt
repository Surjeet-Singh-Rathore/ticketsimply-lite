package com.bitla.ts.presentation.view.activity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.ActivityBusServiceCollectionSummaryReportBinding
import com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
import com.bitla.ts.domain.pojo.all_reports.new_response.bus_service_collection_summary_report_data.BusServiceCollectionReportResponse
import com.bitla.ts.presentation.adapter.BusServiceCollectionSummaryReportAdapter
import com.bitla.ts.presentation.viewModel.AllReportsViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.constants.STORAGE_PERMISSION_CODE
import com.bitla.ts.utils.downloadPdf.DownloadPdf
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.gson.Gson
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible
import java.io.File

class BusServiceCollectionSummaryReportActivity: BaseActivity(), DialogSingleButtonListener, DialogButtonAnyDataListener {
    private lateinit var binding: ActivityBusServiceCollectionSummaryReportBinding
    private var adapter: BusServiceCollectionSummaryReportAdapter? = null
    private val allReportsViewModel by viewModel<AllReportsViewModel<Any?>>()
    private var currency: String = ""
    private var travelDate: String = ""
    private var country: String = ""
    private var currencyFormat: String = ""
    private var apiKey: String? = null
    private var locale: String? = ""
    private var pdfFilename: String = ""
    private var reqBody: ReqBody?= null
    private var busServiceData: BusServiceCollectionReportResponse? = null

    override fun isInternetOnCallApisAndInitUI() {}
    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {}
    override fun onSingleButtonClick(str: String) {}

    override fun initUI() {
        binding = ActivityBusServiceCollectionSummaryReportBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        getPref()
        lifecycleScope.launch {
            allReportsViewModel.messageSharedFlow.collect {
                if(it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }

        if(country.equals("India",true)){
            binding.updateRatecardToolbar.imgDownload.visible()
            binding.updateRatecardToolbar.imgShare.visible()
        }else{
            binding.updateRatecardToolbar.imgDownload.visible()
            binding.updateRatecardToolbar.imgDownload.setColorFilter(resources.getColor(R.color.colorPrimaryDark))

            binding.updateRatecardToolbar.imgShare.gone()
        }

        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.bus_service_collection)

        if (intent.hasExtra("data")) {
            val data = intent.getStringExtra("data")
            if (!data.isNullOrEmpty()) {
                busServiceData = Gson().fromJson(data, BusServiceCollectionReportResponse::class.java)
                if(busServiceData?.pdfUrl != null)
                    downloadPdfToShare(busServiceData?.pdfUrl!!)
                else
                    toast(getString(R.string.error_occured))
            }
        }

        if(busServiceData != null) {
            setUpAdapter(busServiceData!!)
        }

        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener{
            onBackPressed()
        }

        binding.updateRatecardToolbar.imgDownload.setOnClickListener{
            checkDownloadPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        binding.updateRatecardToolbar.imgShare.setOnClickListener{
            if(pdfFilename.isNotEmpty()) {
                sharePdfFile(pdfFilename)
            } else {
                toast(getString(R.string.error_occured))
            }
        }

        if(intent.hasExtra("req_data")){
            val data = intent.getStringExtra("req_data")
            reqBody = Gson().fromJson(data,ReqBody::class.java)
        }

        if (intent.hasExtra("travel_date")) {
            travelDate = intent.getStringExtra("travel_date") ?: ""
            binding.updateRatecardToolbar.headerTitleDesc.text = travelDate
        }

        binding.totalSeatsValueTV.text = busServiceData?.totalSeats?.ifEmpty { "N/A" }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            DownloadPdf.downloadReportPdf(this, busServiceData!!.pdfUrl)
        }
    }

    private fun downloadPdfToShare(pdfUrl: String) {
        try {
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(pdfUrl)

            val currentTimeMillis = System.currentTimeMillis()
            pdfFilename = currentTimeMillis.toString() + pdfUrl.substringAfterLast("/")
            if (!pdfFilename.contains(".pdf")) {
                pdfFilename += ".pdf"
            }
            val request = DownloadManager.Request(uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle(getString(R.string.downloading_pdf_title))
            request.setDescription(getString(R.string.downloading_pdf_description))
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pdfFilename)
            downloadManager.enqueue(request)
        }catch (e : Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }
    }

    private fun getPref() {
        if (PreferenceUtils.getPrivilege() != null) {
            val privilegeResponseModel = PreferenceUtils.getPrivilege()!!
            currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(this, privilegeResponseModel.currencyFormat)
            country = privilegeResponseModel.country
        }
    }


    private fun checkDownloadPermission(permission: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, this)
        if (Build.VERSION.SDK_INT >= 33) {
            DownloadPdf.downloadReportPdf(this, busServiceData?.pdfUrl)
        } else {
            if (permissionResult) {
                DownloadPdf.downloadReportPdf(this, busServiceData?.pdfUrl)
            } else {
                DownloadPdf.onRequestPermissionsResult(STORAGE_PERMISSION_CODE, permission, this)
            }
        }
    }

    private fun setUpAdapter(list: BusServiceCollectionReportResponse) {
        if (list.result != null) {
            adapter = BusServiceCollectionSummaryReportAdapter(this, list.result)
            binding.apply {
                originDestinationSeatsRV.adapter = adapter
                originDestinationSeatsRV.layoutManager = LinearLayoutManager(this@BusServiceCollectionSummaryReportActivity)
            }

        } else {
            toast(getString(R.string.no_data_available))
        }
    }

    override fun onDataSend(type: Int, file: Any) {}

}