package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.Gravity
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.databinding.ActivityRoutewiseBookingMemoBinding
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.routewise_booking_memo.RouteWiseResponse
import com.bitla.ts.presentation.adapter.RouteWiseBookingMemoAdapter
import com.bitla.ts.utils.bluetooth_print.AsyncBluetoothEscPosPrint
import com.bitla.ts.utils.bluetooth_print.AsyncEscPosPrint
import com.bitla.ts.utils.bluetooth_print.AsyncEscPosPrinter
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getCurrencyFormat
import com.bitla.ts.utils.constants.PERMISSION_BLUETOOTH
import com.bitla.ts.utils.constants.PERMISSION_BLUETOOTH_ADMIN
import com.bitla.ts.utils.constants.PERMISSION_BLUETOOTH_CONNECT
import com.bitla.ts.utils.constants.PERMISSION_BLUETOOTH_SCAN
import com.bitla.ts.utils.constants.REQUEST_ENABLE_BT
import com.bitla.ts.utils.constants.STORAGE_PERMISSION_CODE
import com.bitla.ts.utils.downloadPdf.DownloadPdf
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.google.gson.Gson
import timber.log.Timber
import toast
import visible
import java.io.File


class RoutewiseBookingMemoActivity : BaseActivity() {

    private var travelDate: String = ""
    private var pdfFilename: String = ""
    private var bookingData: RouteWiseResponse? = null
    private var busType: String = ""
    private var template: String = ""
    private lateinit var binding: ActivityRoutewiseBookingMemoBinding
    private var selectedDevice: BluetoothConnection? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var adapter : RouteWiseBookingMemoAdapter?= null
    private var currency: String = ""
    private var currencyFormat: String = ""
    private lateinit var privilegeResponseModel: PrivilegeResponseModel
    @RequiresApi(Build.VERSION_CODES.Q)

    @SuppressLint("SetTextI18n")
    override fun initUI() {
        binding = ActivityRoutewiseBookingMemoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()

        binding.updateRatecardToolbar.imageHeaderPrint.visible()
        binding.updateRatecardToolbar.busEta.visible()
        binding.updateRatecardToolbar.busEta.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_dots_grey))



        binding.updateRatecardToolbar.textHeaderTitle.text =
            getString(R.string.routewise_booking_memo)


        if(intent.hasExtra("data")){
            val data = intent.getStringExtra("data")
            if(!data.isNullOrEmpty()){
                bookingData = Gson().fromJson<RouteWiseResponse>(data,RouteWiseResponse::class.java)

            }
        }
        if(intent.hasExtra("travel_date")){
            travelDate = intent.getStringExtra("travel_date")?:""
            binding.updateRatecardToolbar.headerTitleDesc.text = travelDate


        }

        if(bookingData != null){
            setAdpater()
            downloadPdfToShare(bookingData!!.pdfUrl)
            binding.totalAmountValueTV.text = currency + bookingData!!.totalAmount.toDouble().convert(currencyFormat)
            binding.totalSeatsValueTV.text = bookingData!!.totalSeatCount
        }

        binding.updateRatecardToolbar.imageHeaderPrint.setOnClickListener {
            if(bookingData != null){
                printTicket(bookingData!!)
            }
        }

        binding.updateRatecardToolbar.busEta.setOnClickListener {
            showPopupMenu()
        }

        binding.updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showPopupMenu() {

        val popup = PopupMenu(this, binding.updateRatecardToolbar.busEta)
        popup.inflate(R.menu.custom_menu)
        popup.gravity = Gravity.RIGHT
        if(Build.VERSION.SDK_INT > 28){
            popup.setForceShowIcon(true)
        }
        popup.menu.getItem(0).title = getString(R.string.download)
        popup.menu.getItem(1).title = getString(R.string.share)
        popup.menu.getItem(1).icon = AppCompatResources.getDrawable(this,R.drawable.ic_share)
        popup.menu.getItem(2).setVisible(false)


        // Set a listener for menu item clicks
        popup.setOnMenuItemClickListener { item -> onMenuItemClick(item) }
        // Show the PopupMenu
        popup.show()
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.downloadMI -> {
                checkDownloadPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)


            }
            R.id.viewTicketMI -> {
                if(pdfFilename.isNotEmpty()){
                    sharePdfFile(pdfFilename)
                }else{
                    toast(getString(R.string.error_occured))
                }
            }


        }
        return false
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
        if(Build.VERSION.SDK_INT >= 33){
            DownloadPdf.downloadReportPdf(this, bookingData!!.pdfUrl)

        }else{
            if (permissionResult) {
                DownloadPdf.downloadReportPdf(this, bookingData!!.pdfUrl)

            } else {
                DownloadPdf.onRequestPermissionsResult(STORAGE_PERMISSION_CODE, permission, this)
            }
        }

    }

    private fun getPref() {
        if (getPrivilegeBase() != null) {
            privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel
            currency = privilegeResponseModel.currency
            currencyFormat = getCurrencyFormat(this, privilegeResponseModel.currencyFormat)
        }
    }


    private fun setAdpater() {
        adapter = RouteWiseBookingMemoAdapter(this,bookingData!!.report,privilegeResponseModel)
        binding.listRV.adapter = adapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE){
            DownloadPdf.downloadReportPdf(this, bookingData!!.pdfUrl)

        }
        if(requestCode == PERMISSION_BLUETOOTH_SCAN){
            enableDeviceBluetooth()
        }
        else{
           checkPermissions()
        }

    }

    private fun printTicket(data : RouteWiseResponse) {

        val printData = data

        template = "Routewise Booking Memo\n" +
                "OPERATOR_NAME\n" +
                "USER_BRANCH_ADDRESS\n" +
                "REPORT_NAME\n" +
                "Bus Number - BUS_NUMBER\n" +
                "From ORIGIN To DEST\n" +
                "Travel Date - TRAVEL_DATE\n" +
                "Travel Time - DEPT_TIME\n" +
                "--------------------------------\n"+
                "FROM | TO | RATE | SEAT NO. | TOTAL SEAT | AMOUNT\n"


        template = template.replace("OPERATOR_NAME",printData.operatorName)
        template = template.replace("USER_BRANCH_ADDRESS",printData.userBranchAddress)
        template = template.replace("REPORT_NAME",printData.reportName)
        template = template.replace("BUS_NUMBER",printData.busNumber)
        template = template.replace("ORIGIN",printData.origin)
        template = template.replace("DEST",printData.destination)
        template = template.replace("TRAVEL_DATE",travelDate)
        template = template.replace("DEPT_TIME",printData.departureTime)
        template += makeRouteList(bookingData!!)
        template += lastAmountDetails()
        checkPermissions()

    }

    private fun lastAmountDetails(): String {
        var temp  = "\n\nTotal Seats :- TOTAL_SEATS\n" +
                "Total Amount :- TOTAL_NET_AMOUNT\n\n" +
                "By :- PRINTED_BY\n" +
                "--------------------------------\n" +
                "PRINTED_TIME\n"
        temp = temp.replace("TOTAL_SEATS",bookingData!!.totalSeatCount)
        temp = temp.replace("TOTAL_NET_AMOUNT",currency +bookingData!!.totalAmount.toDouble().convert(currencyFormat))
        temp = temp.replace("PRINTED_BY",bookingData!!.printedBy)
        temp = temp.replace("PRINTED_TIME",bookingData!!.printDateTime)

        return temp


    }

    fun makeRouteList(data: RouteWiseResponse) : String{
        val printData = data
        var finalString = ""
        for (i in 0 until printData.report.size){
            var temp = "FROM_VAL | TO_VAL| RATE_VAL | SEAT_NO_VAL | TOTAL_SEAT_VAL | TOTAL_AMOUNT"
            temp = temp.replace("FROM_VAL", printData.report[i].from)
            temp = temp.replace("TO_VAL",printData.report[i].to)
            temp = temp.replace("RATE_VAL",currency + printData.report[i].rate)
            temp = temp.replace("SEAT_NO_VAL",printData.report[i].seatNumber)
            temp = temp.replace("TOTAL_SEAT_VAL",printData.report[i].noOfSeats)
            temp = temp.replace("TOTAL_AMOUNT",currency + printData.report[i].amount.toDouble().convert(currencyFormat))
            finalString = finalString+"\n"+temp
        }

        return finalString
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
                    BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(BLUETOOTH_SCAN),
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
                    TODO("VERSION.SDK_INT < M")
                }
            }
            mBluetoothAdapter = bluetoothManager.adapter
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
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
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
                        "Async.OnPrintFinished",
                        "AsyncEscPosPrint.OnPrintFinished : An error occurred !"
                    )
                }

                override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                    Timber.d(
                        "Async.OnPrintFinished",
                        "AsyncEscPosPrint.OnPrintFinished : Print is finished !"
                    )
                }
            }
        )
            .execute(this.getAsyncEscPosPrinter(selectedDevice))
    }

    private fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)

        return printer.addTextToPrint(
            template.trim()
        )
    }
    
    
    override fun isInternetOnCallApisAndInitUI() {


    }



}