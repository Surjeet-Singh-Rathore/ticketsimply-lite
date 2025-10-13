package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.annotation.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.net.*
import android.os.*
import android.view.*
import androidx.annotation.*
import androidx.core.content.*
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.all_reports.all_report_request.*
import com.bitla.ts.domain.pojo.all_reports.new_response.occupany_report_data.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.downloadPdf.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import com.google.gson.*
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.io.*

class OccupancyReportActivity : BaseActivity(), DialogSingleButtonListener,
    DialogButtonAnyDataListener {

    private var lastPos: Int = 0
    private var reqBody: ReqBody ?= null
    private var travelDate: String = ""
    private var pdfFilename: String = ""
    private var currentPage: Int = 0
    private var bookingData: OccupancyNewResponse? = null
    private lateinit var binding: ActivityOccupancyReportBinding
    private var adapter: NewOccupancyReportAdapter? = null
    private var pageAdapter: PagenationNumberAdapter?= null
    private var currency: String = ""
    private var currencyFormat: String = ""
    private val allReportsViewModel by viewModel<AllReportsViewModel<Any?>>()
    private var pagenationList : ArrayList<PagenationData> = arrayListOf()

    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    
    override fun isInternetOnCallApisAndInitUI() {}

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityOccupancyReportBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        getPref()
        lifecycleScope.launch {
            allReportsViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                  showToast(it)
                }
            }
        }
        setUpOccupancyReportViewObserver()

        binding.updateRatecardToolbar.imgDownload.visible()
        binding.updateRatecardToolbar.imgShare.visible()
        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.occupancy_report)

        if (intent.hasExtra("data")) {
            val data = intent.getStringExtra("data")
            if (!data.isNullOrEmpty()) {
                bookingData = Gson().fromJson(data, OccupancyNewResponse::class.java)
                downloadPdfToShare(bookingData!!.pdfUrl!!)
                binding.totalCountValueTV.text = bookingData!!.total_items.toString()

                pagenationList.clear()
                for (i in 0 until bookingData!!.number_of_pages!!){
                    val obj = PagenationData()
                    obj.position = i
                    obj.isSelected = false
                    pagenationList.add(obj)
                }
            }
        }

        binding.prevPageBT.setOnClickListener{
            if(lastPos + 1 != 1){
                pageAdapter!!.changeItemPosition(lastPos,lastPos-1)

            }
        }

        binding.nextButtonBT.setOnClickListener {
            if(bookingData!!.number_of_pages!! > lastPos + 1){
                pageAdapter!!.changeItemPosition(lastPos,lastPos+1)

            }
        }
        
        if (pagenationList.size > 1) {
            binding.bottomV.visible()
            pagenationList[0].isSelected = true
            setPageAdapter()
        } else {
            binding.bottomV.gone()
            
        }

        if(intent.hasExtra("req_data")){
            val data = intent.getStringExtra("req_data")
            reqBody = Gson().fromJson(data,ReqBody::class.java)
        }

        if (intent.hasExtra("travel_date")) {
            travelDate = intent.getStringExtra("travel_date") ?: ""
            binding.updateRatecardToolbar.headerTitleDesc.text = travelDate
        }

        if (bookingData != null) {
            setAdpater(bookingData!!)
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

    private fun hitOccupancyReportApi(pageCount : Int){
        val body = reqBody
        body?.page = pageCount
        allReportsViewModel.occupancyReportApiViewOnly(
            body!!,
            alloted_Service_method_name
        )

    }

    private fun setUpOccupancyReportViewObserver() {
        allReportsViewModel.newOccupancyReportDetail.observe(this) { it ->
            Timber.d("allReports_TicketBookedByYou$it")
            dismissProgressDialog()

            if (it != null) {

                when (it.code) {
                    200 -> {

                        binding.pagenationRV.smoothScrollToPosition(lastPos)

                        setAdpater(it)

                    }

                    401 -> {
                       /* DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()
                    }

                    else -> {
                        if (it.message != null) {
                            it.message?.let { it1 -> toast(it1) }
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun dismissProgressDialog() {
        if (DialogUtils.progressDialog != null && DialogUtils.progressDialog!!.isShowing) {
            DialogUtils.progressDialog!!.dismiss()
        }
    }

    fun showProgressDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.Style_Dialog_Rounded_littl_Corner)
        val dialogBinding = DialogProgressBarBinding.inflate(LayoutInflater.from(context))
        builder.setView(dialogBinding.root)
        DialogUtils.progressDialog = builder.create()
        DialogUtils.progressDialog!!.setCancelable(false)
        DialogUtils.progressDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        DialogUtils.progressDialog!!.show()
    }


    private fun getPref() {
        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
            currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(this, privilegeResponseModel.currencyFormat)
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

    private fun setAdpater(list : OccupancyNewResponse) {

        if (list.result != null) {
            adapter = NewOccupancyReportAdapter(this, list.result!!)
            binding.listRV.adapter = adapter

        } else {
            toast(getString(R.string.no_data_available))
        }

    }

    private fun setPageAdapter() {
        pageAdapter = PagenationNumberAdapter(this,pagenationList,this)
        binding.pagenationRV.adapter = pageAdapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
//            DownloadPdf.downloadReportPdf(this, bookingData!!.pdfUrl)
        }
    }

    override fun onSingleButtonClick(str: String) {

    }

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

                if(bookingData!!.number_of_pages == lastPos + 1){
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
                hitOccupancyReportApi(lastPos + 1)
            }
        }
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {


    }


}