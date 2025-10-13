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
import com.bitla.ts.domain.pojo.all_reports.new_response.*
import com.bitla.ts.domain.pojo.all_reports.new_response.occupany_report_data.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.ticket_details_compose.TicketDetailsActivityCompose
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


class BookedByYouReportActivity : BaseActivity(),DialogButtonAnyDataListener,
    DialogSingleButtonListener, OnPnrListener {
    private var lastPos: Int = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = ""
    private var reqBody: ReqBody ?= null
    private var apiKey: String? = null
    private var travelDate: String = ""
    private var pdfFilename: String = ""
    private var noOfPages = 10
    private var bookingData: BookedByYouNewResponse? = null
    private lateinit var binding: ActivityBookedByYouReportBinding
    private var adapter: BookByYouReportAdapter? = null
    private var currency: String = ""
    private var currencyFormat: String = ""
    private var privilegeResponseModel: PrivilegeResponseModel?= null
    private var pageAdapter: PagenationNumberAdapter?= null
    private var pagenationList : ArrayList<PagenationData> = arrayListOf()
    private val allReportsViewModel by viewModel<AllReportsViewModel<Any?>>()
    private var isPaymentReportData=false
    
    override fun isInternetOnCallApisAndInitUI() {}

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityBookedByYouReportBinding.inflate(layoutInflater)
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
        setTicketBookedByYouNewObserver()
        setPaymentStatusObserver()
        if(privilegeResponseModel?.country.equals("India",true)){
            binding.updateRatecardToolbar.imgDownload.visible()
            binding.updateRatecardToolbar.imgShare.visible()
        }else{
            binding.updateRatecardToolbar.imgDownload.visible()
            binding.updateRatecardToolbar.imgDownload.setColorFilter(resources.getColor(R.color.colorPrimaryDark))

            binding.updateRatecardToolbar.imgShare.gone()
        }

        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.booked_by_you_report)

        if (intent.hasExtra(R.string.data_ticket_boked_by_you.toString())) {
            val data = intent.getStringExtra(R.string.data_ticket_boked_by_you.toString())
            if (!data.isNullOrEmpty()) {
                bookingData = Gson().fromJson(data, BookedByYouNewResponse::class.java)

                var pdfStrLink: String =bookingData?.pdfUrl?:""
                PreferenceUtils.putString(PREF_PDF_DOWNLOAD_LINK,pdfStrLink)

                downloadPdfToShare(bookingData?.pdfUrl?:"")
                pagenationList.clear()
                var nosOfpage:Int =0
                nosOfpage = bookingData?.number_of_pages?:noOfPages
                for (i in 0 until nosOfpage){
                    val obj = PagenationData()
                    obj.position = i
                    obj.isSelected = false
                    pagenationList.add(obj)
                }
            }
        }
        if (intent.hasExtra( R.string.travel_date.toString())) {
            travelDate = intent.getStringExtra(R.string.travel_date.toString()) ?: ""
            binding.updateRatecardToolbar.headerTitleDesc.text = travelDate
        }

        if(intent.hasExtra(R.string.request_data_ticket_booked_by_you.toString())){
            val data = intent.getStringExtra(R.string.request_data_ticket_booked_by_you.toString())
            reqBody = Gson().fromJson(data, ReqBody::class.java)
        }

        if(intent.hasExtra("isPaymentStatusReport")){
            isPaymentReportData= intent.extras?.getBoolean("isPaymentStatusReport",false)!!
        }

        if (bookingData != null) {
            setAdpater(bookingData)
            if(privilegeResponseModel?.country.equals("India",true))
            {
                binding.totalSeatsValueTV.text = bookingData?.result?.totalSeats.toString()
                // binding.totalAmountValueTV.text = currency + bookingData!!.result.totalAmount.toDouble().convert(currencyFormat)
                binding.totalAmountValueTV.text = " " +bookingData?.result?.totalAmount
                binding.commisionText.gone()
                binding.commisionTextValue.gone()
                binding.netAmtText.gone()
                binding.netAmtTextValue.gone()
                binding.discountText.gone()
                binding.discountTextValue.gone()
               // binding.pagenationRV.gone()
            }
            else{
               // binding.pagenationRV.visible()
                if(privilegeResponseModel?.isAgentLogin == false) {
                    binding.discountText.gone()
                    binding.discountTextValue.gone()
                }

                binding.commisionText.visible()
                binding.commisionTextValue.visible()
                binding.netAmtText.visible()
                binding.netAmtTextValue.visible()
                binding.totalSeatsTV.setText(R.string.total_seats_colon_new)
                binding.totalSeatsValueTV.text = bookingData?.total_items.toString()


                if(isPaymentReportData){
                    binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.payment_status_report)
                    if(intent.hasExtra("payment_type")){
                        val paymentType = intent.getStringExtra("payment_type")
                        travelDate += paymentType
                        binding.updateRatecardToolbar.headerTitleDesc.text = travelDate
                        binding.amountCL.gone()
                        binding.paymentAmountCL.visible()
                        binding.totalTransactionValueTV.text=
                            (bookingData?.result?.totalTransaction.toString()?:"Rp0")

                        binding.totalPaidValueTV.text=
                        " $currency " + bookingData?.result?.totalPaidAmount?.convert(currencyFormat)

                        binding.totalUnPaidValueTV.text=
                            " $currency " + bookingData?.result?.totalUnpaidAmount?.convert(currencyFormat)
                    }

                }else {

                    binding.totalAmountValueTV.text =
                        " " + currency + " " + bookingData?.result?.totalAmount?.toDouble()?.convert(currencyFormat)
                    binding.commisionTextValue.text =
                        " " + currency + " " + bookingData?.result?.commission?.toDouble()?.convert(currencyFormat)
                    binding.netAmtTextValue.text =
                        " " + currency + " " + bookingData?.result?.netAmount?.toDouble()?.convert(currencyFormat)
                    binding.discountTextValue.text = " " + bookingData?.result?.totalDiscount
                }
            }

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

        if(pagenationList.size > 1){
            binding.bottomV.visible()
            pagenationList[0].isSelected  = true
            setPageAdapter()
        }else{
            binding.bottomV.gone()
        }

        binding.prevPageBT.setOnClickListener{
            if(lastPos + 1 != 1){
                pageAdapter?.changeItemPosition(lastPos,lastPos-1)

            }
        }

        binding.nextButtonBT.setOnClickListener {
            if(bookingData?.number_of_pages!! > lastPos + 1){
                pageAdapter?.changeItemPosition(lastPos,lastPos+1)

            }
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
            request.setTitle("Downloading PDF")
            request.setDescription("Downloading PDF file...")
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



        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase()
            currency = privilegeResponseModel?.currency?:""
            currencyFormat = getCurrencyFormat(this, privilegeResponseModel?.currencyFormat)
        }
    }


    private fun checkDownloadPermission(permission: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, this)
        if (Build.VERSION.SDK_INT >= 33) {
            var pdfLink = PreferenceUtils.getString(PREF_PDF_DOWNLOAD_LINK).toString()
           DownloadPdf.downloadReportPdf(this, pdfLink)
        } else {
            if (permissionResult) {
                var pdfLink = PreferenceUtils.getString(PREF_PDF_DOWNLOAD_LINK).toString()
               DownloadPdf.downloadReportPdf(this, pdfLink)

            } else {
                DownloadPdf.onRequestPermissionsResult(STORAGE_PERMISSION_CODE, permission, this)
            }
        }

    }



    private fun setAdpater(list : BookedByYouNewResponse?) {
        if (list?.result?.data != null) {
            if(isPaymentReportData){
                adapter = BookByYouReportAdapter(
                    this,
                    list.result.data,
                    this,
                    true,
                    privilegeResponseModel
                )
            }else{
                adapter = BookByYouReportAdapter(this, list.result.data,this,false,privilegeResponseModel)
            }

            binding.listRV.adapter = adapter
        }
        else {
            toast(getString(R.string.no_data_available))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            DownloadPdf.downloadReportPdf(this, bookingData?.pdfUrl)
        }
    }
    private fun setPageAdapter() {

        pageAdapter = PagenationNumberAdapter(this,pagenationList,this)
        binding.pagenationRV.adapter = pageAdapter


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

                if(bookingData?.number_of_pages == lastPos + 1){
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
                if(isPaymentReportData){
                    hitPaymentStatusReportApi(lastPos+1)
                }
                else
                        {
                            hitTicketBookedByYouReportApi(lastPos + 1)
                        }
            }
        }
    }

    override fun onDataSendWithExtraParam(type: Int, file: Any, extra: Any) {


    }

    /* override fun onDataSend(type: Int, file: Any) {
         when(type) {
             1 -> {
                 val lastPos = file as Int
                 showProgressDialog(this)
                 for (i in 0 until pagenationList.size){
                     pagenationList[i].isSelected = false
                 }
                 pagenationList[lastPos].isSelected = true
                 pageAdapter?.notifyDataSetChanged()
                 hitTicketBookedByYouReportApi(lastPos + 1)
             }
         }
     }*/
    private fun hitTicketBookedByYouReportApi(pageCount : Int){
        reqBody?.page = pageCount
        allReportsViewModel.ticketBookedByYouNewApi(
            apiKey = apiKey ?: "",
            locale = locale ?: "en",
            allReportsRequest = reqBody!!,
            apiType = alloted_Service_method_name
        )

    }


    private fun hitPaymentStatusReportApi(pageCount : Int){
        reqBody?.page = pageCount
        allReportsViewModel.paymentStatusReportApi(
            apiKey = apiKey ?: "",
            locale = locale ?: "en",
            allReportsRequest = reqBody!!,
            apiType = alloted_Service_method_name
        )

    }



    fun showProgressDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.Style_Dialog_Rounded_littl_Corner)
        val dialogBinding = DialogProgressBarBinding.inflate(LayoutInflater.from(context))
        builder.setView(dialogBinding.root)
        DialogUtils.progressDialog = builder.create()
        DialogUtils.progressDialog?.setCancelable(false)
        DialogUtils.progressDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        DialogUtils.progressDialog?.show()
    }
    private fun dismissProgressDialog() {
        if (DialogUtils.progressDialog != null && DialogUtils.progressDialog?.isShowing==true) {
            DialogUtils.progressDialog?.dismiss()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setTicketBookedByYouNewObserver() {
        allReportsViewModel.ticketBookedByYouNewResp.observe(this) { it ->
            Timber.d("allReports - TicketBookedByYou ->> $it")
         //   binding.progressPB.root.gone()
           dismissProgressDialog()

            if (it != null) {

                when (it.code) {
                    200 -> {
                        setAdpater(it)

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



    @SuppressLint("SetTextI18n")
    private fun setPaymentStatusObserver() {
        allReportsViewModel.paymentStatusReportResp.observe(this) { it ->
            Timber.d("allReports - PaymeentStatusReport ->> $it")
            //   binding.progressPB.root.gone()
            dismissProgressDialog()

            if (it != null) {

                when (it.code) {
                    200 -> {
                        setAdpater(it)

                    }

                    401 -> {
                        DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )
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

    override fun onSingleButtonClick(str: String) {

    }

    override fun onPnrSelection(tag: String, pnr: Any, doj: Any?) {
        when (tag) {
            getString(R.string.view_ticket) -> {
//                val intent = Intent(this, TicketDetailsActivity::class.java)
                val intent = Intent(this, TicketDetailsActivityCompose::class.java)
                intent.putExtra(getString(R.string.TICKET_NUMBER), pnr.toString())
                intent.putExtra("returnToDashboard", false)
                startActivity(intent)

            }
        }
    }
}