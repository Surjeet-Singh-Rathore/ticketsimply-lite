package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.*
import android.os.*
import android.view.ScaleGestureDetector
import androidx.activity.result.contract.*
import androidx.annotation.*
import androidx.appcompat.content.res.*
import androidx.core.app.ActivityCompat
import androidx.core.content.*
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.downloadPdf.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible
import java.io.*
import java.net.*
import java.nio.channels.*


class ViewPdfActivity : BaseActivity(), DialogSingleButtonListener {

    companion object {
        val TAG = ViewPdfActivity::class.java.simpleName
    }

    private var pdfFilename: String = ""
    private var pdfFile: File? = null
    private var pdfUrl: String = ""
    private var resID: String = ""
    private var serviceData = ""
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var pageIndex = 0

    // Pinch-to-zoom variables


    private var auditType: String = ""
    private lateinit var binding: ActivityViewPdfBinding
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private var locale: String? = ""
    private var loginModelPref: LoginModel? = null
    private var travelDate: String = ""
    private var isDownload = false
    private val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                downloadAndDisplayPdf(pdfUrl)
            } else {
                // Permission denied, handle the situation accordingly
                toast(getString(R.string.permission_denied))
            }
        }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun initUI() {
        binding = ActivityViewPdfBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.previousBT.alpha = 0.2f




        val scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                binding.rootCL.apply {
                    scaleX *= scaleFactor
                    scaleY *= scaleFactor
                }
                return true
            }
        })

        binding.nextBT.setOnClickListener {
            if (pageIndex < pdfRenderer!!.pageCount - 1) {
                pageIndex++
                showPage(pageIndex)

            }
            if(pdfRenderer!!.pageCount == pageIndex+1){
                binding.nextBT.alpha = 0.2f
            }else{
                binding.nextBT.alpha = 1.0f

            }
            if(pageIndex+1 == 1){
                binding.previousBT.alpha = 0.2f
            }else{
                binding.previousBT.alpha = 1.0f

            }
        }

        binding.previousBT.setOnClickListener {
            if (pageIndex > 0) {
                pageIndex--
                showPage(pageIndex)
            }
            if(pageIndex+1 == 1){
                binding.previousBT.alpha = 0.2f
            }else{
                binding.previousBT.alpha = 1.0f

            }
            if(pdfRenderer!!.pageCount == pageIndex+1){
                binding.nextBT.alpha = 0.2f
            }else{
                binding.nextBT.alpha = 1.0f

            }
        }

        binding.rootCL.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()
        pickUpchartPDFObserver()
        pickUpchartPDFapi()
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentPage?.close()
        pdfRenderer?.close()
    }



    // This method ensures the image stays within the bounds of the ImageView



    private fun startDownload(url: String) {
        var fileName = url.substringAfterLast("/")
        if (!fileName.contains(".pdf")) {
            fileName += ".pdf"
        }

        val downloader =
            FileDownloader(cacheDir, object : FileDownloader.OnDownloadCompleteListener {
                override fun onDownloadComplete(file: File?) {
                    if (file != null) {
                        displayPDF(file)
                        pdfFile = file
                    } else {
                        toast(getString(R.string.error_loading_pdf_please_try_again))
                    }
                }
            })

        downloader.execute(url, fileName)
    }

    class FileDownloader(
        private val cacheDir: File,
        private val listener: OnDownloadCompleteListener? = null
    ) : AsyncTask<String, Int, File>() {

        interface OnDownloadCompleteListener {
            fun onDownloadComplete(file: File?)
        }

        override fun doInBackground(vararg params: String): File? {
            val fileUrl = params[0]
            val fileName = params[1]
            var file: File? = null

            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                // Check if the response code indicates success
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val fileLength = connection.contentLength
                    val inputStream: InputStream = connection.inputStream

                    // Create a file in the app's internal storage
                    /*  val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
                      if (!directory.exists()) {
                          directory.mkdirs()
                      }*/

                    val directory = File(cacheDir, "downloads")
                    if (!directory.exists()) {
                        directory.mkdirs()
                    }

                    file = File(directory, fileName)
                    val outputStream = FileOutputStream(file)

                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    var totalBytesRead: Long = 0

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        // Update progress if needed
                        // publishProgress((totalBytesRead * 100 / fileLength).toInt())
                    }

                    outputStream.close()
                    inputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return file
        }

        override fun onPostExecute(file: File?) {
            super.onPostExecute(file)
            listener?.onDownloadComplete(file)
        }
    }

    private fun getDownloadFolderPath(): String? {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            val downloadDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            return downloadDir.toURI().path
        }
        return ""
    }

    private fun downloadAndDisplayPdf(url: String) {
        var filename = url.substringAfterLast("/")
        if (!filename.contains(".pdf")) {
            filename += ".pdf"
        }
        val storagePath = getDownloadFolderPath()
        val path = "${storagePath}/${filename}"
        //val path = "/sdcard/Download/${filename}"
        downloadPdfFromUrl(url, path)
        val file = File(path)
        displayPDF(file)
    }


    private fun downloadPdfFromUrl(url: String, destinationPath: String) {
        try {
            val connection = URL(url).openConnection()
            val inputStream = connection.getInputStream()
            val outputStream = FileOutputStream(destinationPath)
            val readableByteChannel = Channels.newChannel(inputStream)
            outputStream.channel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
            outputStream.close()
            inputStream.close()

        } catch (e: Exception) {
            Timber.e("Error while downloading PDF: ${e.message}")
        }

    }


    fun writeData() {

    }

    private fun requestWritePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1004) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadAndDisplayPdf(pdfUrl)
            } else {
                // The user denied the permission. Handle this situation as needed.
                toast(getString(R.string.please_grant_storage_permission_to_view_pdf))
            }
        } else if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadPdfToShare(pdfUrl)
            } else {
                toast(getString(R.string.please_grant_permission_to_view_and_share_pdf))
                requestWritePermission()
            }
        }
    }

    private fun displayPDF(url: File) {
        // Load the PDF from the provided URL
        binding.dialogProgressBar.gone()
        if (url.exists()) {
            try {
                val pfd: ParcelFileDescriptor = ParcelFileDescriptor.open(url, ParcelFileDescriptor.MODE_READ_ONLY)
                pdfRenderer = PdfRenderer(pfd)

                // Only display the first page (index 0)
                if (pdfRenderer!!.pageCount > 0) {
                    showPage(pageIndex)

                    if(pdfRenderer!!.pageCount > 1){
                        binding.nextBT.visible()
                        binding.previousBT.visible()
                    }else{
                        binding.nextBT.gone()
                        binding.previousBT.gone()
                    }
                } else {
                    toast(getString(R.string.pdf_has_no_pages))
                }
            } catch (e: IOException) {
                e.printStackTrace()
                toast(getString(R.string.error_loading_pdf_please_try_again))
            }
        } else {
            toast(getString(R.string.pdf_file_not_found))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("service_data")) {
            serviceData = intent.getStringExtra("service_data") ?: ""
        }
        
        binding.apply {
            updateRatecardToolbar.imgDownload.visible()
            updateRatecardToolbar.busEta.visible()
            updateRatecardToolbar.busEta.setImageDrawable(
                AppCompatResources.getDrawable(
                    this@ViewPdfActivity, R.drawable.ic_share_grey_3
                )
            )
            updateRatecardToolbar.imgDownload.setImageDrawable(
                AppCompatResources.getDrawable(
                    this@ViewPdfActivity, R.drawable.download_grey_3
                )
            )
            updateRatecardToolbar.headerTitleDesc.text = serviceData
            updateRatecardToolbar.textHeaderTitle.text = getString(R.string.view_pickup_chart_pdf)
            updateRatecardToolbar.toolbarImageLeft.setOnClickListener {
                onBackPressed()
            }
            
            updateRatecardToolbar.imgDownload.setOnClickListener {
                toast(getString(R.string.start_downloading))
                isDownload = true
                auditType = getString(R.string.download_pickup_chart_text)
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                
            }
            
            updateRatecardToolbar.busEta.setOnClickListener {
                auditType = getString(R.string.share_pickup_chart_text)
                pickUpchartPDFapi()
                sharePdfFile(pdfFilename)
            }
        }
    }


    private fun checkPermission(permission: String) {
        val permissionResult = DownloadPdf.checkPermission(permission, this)
        if (Build.VERSION.SDK_INT >= 33) {
            pickUpchartPDFapi()
        } else {
            if (permissionResult) {
                pickUpchartPDFapi()
            } else {
                DownloadPdf.onRequestPermissionsResult(STORAGE_PERMISSION_CODE, permission, this)
            }
        }

    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()
        travelDate = PreferenceUtils.getString("ViewReservation_date") ?: ""
//        resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)
        resID = PreferenceUtils.getString("reservationid") ?: ""

    }

    private fun pickUpchartPDFapi() {

        if (isNetworkAvailable()) {

            pickUpChartViewModel.pickUpChartPdfAPI(
                com.bitla.ts.domain.pojo.pickup_chart_pdf_url.request.ReqBody(
                    api_key = loginModelPref?.api_key ?: "",
                    res_id = resID,
                    travel_date = travelDate,
                    locale = locale,
                    audit_type = auditType
                ), bulk_cancellation_method_name
            )
        } else noNetworkToast()
    }


    private fun pickUpchartPDFObserver() {
        pickUpChartViewModel.pickUpChatPdfResponse.observe(this) {
            Timber.d("viewReservation $it")
            if (it != null) {
                if (it.status == 200) {

                    if (isDownload) {
                        DownloadPdf.downloadReportPdf(this, it.pdf_url)
                        isDownload = false
                    } else {
                        pdfUrl = it.pdf_url
                        startDownload(pdfUrl)
                        if (pdfFilename.isEmpty()) {
                            // Below android 10, need write permission to save pdf in downloads
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                downloadPdfToShare(pdfUrl)
                            } else {
                                requestWritePermission()
                            }
                        }
                        /*if (Build.VERSION.SDK_INT >= 33) {
                            downloadAndDisplayPdf(pdfUrl)
                        }else{
                            if (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                // Permission is already granted, you can perform your camera-related tasks here
                                downloadAndDisplayPdf(pdfUrl)
                            } else {
                                // Permission is not granted, request it
                                    requestStoragePermission()
                                }
                            }*/
                    }
                } else if (it.status == 401) {
                    /*DialogUtils.unAuthorizedDialog(
                        this,
                        "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                        this
                    )*/
                    showUnauthorisedDialog()

                } else {
                    if (it.result?.message != null) {
                        it.result.message.let { it1 -> toast(it1) }
                    }

                }
            } else {
                toast(getString(R.string.server_error))
            }
        }


    }

    private fun downloadAndOpenPdf(pdfUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    toast(getString(R.string.downloading_pdf))
                }

                val url = URL(pdfUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: ${connection.responseCode}")
                }

                val inputStream = BufferedInputStream(connection.inputStream)
                // Use a simple filename, or parse from URL if it's reliable
                val fileName = "downloaded_pdf.pdf"
                val outputFile = File(cacheDir, fileName)

                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                withContext(Dispatchers.Main) {
                    openPdf(Uri.fromFile(outputFile)) // Open the downloaded file
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    toast(getString(R.string.error_downloading_pdf, e.message))
                    e.printStackTrace()
                }
            }
        }
    }

    private fun openPdf(uri: Uri) {
        try {
            val pfd: ParcelFileDescriptor? = contentResolver.openFileDescriptor(uri, "r")
            if (pfd != null) {
                pdfRenderer = PdfRenderer(pfd)
                // Only display the first page (index 0)
                if (pdfRenderer!!.pageCount > 0) {
                    showPage(pageIndex)

                    if(pdfRenderer!!.pageCount > 1){
                        binding.nextBT.visible()
                        binding.previousBT.visible()
                    }else{
                        binding.nextBT.gone()
                        binding.previousBT.gone()
                    }
                } else {
                    toast(getString(R.string.pdf_has_no_pages))
                }
            } else {
                toast(getString(R.string.failed_to_open_pdf_file_descriptor))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            toast(getString(R.string.error_opening_pdf))
        }
    }



    private fun showPage(index: Int) {

        pdfRenderer?.let { renderer ->
            if (renderer.pageCount <= index || index < 0) {
                return // Invalid page index
            }

            binding.dialogProgressBar.gone()

            currentPage?.close() // Close previous page if open

            currentPage = renderer.openPage(index) // Open the requested page

            // 🟡 SCALE FACTOR — increase for sharper image
            val scale = 2.0f // You can adjust this (e.g., 2.0f, 2.5f, 3.0f)

            // 🔵 Create high-resolution bitmap
            val width = (currentPage!!.width * scale).toInt()
            val height = (currentPage!!.height * scale).toInt()
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // Optional: clear background
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)

            // 🔴 Render at higher resolution
            currentPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Display the bitmap in the ImageView
            binding.pdfPageView.setImageBitmap(bitmap)

        } ?: run {
            toast(getString(R.string.pdf_not_loaded))
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

    private fun sharePdfFile() {

        try {
            val filePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/${pdfFilename}"
            val pdfFile = File(filePath)

            val pdfUri =
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/pdf"
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            val chooser = Intent.createChooser(shareIntent, "Share File")

            val resInfoList =
                this.packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                grantUriPermission(
                    packageName,
                    pdfUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            startActivity(chooser)
        } catch (e: Exception) {
        }
    }


    override fun isInternetOnCallApisAndInitUI() {

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
}
