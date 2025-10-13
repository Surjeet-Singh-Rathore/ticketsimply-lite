package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityBranchCollectionSummaryReportBinding
import com.bitla.ts.domain.pojo.all_reports.new_response.BookedByYouNewResponse
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.BranchCollectionSummaryAdapter
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.constants.STORAGE_PERMISSION_CODE
import com.bitla.ts.utils.downloadPdf.DownloadPdf
import com.google.gson.Gson
import toast
import visible
import java.io.File

class BranchCollectionSummaryReportActivity : BaseActivity() {


    private var privileges: PrivilegeResponseModel? = null
    private var travelDate: String = ""
    private var pdfFilename: String = ""
    private var bookingData: BookedByYouNewResponse? = null
    private lateinit var binding: ActivityBranchCollectionSummaryReportBinding
    private var adapter: BranchCollectionSummaryAdapter? = null
    private var currency: String = ""
    private var currencyFormat: String = ""

    override fun isInternetOnCallApisAndInitUI() {}

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityBranchCollectionSummaryReportBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        privileges = getPrivilegeBase()

        getPref()

        binding.updateRatecardToolbar.imgDownload.visible()
        binding.updateRatecardToolbar.imgShare.visible()
        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.branch_collection_report)

        if (intent.hasExtra("data")) {
            val data = intent.getStringExtra("data")
            if (!data.isNullOrEmpty()) {
                bookingData = Gson().fromJson(data, BookedByYouNewResponse::class.java)
                binding.updateRatecardToolbar.headerTitleDesc.text = bookingData?.header
                downloadPdfToShare(bookingData!!.pdfUrl)
            }
        }

        if (bookingData != null) {
            setAdpater()
        }

        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        binding.updateRatecardToolbar.imgDownload.setOnClickListener {
            checkDownloadPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        binding.updateRatecardToolbar.imgShare.setOnClickListener {
            if (pdfFilename.isNotEmpty()) {
                sharePdfFile(pdfFilename)
            } else {
                toast(getString(R.string.error_occured))
            }
        }
    }

    private fun downloadPdfToShare(pdfUrl: String) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(pdfUrl)

        val currentTimeMillis = System.currentTimeMillis()
        pdfFilename = currentTimeMillis.toString() + pdfUrl.substringAfterLast("/")
        if (!pdfFilename.contains(".pdf")) {
            pdfFilename += ".pdf"
        }
        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("Downloading PDF")
        request.setDescription("Downloading PDF file...")
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pdfFilename)
        downloadManager.enqueue(request)

    }

    private fun getPref() {
        if (privileges != null) {
            currency = privileges?.currency?:""
            currencyFormat = getCurrencyFormat(this, privileges?.currencyFormat)
        }
    }


    private fun checkDownloadPermission(permission: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, this)
        if (Build.VERSION.SDK_INT >= 33) {
            DownloadPdf.downloadReportPdf(this, bookingData!!.pdfUrl)

        } else {
            if (permissionResult) {
                DownloadPdf.downloadReportPdf(this, bookingData!!.pdfUrl)

            } else {
                DownloadPdf.onRequestPermissionsResult(STORAGE_PERMISSION_CODE, permission, this)
            }
        }

    }

    private fun setAdpater() {
        adapter = BranchCollectionSummaryAdapter(this, bookingData!!.tickets,privileges)
        binding.listRV.adapter = adapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            DownloadPdf.downloadReportPdf(this, bookingData!!.pdfUrl)
        }
    }


}