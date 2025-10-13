package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityVehicleDetailsBinding
import com.bitla.ts.domain.pojo.get_coach_details.request.CoachDetailsRequest
import com.bitla.ts.domain.pojo.get_coach_documents.request.CoachDocumentsRequest
import com.bitla.ts.domain.pojo.get_coach_documents.response.CoachDocumentsResponseItem
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.VehicleDetailsAdapter
import com.bitla.ts.presentation.viewModel.VehicleDetailsViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.jsonToString
import com.bitla.ts.utils.constants.SEARCH_COACH_REQUEST_CODE
import com.bitla.ts.utils.constants.STORAGE_PERMISSION
import com.bitla.ts.utils.constants.get_coach_details
import com.bitla.ts.utils.constants.get_coach_documents
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast

class VehicleDetailsActivity : BaseActivity() {

    private var serviceList: java.util.ArrayList<CoachDocumentsResponseItem> = arrayListOf()
    private var fileUrl: String = ""
    private var coachNumber: String? = ""
    private var coachId: String? = ""
    private lateinit var binding: ActivityVehicleDetailsBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var vehicleDetailsAdapter: VehicleDetailsAdapter
    private val vehicleDetailsViewModel by viewModel<VehicleDetailsViewModel<Any?>>()
    private var loginModelPref: LoginModel = LoginModel()
    private var locale: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    override fun initUI() {
        binding = ActivityVehicleDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        lifecycleScope.launch {
            vehicleDetailsViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                  showToast(it)
                }
            }
        }
        binding.layoutToolbar.imgBack.setOnClickListener {
            super.onBackPressed()
        }
        getPref()
        setUpObserver()
        callCoachDetailsApi()
        binding.layoutToolbar.toolbarHeaderText.text = getString(R.string.vehicle_documents)
        binding.layoutToolbar.imgToolbarSearch.gone()
        setUpAdapter(arrayListOf<CoachDocumentsResponseItem>())
    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        PreferenceUtils.removeKey("COACH_DETAILS_LIST")
    }

    private fun setUpAdapter(mList: ArrayList<CoachDocumentsResponseItem>) {
        layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        vehicleDetailsAdapter =
            VehicleDetailsAdapter(this@VehicleDetailsActivity, mList,
                onViewClick = { position, imageURL ->
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(imageURL)
                    startActivity(i)
                },
                onShareClick = { position, imageURL, actionName ->
                    shareFile(imageURL,actionName)
                },
                onDownloadClick = { position, imageURL ->
                   /* if (isFilePermissionGranted(this)) {
                        downloadFile(this, imageURL)
                    }*/
                    fileUrl = imageURL
                    Timber.d("url", fileUrl)
                    onDownload()

                }
            )
        binding.rvVehicleDetails.layoutManager = layoutManager
        binding.rvVehicleDetails.adapter = vehicleDetailsAdapter
    }
    private fun shareFile(imageURL: String, actionName: String) {

        val text = imageURL + "\n\n" + actionName
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

    }

    private fun onDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val fileName: String = fileUrl.substring(fileUrl.lastIndexOf('/').plus(1))
            downloadFile(fileName,"desc",fileUrl)
        }else{
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
                STORAGE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            STORAGE_PERMISSION -> {
                val fileName: String = fileUrl.substring(fileUrl.lastIndexOf('/').plus(1))
                downloadFile(fileName,"desc",fileUrl)
            }
        }
    }

    private fun downloadFile(fileName : String, desc :String, url : String){
        val request = DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle(fileName)
            .setDescription(desc)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName)
        val downloadManager= getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request)
    }

    private fun callCoachDocumentsApi() {
        val coachDocumentsRequest = CoachDocumentsRequest(
            isFromMiddleTier = true,
            coachNumber = coachNumber,
            apiKey = loginModelPref.api_key,
            operatorApiKey = "Bitla@123",
            locale = locale,
            coachId = coachId
        )

        vehicleDetailsViewModel.getCoachDocumentsApi(coachDocumentsRequest, get_coach_documents)
    }

    private fun callCoachDetailsApi() {
        val coachDetailsRequest = CoachDetailsRequest(
            isFromMiddleTier = true,
            apiKey = loginModelPref.api_key,
            operatorApiKey = "Bitla@123",
            locale = locale
        )

        vehicleDetailsViewModel.getCoachDetailsApi(coachDetailsRequest, get_coach_details)
    }

    private fun setUpObserver() {

        vehicleDetailsViewModel.getCoachDetails.observe(this) { response ->

            Timber.d(jsonToString(response))
            PreferenceUtils.putObject(response, "COACH_DETAILS_RESPONSE")
            binding.apply {
                etSelectAgent.keyListener = null
                etSelectAgent.isFocusable = false
                etSelectAgent.isFocusableInTouchMode = false
                etSelectAgent.setOnClickListener {
                    val intent = Intent(this@VehicleDetailsActivity, SearchCoachActivity::class.java)
                    startActivityForResult(intent, SEARCH_COACH_REQUEST_CODE)

                }
                subHeaderLabel.setEndIconOnClickListener {
                    val intent = Intent(this@VehicleDetailsActivity, SearchCoachActivity::class.java)
                    startActivityForResult(intent, SEARCH_COACH_REQUEST_CODE)
                }
            }
        }

        vehicleDetailsViewModel.getCoachDocuments.observe(this) {
            if(it.code == 200) {
                if (it.result != null) {
                    setUpAdapter(it.result)
                }
            }else{
                if(!it.message.isNullOrEmpty()){
                    toast(it.message)

                }else{
                    toast(getString(R.string.error_occur_please_try_again))

                }
            }

        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (resultCode == SEARCH_COACH_REQUEST_CODE) {
                coachId = data.extras!!["coach_id"].toString()
                coachNumber = data.extras!!["coach_number"].toString()
                binding.etSelectAgent.setText(coachNumber)
                if (!coachId.isNullOrEmpty() && !coachNumber.isNullOrEmpty()) {
                    callCoachDocumentsApi()
                }
            }
        }
    }
}