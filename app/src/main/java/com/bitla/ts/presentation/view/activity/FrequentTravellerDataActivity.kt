package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.annotation.*
import android.app.*
import android.bluetooth.*
import android.content.*
import android.content.pm.*
import android.graphics.*
import android.os.*
import android.view.*
import androidx.core.app.*
import androidx.core.content.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.bluetooth_print.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getBccId
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getLogin
import com.bitla.ts.utils.showToast
import com.dantsu.escposprinter.connection.*
import com.dantsu.escposprinter.connection.bluetooth.*
import com.dantsu.escposprinter.textparser.*
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.io.*
import java.net.*


class FrequentTravellerDataActivity : BaseActivity(), DialogSingleButtonListener {

    private var bmpLogo: Bitmap? = null
    private var printTemplate: String = ""
    private lateinit var _sheetFrequentTravellersBinding: SheetFrequentTravellersBinding
    private val sheetFrequentTravellersBinding get() = _sheetFrequentTravellersBinding
    lateinit var context: Context
    private var loginModelPref: LoginModel = LoginModel()
    private lateinit var frequentTravellerDataAdapter: FrequentTravellerDataAdapter
    private var frequentTravellerList = mutableListOf<com.bitla.ts.domain.pojo.frequent_traveller_model.response.Result>()
    var reservationId: Long = 0L
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var selectedDevice: BluetoothConnection? = null

    private var bccId: Int? = 0
    private lateinit var binding: ActivityFrequentTravellerDataBinding

    private var sourceId: String = ""
    private var destinationId: String = ""
    private var source: String? = ""
    private var destination: String? = ""
    private var travelDate: String = ""
    private var busType: String? = null
    private lateinit var apiKey: String
    private var deptTime: String? = null
    private var IsIndia= false

    private var serviceNumber: String = ""
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    private var locale: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@FrequentTravellerDataActivity

        getPref()
        setToolbarTitle()
        binding.includeHeader.apply {
            tvScan.gone()
            printIV.visible()
            layoutLegendLocation.visible()
            imgLegend.gone()
            currentLocation.gone()
            imgBack.setOnClickListener { onBackPressed() }
        }
        
        binding.includeProgress.progressBar.visible()
        callFrequentTravellersDataApi()
        frequentTravellerObserver()
    }

    override fun isInternetOnCallApisAndInitUI() {
    }

    private fun noResultVisibility(binding: ActivityFrequentTravellerDataBinding, frequentTravellerList: MutableList<com.bitla.ts.domain.pojo.frequent_traveller_model.response.Result>) {

        if (frequentTravellerList.size == 0) {
            binding.shimmerLayout.gone()
            binding.NoResult.visible()
            binding.shimmerLayout.gone()
        } else {
            binding.NoResult.gone()
            binding.shimmerLayout.gone()
        }
    }

    private fun getPref() {
        bccId = getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = getLogin()

        val operatorLogo = PreferenceUtils.getPreference(PREF_LOGO, "")

        operatorLogo?.let { getBitmapFromURL(it) }
        apiKey = loginModelPref.api_key
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()

        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            reservationId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!

        if (PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES) != null) {
            val result = PreferenceUtils.getObject<Result>(PREF_SELECTED_AVAILABLE_ROUTES)
            deptTime = result?.dep_time ?: getString(R.string.dash)
            busType = result?.bus_type ?: getString(R.string.empty)
            serviceNumber = result?.number ?: getString(R.string.empty)
        }

        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
            IsIndia= privilegeResponseModel.country.equals("India", true)
        }
    }

    override fun initUI() {
        binding = ActivityFrequentTravellerDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }


        binding.includeHeader.printIV.setOnClickListener {
            setPrintData(frequentTravellerList)
        }
        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                  showToast(it)
                }
            }
        }
    }

    private fun setToolbarTitle() {
        val subtitle = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${getDateDMYY(travelDate)} $deptTime | $busType"
        else
            "${getDateDMYY(travelDate)} $deptTime | $busType"
        binding.includeHeader.toolbarSubtitle.text = subtitle
        binding.includeHeader.toolbarHeaderText.text = getString(R.string.frequent_traveller)
    }

    fun setAdapter(frequentTravellerList: MutableList<com.bitla.ts.domain.pojo.frequent_traveller_model.response.Result>) {
        binding.recyclerServiceView.visible()
        binding.recyclerServiceView.layoutManager = LinearLayoutManager(
            /* context = */ this,
            /* orientation = */ LinearLayoutManager.VERTICAL,
            /* reverseLayout = */ false
        )

        frequentTravellerDataAdapter = FrequentTravellerDataAdapter(
            context = this,
            frequentTravellerData = frequentTravellerList,
        )

        binding.recyclerServiceView.adapter = frequentTravellerDataAdapter
    }

    private fun callFrequentTravellersDataApi() {

        if (isNetworkAvailable()) {
            sharedViewModel.frequentTravellersListApi(apiKey,reservationId.toString(),locale)
        } else {
            noNetworkToast()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun frequentTravellerObserver() {

        sharedViewModel.frequentData.observe(this){

            binding.includeProgress.progressBar.gone()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        frequentTravellerList = it.result

                        if (frequentTravellerList.size > 0) {
                            setAdapter(frequentTravellerList )
                        }

                        noResultVisibility(binding,frequentTravellerList)

//                        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
//                        _sheetFrequentTravellersBinding = SheetFrequentTravellersBinding.inflate(layoutInflater)
//                        bottomSheetDialog.setContentView(sheetFrequentTravellersBinding.root)
//                        bottomSheetDialog.setCancelable(false)
//                        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                        sheetFrequentTravellersBinding.serviceName.text = "${frequentTravellerList.size} Frequent Travellers in ${it.result[0].serviceName}"
//
//                        val subtitle = if (serviceNumber.isNotEmpty())
//                            "$serviceNumber | ${getDateDMYY(travelDate)} $deptTime | $busType"
//                        else
//                            "${getDateDMYY(travelDate)} $deptTime | $busType"
//
//                        sheetFrequentTravellersBinding.ftSubtitle.text = subtitle

//                        sheetFrequentTravellersBinding.btnRight.setOnClickListener {
//                            bottomSheetDialog.dismiss()
//                            finish()
//                        }
//                        bottomSheetDialog.show()
                    }
                    404 -> {
                        it.message.let { it1 -> toast(it1) }
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }
                    else -> it.message.let { it1 -> toast(it1) }
                }
            }
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.toolbar_image_left -> {
                onBackPressed()
            }
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


    private fun setPrintData(data: MutableList<com.bitla.ts.domain.pojo.frequent_traveller_model.response.Result>){

        printTemplate = "IMAGE"+"\n\n[C]<b>Frequent Traveller</b>" +
                "\n\n$serviceNumber | ${getDateDMYY(travelDate)} $deptTime | $busType\n" +
                "\n\n"


        for (i in 0 until data.size){
            var localtemp = "PNR : PNR_NO" +"\nNAME | PHONE_NUM" +"\nSEAT : SEAT_NO" +"\nTRIPS : TRIPS_COUNT\n\n"

            localtemp = localtemp.replace("PNR_NO",data[i].pnrNo)
            localtemp = localtemp.replace("NAME",data[i].passengerName)
            localtemp = localtemp.replace("PHONE_NUM",data[i].mobileNo)
            localtemp = localtemp.replace("SEAT_NO",data[i].seatNo)
            localtemp = localtemp.replace("TRIPS_COUNT",data[i].tripCounts)
            printTemplate += localtemp
        }
        checkPermissions()
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PERMISSION_BLUETOOTH_ADMIN
            )
        } else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    PERMISSION_BLUETOOTH_CONNECT
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                    PERMISSION_BLUETOOTH_SCAN
                )
            } else
                enableDeviceBluetooth()
    }


    @SuppressLint("MissingPermission")
    private fun enableDeviceBluetooth() {
        try {
            val bluetoothManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.getSystemService(BluetoothManager::class.java)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getSystemService(BluetoothManager::class.java)
                } else {
                    ContextCompat.getSystemService(this, BluetoothManager::class.java)
                }
            }
            mBluetoothAdapter = bluetoothManager?.adapter
            if (mBluetoothAdapter != null) {
                if (!mBluetoothAdapter?.isEnabled!!) {
                    val enableBtIntent = Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE
                    )
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                } else {
                    browseBluetoothDevice()
                }
            }
        } catch (e: Exception) {
//            handle
        }
    }

    @SuppressLint("MissingPermission")
    private fun browseBluetoothDevice() {
        try {
            val bluetoothDevicesList = BluetoothPrintersConnections().list
            if (bluetoothDevicesList != null) {
                val items = arrayOfNulls<String>(bluetoothDevicesList.size)
                var i = 0
                for (device in bluetoothDevicesList) {
                    items[i++] = device.device.name
                }

                val savedBluetoothPrinter = PreferenceUtils.getPreference(PREF_BLUETOOTH_DEVICE,"")
                if (items.contains(savedBluetoothPrinter)){
                    Handler().postDelayed(kotlinx.coroutines.Runnable {
                        val savedPrinterIndex =
                            bluetoothDevicesList.indexOfFirst { it.device.name == savedBluetoothPrinter }
                        if (savedPrinterIndex != -1) {
                            selectedDevice = bluetoothDevicesList[savedPrinterIndex]
                            //privilegeResponseModel?.availableAppModes?.allowReprint = true
                         //   if (isFirstPrint)
                                printBluetooth()
                         /*   else {
                                if (::privilegeResponseModel.isInitialized && privilegeResponseModel?.availableAppModes?.allowReprint == true) {
                                    printBluetooth()
                                } else
                                    toast(getString(R.string.not_allowed_to_reprint))
                            }*/
                        }
                    },1000)
                }else {
                    if (items.isNotEmpty()) {
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.setTitle(getString(R.string.bluetooth_printer_selection))
                        alertDialog.setItems(
                            items
                        ) { dialogInterface, i ->
                            val index = i
                            if (index == -1) {
                                selectedDevice = null
                            } else {
                                selectedDevice = bluetoothDevicesList[index]
                                //privilegeResponseModel?.availableAppModes?.allowReprint = true
                                printBluetooth()
                            }
                        }

                        val alert = alertDialog.create()
                        alert.setCanceledOnTouchOutside(true)
                        alert.show()
                    } else
                        toast(getString(R.string.no_paired_devices))
                }
            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    private fun getBitmapFromURL(image: String) {
        try {
            val url = URL(image)
            bmpLogo = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: IOException) {
            println(e)
        }


    }


    private fun printBluetooth() {
        AsyncBluetoothEscPosPrint(
            this,
            object : AsyncEscPosPrint.OnPrintFinished() {
                override fun onError(
                    asyncEscPosPrinter: AsyncEscPosPrinter?,
                    codeException: Int
                ) {
                    Timber.d(
                        "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                    )
                }

                override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                    if (ActivityCompat.checkSelfPermission(
                            this@FrequentTravellerDataActivity,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    PreferenceUtils.setPreference(PREF_BLUETOOTH_DEVICE,selectedDevice?.device?.name)
                    Timber.d(
                        "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                    )
                }
            }
        )
            .execute(this.getAsyncEscPosPrinter(selectedDevice))
    }

    private fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        var hexaDecimalString = ""
        if(bmpLogo != null){
           hexaDecimalString = PrinterTextParserImg.bitmapToHexadecimalString(
                printer,
                bmpLogo
            )

            printTemplate = printTemplate.replace(
                "IMAGE", "[C]<img>${
                    hexaDecimalString
                }</img>"
            ).toString()
        }else{
            printTemplate = printTemplate.replace("IMAGE","")
        }

        return printer.addTextToPrint(
            printTemplate.trim()
        )
    }
}