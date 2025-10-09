package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.annotation.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.graphics.*
import android.net.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.core.content.*
import com.bitla.ts.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.all_reports.*
import com.bitla.ts.domain.pojo.all_reports.all_report_request.*
import com.bitla.ts.domain.pojo.booking.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.edgeToEdgeFromOnlyBottom
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.downloadPdf.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.material.tabs.*
import com.google.gson.*
import org.koin.androidx.viewmodel.ext.android.*
import toast
import visible
import java.io.*
import java.time.*

class BranchCollectionDetailsReportActivity : BaseActivity(), DialogSingleButtonListener {

    private var reqBody: ReqBody? = null
    private var pdfFilename: String = ""
    lateinit var binding: ActivityBranchCollectionDetailsReportBinding
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private val privilegeDetailsViewModel by viewModel<PrivilegeDetailsViewModel>()
    private var locale: String? = ""
    private var bookingData: AllReports? = null
    private var travelDate: String = ""

    override fun initUI() {

        binding = ActivityBranchCollectionDetailsReportBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            edgeToEdge(binding.root)
        }

        if (intent.hasExtra("data")) {
            val data = intent.getStringExtra("data")
            if (!data.isNullOrEmpty()) {
                bookingData = Gson().fromJson(data, AllReports::class.java)
                downloadPdfToShare(bookingData!!.pdf_url)

            }
        }
        
        if(intent.hasExtra("req_data")){
            val data = intent.getStringExtra("req_data")
            reqBody = Gson().fromJson(data, ReqBody::class.java)
        }

        if (intent.hasExtra("travel_date")) {
            travelDate = intent.getStringExtra("travel_date") ?: ""
            binding.updateRatecardToolbar.headerTitleDesc.text = travelDate
        }

        binding.updateRatecardToolbar.imgDownload.visible()
        binding.updateRatecardToolbar.imgShare.visible()
        binding.updateRatecardToolbar.textHeaderTitle.text = getString(R.string.branch_collection_report)

        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        binding.updateRatecardToolbar.imgDownload.setOnClickListener {
            checkDownloadPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        binding.updateRatecardToolbar.imgShare.setOnClickListener {
            if(pdfFilename.isNotEmpty()){
                sharePdfFile(pdfFilename)
            }else{
                toast(getString(R.string.error_occured))
            }
        }
        
        getPref()
        initTab()
        setPrivilegesObserver()
    }
    
    
    override fun isInternetOnCallApisAndInitUI() {
    }
    
    
    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
    }

    @SuppressLint("LogNotTimber")
    private fun initTab() {
        val tabsList: MutableList<Tabs> = mutableListOf()

        val tabFavorits = Tabs()
        tabFavorits.title = getString(R.string.booking)
        tabsList.add(tabFavorits)

        val tabAllReports = Tabs()
        tabAllReports.title = getString(R.string.cancellation)
        tabsList.add(tabAllReports)

        val fragmentAdapter = BranchCollectionViewPagerReportAdapter(
            context = this,
            tabList = tabsList,
            fm = this.supportFragmentManager,
            Gson().toJson(reqBody)
        )
        binding.viewpagerReport.adapter = fragmentAdapter
        binding.tabsBus.setupWithViewPager(binding.viewpagerReport)
        // custom tabs

        for (i in 0..binding.tabsBus.tabCount.minus(3)) {
            val tab = binding.tabsBus.getTabAt(i)!!
            tab.customView = null
            //tab!!.customView = fragmentAdapter.getTabView(i)

            val tabTextView = TextView(this)

            tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            tabTextView.text = tab.text
            tabTextView.setTextColor(Color.BLACK)
            tab.customView = tabTextView

        }

        binding.tabsBus.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewpagerReport.currentItem = tab!!.position
                if (tab.customView != null) {
                    tab.customView as TextView?

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab?.customView != null) {
                    val text: TextView = tab.customView as TextView
                    text.setTextColor(Color.BLACK)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

    fun setData(it:PrivilegeResponseModel){
        PreferenceUtils.setPreference("otp_validation_time",it.configuredLoginValidityTime)

        PreferenceUtils.setPreference(
            "send_qr_code_to_customers_to_authenticate_boarding_status",
            it.sendQrCodeToCustomersToAuthenticateBoardingStatus
        )
        PreferenceUtils.setPreference(
            "send_otp_to_customers_to_authenticate_boarding_status",
            it.sendOtpToCustomersToAuthenticateBoardingStatus
        )
    }

    private fun setPrivilegesObserver() {
        privilegeDetailsViewModel.privilegeResponseModel.observe(this) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                       // PreferenceUtils.putObject(it, PREF_PRIVILEGE_DETAILS)
                        putObjectBase(it,PREF_PRIVILEGE_DETAILS)
                        PreferenceUtils.putObject(LocalDateTime.now(), PREF_PRIVILEGE_DETAILS_CALLED)
                        setData(it)
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
                        toast("${it.result.message}")
                    }
                }
            }
        }
    }


    private fun checkDownloadPermission(permission: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, this)
        if (Build.VERSION.SDK_INT >= 33) {
            DownloadPdf.downloadReportPdf(this, bookingData!!.pdf_url)

        } else {
            if (permissionResult) {
                DownloadPdf.downloadReportPdf(this, bookingData!!.pdf_url)

            } else {
                DownloadPdf.onRequestPermissionsResult(STORAGE_PERMISSION_CODE, permission, this)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            DownloadPdf.downloadReportPdf(this, bookingData!!.pdf_url)
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
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
}