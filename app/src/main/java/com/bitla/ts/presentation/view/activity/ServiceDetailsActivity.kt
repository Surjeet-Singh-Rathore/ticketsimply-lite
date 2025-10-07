package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.content.*
import android.os.*
import android.view.*
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bitla.ts.*
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.my_bookings.response.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.view.fragments.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.google.zxing.integration.android.*
import gone
import org.json.*
import toast


class ServiceDetailsActivity : BaseActivity(), DialogSingleButtonListener {

    companion object {
        val tag: String = ServiceDetailsActivity::class.java.simpleName
    }

    private var origin: String? = ""
    private var destination: String? = ""
    private var busType: String? = ""
    private var isFromChile: Boolean = false

    private lateinit var binding: ActivityServiceDetailsBinding
    private var privilegeResponseModel: PrivilegeResponseModel? = null


    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityServiceDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        binding.toolbar.tvScan.gone()

        if (intent.getStringExtra(getString(R.string.origin)) != null) {
            origin = intent.getStringExtra(getString(R.string.origin))
        }
        if (intent.getStringExtra(getString(R.string.destination)) != null) {
            destination = intent.getStringExtra(getString(R.string.destination))
        }

        if (intent.getStringExtra(getString(R.string.bus_type)) != null) {
            busType = intent.getStringExtra(getString(R.string.bus_type))
        }

         privilegeResponseModel = getPrivilegeBase()
        if (privilegeResponseModel!= null) {
            privilegeResponseModel?.apply {
                if (isChileApp!=null){
                    isFromChile = isChileApp
                }

            }
        }

        binding.toolbar.tvCurrentHeader.gone()
//        binding.toolbar.tvCurrentHeader.text = "$origin - $destination"
        binding.toolbar.toolbarHeaderText.text = getString(R.string.service_details)
        binding.toolbar.toolbarSubtitle.text = busType


        PreferenceUtils.putString("serviceSubToolbar", busType)

        binding.toolbar.imgBack.setOnClickListener {
            onBackPressed()
        }

        setTabs(
            binding.tabs,
            binding.viewpager,
            if (privilegeResponseModel?.allowToShowNewFlowInTsApp == true) CrewDetailsFragmentNewFlow() else CrewDetailsFragment(),
            if (privilegeResponseModel?.allowToShowNewFlowInTsApp == true) ExpenseDetailsNewFlowFragment() else ExpenseDetailsFragment(),
            getString(
                R.string.crew_details
            ),
            getString(
                R.string.expense_details
            )
        )


        binding.viewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
//                    0 -> binding.toolbar.tvScan.visible()
//                    1 -> binding.toolbar.tvScan.gone()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        clickListener()

    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun clickListener() {
        binding.toolbar.tvScan.setOnClickListener(this)
        binding.toolbar.imgBack.setOnClickListener {
            PreferenceUtils.removeKey(getString(R.string.scannedUserId))
            PreferenceUtils.removeKey(getString(R.string.scannedUserName))
            onBackPressed()
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.tvScan -> {
                val serviceDetailsActivityIntent = Intent(this, ScanQrActivity::class.java)
                serviceDetailsActivityIntent.putExtra(getString(R.string.origin), origin)
                serviceDetailsActivityIntent.putExtra(getString(R.string.destination), destination)
                serviceDetailsActivityIntent.putExtra(getString(R.string.bus_type), busType)
                scanQrCode()
                //startActivity(serviceDetailsActivityIntent)
                //finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        PreferenceUtils.removeKey(getString(R.string.scannedUserId))
        PreferenceUtils.removeKey(getString(R.string.scannedUserName))
    }

    private fun scanQrCode() {
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
        if (isFromChile){
            searchModel5.label = getString(R.string.scan_driver_3)
            searchModel5.id = 5
        }
        val searchModel3 = Filter()
        searchModel3.label = getString(R.string.scan_cleaner)
        searchModel3.id = 3

        val searchModel4 = Filter()
        searchModel4.label = getString(R.string.scan_contractor)
        searchModel4.id = 4
        var scanQrCrewDetailsList = mutableListOf<Filter>()

        scanQrCrewDetailsList.apply {
            add(searchModel)
            add(searchModel1)
            add(searchModel2)
            if (isFromChile){
                add(searchModel5)
            }
            add(searchModel3)
            add(searchModel4)
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

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.cancel)) {

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
                    //finish()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}