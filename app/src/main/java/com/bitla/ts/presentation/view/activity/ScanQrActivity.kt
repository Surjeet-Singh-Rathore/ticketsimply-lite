package com.bitla.ts.presentation.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.ActivityScanQrBinding
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import com.bitla.ts.domain.pojo.my_bookings.response.Filter
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONObject
import toast


class ScanQrActivity : BaseActivity(), DialogSingleButtonListener {

    private lateinit var binding: ActivityScanQrBinding
    private var scanQrCrewDetailsList = mutableListOf<Filter>()

    private var driverId: String? = null
    private var driverName: String? = null
    private var driverType: String? = null

    private var origin: String? = ""
    private var destination: String? = ""
    private var busType: String? = ""

    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        if (intent.getStringExtra(getString(R.string.origin)) != null) {
            origin = intent.getStringExtra(getString(R.string.origin))
        }
        if (intent.getStringExtra(getString(R.string.destination)) != null) {
            destination = intent.getStringExtra(getString(R.string.destination))
        }

        if (intent.getStringExtra(getString(R.string.bus_type)) != null) {
            busType = intent.getStringExtra(getString(R.string.bus_type))
        }

        binding.toolbar.tvCurrentHeader.text = "Scan QR Code"

        showScanTypeDialog()
    }

    private fun showScanTypeDialog() {
        val searchList = mutableListOf<SearchModel>()
        val searchModel = SearchModel()
        searchList.add(searchModel)
        searchList.add(searchModel)
        searchList.add(searchModel)
        searchList.add(searchModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        driverId = intent.getStringExtra(getString(R.string.scannedUserId))
        driverName = intent.getStringExtra(getString(R.string.scannedUserName))
        driverType = intent.getStringExtra(getString(R.string.driver_type))

        val searchModel = Filter()
        searchModel.label = getString(R.string.scan_coach)
        searchModel.id = 0

        val searchModel1 = Filter()
        searchModel1.label = getString(R.string.scan_driver_1)
        searchModel1.id = 1

        val searchModel2 = Filter()
        searchModel2.label = getString(R.string.scan_driver_2)
        searchModel2.id = 2
        val searchModel5 = Filter()
        searchModel5.label = getString(R.string.scan_driver_3)
        searchModel5.id = 5

        val searchModel3 = Filter()
        searchModel3.label = getString(R.string.scan_cleaner)
        searchModel3.id = 3

        val searchModel4 = Filter()
        searchModel4.label = getString(R.string.scan_contractor)
        searchModel4.id = 4

        scanQrCrewDetailsList.apply {
            add(searchModel)
            add(searchModel1)
            add(searchModel2)
            add(searchModel3)
            add(searchModel4)
            add(searchModel5)
        }

        DialogUtils.scanQrCodeDialog(
            this,
            scanQrCrewDetailsList,
            getString(R.string.scan_qr_code),
            getString(R.string.scan),
            true,
            this,
            this
        )
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    //    fun scanoption (){
//        val options =  ScanOptions()
//        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
//        options.setPrompt("Scan a barcode")
//        options.setCameraId(0)  // Use a specific camera of the device
//        options.setBeepEnabled(false)
//        options.setBarcodeImageEnabled(true)
//        barcodeLauncher.launch(options)
//
//
//    }
    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.cancel)) {
            finish()
        } else {
            PreferenceUtils.setPreference("selectedScanType", str)

            val integrator = IntentIntegrator(this)
            integrator.setPrompt(str)
            integrator.setCameraId(0) // Use a specific camera of the device
            integrator.setOrientationLocked(false)
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(true)
            integrator.setOrientationLocked(true)
            integrator.initiateScan()
        }
    }


    // Get the results:
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {

            if (result.contents == null) {
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                try {
                    val jsonObj = JSONObject(
                        result.contents
                    )
                    val serviceDetailsActivityIntent =
                        Intent(this, ServiceDetailsActivity::class.java)
                    serviceDetailsActivityIntent.putExtra(getString(R.string.origin), origin)
                    serviceDetailsActivityIntent.putExtra(
                        getString(R.string.destination),
                        destination
                    )
                    serviceDetailsActivityIntent.putExtra(getString(R.string.bus_type), busType)
                    startActivity(serviceDetailsActivityIntent)

                    PreferenceUtils.setPreference(
                        getString(R.string.scannedUserId),
                        jsonObj.getString("id")
                    )
                    PreferenceUtils.setPreference(
                        getString(R.string.scannedUserName),
                        jsonObj.getString("name")
                    )
                    //PreferenceUtils.setPreference(getString(R.string.userType),)
                    finish()
                } catch (t: Throwable) {
                    toast(getString(R.string.scan_valid_qr_code))
                    PreferenceUtils.removeKey("selectedScanType")
                    finish()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}