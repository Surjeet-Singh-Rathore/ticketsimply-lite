package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.alloted_Service_method_name
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.ActivityBusServiceCollectionDetailedReportBinding
import com.bitla.ts.databinding.DialogProgressBarBinding
import com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
import com.bitla.ts.domain.pojo.all_reports.new_response.bus_service_collection_summary_report_data.BusServiceCollectionReportResponse
import com.bitla.ts.domain.pojo.all_reports.new_response.occupany_report_data.PagenationData
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.BusServiceCollectionDetailedReportAdapter
import com.bitla.ts.presentation.adapter.PagenationNumberAdapter
import com.bitla.ts.presentation.viewModel.AllReportsViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.constants.STORAGE_PERMISSION_CODE
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.downloadPdf.DownloadPdf
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.gson.Gson
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.io.File

class BusServiceCollectionDetailedReportActivity: BaseActivity(), DialogSingleButtonListener, DialogButtonAnyDataListener {
    private lateinit var binding: ActivityBusServiceCollectionDetailedReportBinding
    private var adapter: BusServiceCollectionDetailedReportAdapter? = null
    private val allReportsViewModel by viewModel<AllReportsViewModel<Any?>>()
    private var currency: String = ""
    private var loginModelPref: LoginModel = LoginModel()
    private var travelDate: String = ""
    private var country: String = ""
    private var currencyFormat: String = ""
    private var lastPos: Int = 0
    private var apiKey: String? = null
    private var locale: String? = ""
    private var coachId: String? = ""
    private var pdfFilename: String = ""
    private var reqBody: ReqBody?= null
    private var noOfPages = 10
    private var pageAdapter: PagenationNumberAdapter?= null
    private var pagenationList : ArrayList<PagenationData> = arrayListOf()
    private var busServiceData: BusServiceCollectionReportResponse? = null
    val privilegeResponseModel: PrivilegeResponseModel? = null

    override fun isInternetOnCallApisAndInitUI() {}
    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {}
    override fun onSingleButtonClick(str: String) {}

    override fun initUI() {
        binding = ActivityBusServiceCollectionDetailedReportBinding.inflate(layoutInflater)
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

        setBusServiceCollectionDetailedReportObserver()

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

                pagenationList.clear()
                var nosOfpage:Int =0
                nosOfpage = busServiceData?.number_of_pages?:noOfPages
                for (i in 0 until nosOfpage){
                    val obj = PagenationData()
                    obj.position = i
                    obj.isSelected = false
                    pagenationList.add(obj)
                }
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

        if(pagenationList.size > 1){
            binding.bottomV.visible()
            pagenationList[0].isSelected  = true
            setPageAdapter()
        }else{
            binding.bottomV.gone()
        }

        binding.prevPageBT.setOnClickListener {
            if(lastPos + 1 != 1){
                pageAdapter?.changeItemPosition(lastPos,lastPos-1)

            }
        }

        binding.nextButtonBT.setOnClickListener {
            if(busServiceData!!.number_of_pages!! > lastPos + 1){
                pageAdapter!!.changeItemPosition(lastPos,lastPos+1)

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
        if(intent.hasExtra("coach_id")) {
            coachId = intent.getStringExtra("coach_id") ?: ""
        }
    }

    private fun setPageAdapter() {

        pageAdapter = PagenationNumberAdapter(this,pagenationList,this)
        binding.pagenationRV.adapter = pageAdapter


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
          //  pdfFilename = currentTimeMillis.toString() + pdfUrl.substringAfterLast("/")
           pdfFilename = currentTimeMillis.toString() + "file.pdf"
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
        loginModelPref = PreferenceUtils.getLogin()
        apiKey = loginModelPref.api_key
        locale = PreferenceUtils.getlang()
        locale =locale.toString()

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
            if (PreferenceUtils.getPrivilege() != null) {
                val privilegeResponseModel = PreferenceUtils.getPrivilege()!!
            }
            adapter = BusServiceCollectionDetailedReportAdapter(this, list.result, privilegeResponseModel)
            binding.listRV.adapter = adapter

        } else {
            toast(getString(R.string.no_data_available))
        }
    }

    private fun showProgressDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.Style_Dialog_Rounded_littl_Corner)
        val dialogBinding = DialogProgressBarBinding.inflate(LayoutInflater.from(context))
        builder.setView(dialogBinding.root)
        DialogUtils.progressDialog = builder.create()
        DialogUtils.progressDialog!!.setCancelable(false)
        DialogUtils.progressDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        DialogUtils.progressDialog!!.show()
    }

    private fun hitBusServiceCollectionNewApi(pageCount : Int) {
        reqBody?.page = pageCount
        reqBody?.apiKey = apiKey ?: ""
        reqBody?.locale = locale ?: "en"
        allReportsViewModel.busServiceCollectionNewApi(
            apiKey = apiKey ?: "",
            locale = locale ?: "en",
            coachId = coachId ?: "",
            reqBody!!,
            apiType = alloted_Service_method_name
        )
    }

    private fun dismissProgressDialog() {
        if (DialogUtils.progressDialog != null && DialogUtils.progressDialog!!.isShowing) {
            DialogUtils.progressDialog!!.dismiss()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setBusServiceCollectionDetailedReportObserver() {
        allReportsViewModel.busServiceCollectionReport.observe(this) {
            Timber.d("allReports - BusServiceCollectionDetailedReport ->> $it")
            dismissProgressDialog()

            if(it != null) {
                when(it.code) {
                    200 -> {
                        binding.pagenationRV.smoothScrollToPosition(lastPos)
                        setUpAdapter(it)
                    }

                    400 -> {
                        showUnauthorisedDialog()
                    }
                    else -> {
                        if (it.message != null) {
                            it.message.let { it1 -> toast(it1) }
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDataSend(type: Int, file: Any) {
        when(type) {
            1 -> {

                lastPos = file as Int
                if(lastPos + 1 == 1){
                    binding.prevPageBT.background = ContextCompat.getDrawable(this,R.drawable.button_selected_bg_grey_round)
                    binding.prevPageBT.setImageResource(R.drawable.page_left_grey)
                }else{
                    binding.prevPageBT.background = ContextCompat.getDrawable(this,R.drawable.button_selected_bg_round)
                    binding.prevPageBT.setImageResource(R.drawable.page_left_white)
                }

                if(busServiceData?.number_of_pages == lastPos + 1){
                    binding.nextButtonBT.background = ContextCompat.getDrawable(this,R.drawable.button_selected_bg_grey_round)
                    binding.nextButtonBT.setImageResource(R.drawable.page_right_grey)
                }else{
                    binding.nextButtonBT.background = ContextCompat.getDrawable(this,R.drawable.button_selected_bg_round)
                    binding.nextButtonBT.setImageResource(R.drawable.page_right_white)
                }

                showProgressDialog(this)
                for (i in 0 until pagenationList.size){
                    pagenationList[i].isSelected = false
                }
                pagenationList[lastPos].isSelected = true
                pageAdapter?.notifyDataSetChanged()
                hitBusServiceCollectionNewApi(lastPos+1)
            }
        }
    }
}