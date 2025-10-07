package com.bitla.ts.presentation.view.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.DialogButtonListener
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ActivityCrewToolkitBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.crew_delete_image.request.CrewDeleteImageRequest
import com.bitla.ts.domain.pojo.crew_toolkit.StuffGoodsDetail
import com.bitla.ts.domain.pojo.crew_toolkit.StuffImage
import com.bitla.ts.domain.pojo.crew_toolkit.request.CrewToolKitRequest
import com.bitla.ts.domain.pojo.crew_toolkit.request.ReqBody
import com.bitla.ts.domain.pojo.crew_update.request.UpdateCrewRequest
import com.bitla.ts.domain.pojo.crew_update.request.UpdateStuffGoodsDetail
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.CrewToolKitAdapter
import com.bitla.ts.presentation.viewModel.CrewToolKitViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getDateDMYY
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.downloadPdf.DownloadPdf
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast
import visible
import java.io.*


class CrewToolkitActivity : BaseActivity(), OnItemClickListener, DialogSingleButtonListener, DialogButtonListener {
    private var crewImagesDialog: AlertDialog? = null
    private var stuffId: Int? = null
    private val tag: String = "CrewToolkitActivity"
    private var busType: String = ""
    private var serviceNumber: String = ""
    private var deptTime: String = ""
    private var source: String = ""
    private var destination: String = ""
    private var stuffGoodsId: Int = 0
    private var pdfLink: String? = null
    private val updateStuffGoodsDetailList = mutableListOf<UpdateStuffGoodsDetail>()

    private val maxHeight = 1280.0f
    private val maxWidth = 1280.0f

    private val crewToolKitViewModel by viewModel<CrewToolKitViewModel<Any?>>()

    private lateinit var binding: ActivityCrewToolkitBinding

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var crewToolKitAdapter: CrewToolKitAdapter
    private var lastSelectedPosition: Int = -1

    var imageUri: Uri? = null

    private var apiKey: String = ""
    private var bccId: String = ""
    private var resId: Long = 0L
    private var loginModelPref = LoginModel()

    private var sourceId: String = ""
    private var destinationId: String = ""
    private var travelDate: String = ""
    private var locale: String? = ""
    private var stuffList = mutableListOf<StuffGoodsDetail>()
    private var stuffGoodsImages = mutableListOf<StuffImage>()
    private var initialIsCheckedList = mutableListOf<Boolean?>()
    private var initialRemarksList = mutableListOf<String>()
    private var isListUpdated: Boolean = false
    private var isFromUpdateDelete: Boolean = false


    override fun initUI() {
        binding = ActivityCrewToolkitBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }


        getPref()

        lifecycleScope.launch {
            crewToolKitViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
        firebaseLogEvent(
        this,
        CREW_TOOLKIT_CHECKLIST,
        loginModelPref.userName,
        loginModelPref.travels_name,
        loginModelPref.role,
        CREW_TOOLKIT_CHECKLIST,
        CrewToolkitChecklist.CREW_TOOLKIT_CHECKLIST
        )
        setToolbarSubtitle()
        // setNetworkConnectionObserver
        if (isNetworkAvailable())
            callFetchCrewList()
        else
            noNetworkToast()

    }

    private fun setNetworkConnectionObserver() {
//        networkConnection.observe(this) { isConnected ->
//            if (isConnected != null) {
//                if (isConnected) {
//                    if (isOnlineInit) {
//                        DialogUtils.showNetworkBackOnline(
//                            networkErrorWithDisableAllViews,networkBackOnline
//                        )
//                        DialogUtils.enableDisableView(binding.root, true)
//                        if (stuffList.isNotEmpty() || updateStuffGoodsDetailList.isNotEmpty()){
//                            stuffList.clear()
//                            updateStuffGoodsDetailList.clear()
//                        }
//                        getPref()
//                        callFetchCrewList()
////                        binding.viewPagerDashboard.endFakeDrag()
//                    }
//                } else {
//                    DialogUtils.showNetworkError(networkErrorWithDisableAllViews, networkBackOnline)
//                    DialogUtils.enableDisableView(binding.root, false)
//                    isOnlineInit = true
////                    binding.viewPagerDashboard.beginFakeDrag()
//                }
//            }
//        }

    }

    private fun setToolbarSubtitle() {
        val toolbarSubTitleInfo = if (serviceNumber.isNotEmpty())
            "$serviceNumber | ${getDateDMYY(travelDate)} | $deptTime | $source - $destination | $busType"
        else
            "${getDateDMYY(travelDate)} | $deptTime | $source - $destination | $busType"

        binding.includeToolbar.toolbarSubtitle.text = toolbarSubTitleInfo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObserver()
        onClickListener()
    }

    override fun isInternetOnCallApisAndInitUI() {
        if (stuffList.isNotEmpty() || updateStuffGoodsDetailList.isNotEmpty() || initialIsCheckedList.isNotEmpty() || initialRemarksList.isNotEmpty()) {
            stuffList.clear()
            updateStuffGoodsDetailList.clear()
            initialIsCheckedList.clear()
            initialRemarksList.clear()
        }
        getPref()
        callFetchCrewList()
    }

    private fun onClickListener() {
        binding.includeToolbar.imgBack.setOnClickListener(this)
        binding.includeToolbar.imgDownload.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.img_back -> {
                updateCrewDataChanges()
                if (updateStuffGoodsDetailList.size > 0) {
                    DialogUtils.twoButtonDialog(
                        this,
                        getString(R.string.leavePage),
                        getString(R.string.leavePageMsg),
                        getString(R.string.cancel),
                        getString(R.string.leave),
                        this
                    )
                } else {
                    onBackPressed()
                }
            }
            R.id.imgDownload -> checkWritePermissions()
            R.id.btnSave -> crewCheckListUpdateApiCall()
        }
    }

    private fun downloadPdf() {
        if (!pdfLink.isNullOrEmpty()) {
            DownloadPdf.downloadReportPdf(this, pdfLink!!)
        } else
            toast(getString(R.string.no_pdf_link))
    }

    private fun getPref() {
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        sourceId = PreferenceUtils.getSourceId()
        destinationId = PreferenceUtils.getDestinationId()
        source = PreferenceUtils.getSource()
        destination = PreferenceUtils.getDestination()
        travelDate = PreferenceUtils.getTravelDate()
        locale = PreferenceUtils.getlang()

        if (PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L) != null)
            resId = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L)!!

        if (PreferenceUtils.getString(PREF_BUS_TYPE) != null) {
            busType = PreferenceUtils.getString(PREF_BUS_TYPE)!!
        }

        if (PreferenceUtils.getString(PREF_COACH_NUMBER) != null) {
            serviceNumber = PreferenceUtils.getString(PREF_COACH_NUMBER)!!
        }

        if (PreferenceUtils.getString(PREF_DEPARTURE_TIME) != null) {
            deptTime = PreferenceUtils.getString(PREF_DEPARTURE_TIME)!!
        }


    }

    private fun callFetchCrewList() {
        val reqBody = ReqBody(
            api_key = loginModelPref.api_key,
            destination_id = destinationId,
            is_from_middle_tier = true,
            origin_id = sourceId,
            res_id = resId.toString(),
            travel_date = travelDate
        )

        crewToolKitViewModel.fetchToolKit(
            toolKitRequest = reqBody,
            apiType = fetch_crew_checklist_method
        )
    }

    private fun callUploadImageApi(imageUri: Uri) {
        val randomImageId = (0..100).random()
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(imageUri, filePathColumn, null, null, null)
            ?: return

        cursor.moveToFirst()

        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val filePath = cursor.getString(columnIndex)
        cursor.close()
        val compressedFilePath = compressImage(this, filePath)
        val file = File(compressedFilePath)
        if (file != null) {
            val reqFile = file.asRequestBody("image/*".toMediaType())
            val body: MultipartBody.Part =
                createFormData("stuff_goods_image", file.name.replace(" ", ""), reqFile)

            val bccIdRequestBody = bccId.toRequestBody("text/plain".toMediaType())
            val methodRequestBody =
                upload_crew_image_method.toRequestBody("text/plain".toMediaType())
            val formatRequestBody = format_type.toRequestBody("text/plain".toMediaType())
            val apiKeyRequestBody = loginModelPref.api_key.toRequestBody("text/plain".toMediaType())
            val resIdRequestBody = resId.toString().toRequestBody("text/plain".toMediaType())
            val goodsIdRequestBody =
                stuffGoodsId.toString().toRequestBody("text/plain".toMediaType())
            val goodsImageIdRequestBody =
                randomImageId.toString().toRequestBody("text/plain".toMediaType())

            crewToolKitViewModel.uploadCrewImageApi(
                apiKey = loginModelPref.api_key,
                locale = locale ?: "en",
                format = formatRequestBody,
                resId = resIdRequestBody,
                goodsId = goodsIdRequestBody,
                goodsImageId = goodsImageIdRequestBody,
                goodsImage = body,
                apiType = upload_crew_image_method
            )
        }
    }

    private fun deleteCrewImage(goodsId: Int?, imageId: String) {
        val reqBody = com.bitla.ts.domain.pojo.crew_delete_image.request.ReqBody(
            api_key = loginModelPref.api_key,
            stuff_goods_id = goodsId.toString(),
            stuff_goods_image_id = imageId
        )

        crewToolKitViewModel.deleteCrewImageApi(
            locale = locale ?: "en",
            crewDeleteImageRequest = reqBody,
            apiType = delete_crew_image_method
        )
    }

    private fun callUpdateCrewCheckList() {
        val reqBody = com.bitla.ts.domain.pojo.crew_update.request.ReqBody(
            api_key = loginModelPref.api_key,
            destination_id = destinationId,
            is_from_middle_tier = true,
            origin_id = sourceId,
            res_id = resId.toString(),
            travel_date = travelDate,
            update_stuff_goods_details = updateStuffGoodsDetailList
        )

        crewToolKitViewModel.updateCrewCheckListApi(
            apiKey = loginModelPref.api_key,
            locale = locale ?: "en",
            updateCrewRequest = reqBody,
            apiType = update_crew_checklist_method
        )
    }


    private fun setObserver() {
        crewToolKitViewModel.loadingState.observe(this, Observer { it ->
            Timber.d("LoadingState ${it.status}")
            when (it) {
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }
        })

        crewToolKitViewModel.fetchToolKit.observe(this) {
            if (stuffList.isNotEmpty() || updateStuffGoodsDetailList.isNotEmpty() || initialIsCheckedList.isNotEmpty() || initialRemarksList.isNotEmpty()) {
                if (!isFromUpdateDelete) {
                    stuffList.clear()
                    updateStuffGoodsDetailList.clear()
                    initialIsCheckedList.clear()
                    initialRemarksList.clear()
                }
            }
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                if (it.code == 200) {
                    if (isFromUpdateDelete) {
                        isFromUpdateDelete = false
                        stuffList.forEachIndexed { index, stuffItem ->
                            stuffItem.stuff_goods_image_details = it.stuff_goods_details[index].stuff_goods_image_details
                        }
                    } else {
                        stuffList.addAll(it.stuff_goods_details)
                        initialIsCheckedList = stuffList.map { it.is_checked }.toMutableList()
                        initialRemarksList = stuffList.map { it.remarks }.toMutableList()
                    }
                    pdfLink = it.pdf_link

                    stuffList.forEach { stuffItem ->
                        if (stuffItem.remarks == "null") {
                            isListUpdated = true
                            stuffItem.remarks = ""
                        }
                    }
                    if (isListUpdated) {
                        if (isNetworkAvailable()) crewCheckListUpdateApiCall() else noNetworkToast()
                    } else {
                        if (::crewToolKitAdapter.isInitialized) {
                            crewToolKitAdapter.notifyDataSetChanged()
                        } else {
                            setCrewCheckListAdapter()
                        }
                    }
                } else {
                    if (it.result?.message != null)
                        toast(it.result?.message)
                    else if (it.message != null)
                        toast(it.message)
                    else
                        toast(getString(R.string.server_error))
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        crewToolKitViewModel.uploadCrewImage.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                if (it.code == 200) {
                    toast(it.message)

                    isFromUpdateDelete = true
                    if (isNetworkAvailable())
                        callFetchCrewList()

                } else {
                    if (it.result?.message != null)
                        toast(it.result.message)
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        crewToolKitViewModel.deleteCrewImage.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                if (it.code == 200) {
                    toast(it.message)
                    if (crewImagesDialog != null)
                        crewImagesDialog!!.dismiss()

                    isFromUpdateDelete = true
                    if (isNetworkAvailable())
                        callFetchCrewList()
                } else {
                    if (it.result?.message != null)
                        toast(it.result.message)
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }

        crewToolKitViewModel.updateCrewList.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                if (it.code == 200) {
                    if (isListUpdated) {
                        isListUpdated = false
                    } else {
                        toast(it.message)
                    }
                    callFetchCrewList()
                } else {
                    if (it.result?.message != null)
                        toast(it.result?.message)
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setCrewCheckListAdapter() {

        try {
            layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvCrewToolKit.layoutManager = layoutManager
            crewToolKitAdapter = CrewToolKitAdapter(this, this, stuffList)
            binding.rvCrewToolKit.adapter = crewToolKitAdapter
        }catch (e :Exception){
           binding.NoResult.visible()
        }
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            when (view.tag) {
                getString(R.string.capture_image) -> {
                    if (stuffList[position].stuff_goods_image_details.size == 6) {
                        toast(getString(R.string.maxLimitToUploadImage))
                    } else {
                        lastSelectedPosition = position
                        stuffGoodsId = stuffList[position].id
                        checkCameraPermissions()
                    }
                }

                getString(R.string.crew_images) -> {
                    // show/delete image dialog
                    stuffGoodsImages = stuffList[position].stuff_goods_image_details
                    stuffId = stuffList[position].id
                    if (stuffGoodsImages.isNotEmpty()) {
                        crewImagesDialog = DialogUtils.crewImageDialog(
                            this,
                            stuffList[position].name,
                            this,
                            stuffGoodsImages
                        )
                    }
                }

                else -> {
                    // switch
                    crewCheckListUpdateApiCall()
                }
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    private fun updateCrewDataChanges() {
        updateStuffGoodsDetailList.clear()
        stuffList.forEachIndexed { index, it ->
            if (it.is_checked != initialIsCheckedList[index] || it.remarks != initialRemarksList[index]) {
                val updateStuffGoodsDetail = UpdateStuffGoodsDetail(
                    it.id,
                    it.is_checked,
                    it.name,
                    it.remarks
                )
                updateStuffGoodsDetailList.add(updateStuffGoodsDetail)
            }
        }
    }

    private fun crewCheckListUpdateApiCall() {
        updateCrewDataChanges()
        if (updateStuffGoodsDetailList.size > 0) {
            if (isNetworkAvailable())
                callUpdateCrewCheckList()
            else
                noNetworkToast()
        } else {
            toast(getString(R.string.noChangesDetected))
        }
    }

    private fun checkCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, REQUEST_CAMERA_PERMISSION)
            } else {
                openCamera()
            }
        }
    }

    private fun checkWritePermissions() {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permissionResult = DownloadPdf.checkPermission(permission, this)
        if (Build.VERSION.SDK_INT >= 33) {
            downloadPdf()
        } else {
            if (permissionResult) {
                downloadPdf()
            } else {
                DownloadPdf.onRequestPermissionsResult(STORAGE_PERMISSION_CODE, permission, this)
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "")
        values.put(MediaStore.Images.Media.DESCRIPTION, "")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    private fun compressImage(context: Context, imagePath: String): String? {
        var scaledBitmap: Bitmap?

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
        var bmp: Bitmap? = BitmapFactory.decodeFile(imagePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
            return null
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
            return null
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp!!.width / 2,
            middleY - bmp.height / 2,
            Paint(FILTER_BITMAP_FLAG)
        )

        bmp.recycle()

        val exif: ExifInterface
        try {
            exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap!!,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val out: FileOutputStream?
        val filepath = getFilename(context)
        try {
            out = FileOutputStream(filepath)
            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)

        } catch (e: FileNotFoundException) {

            e.printStackTrace()
        }

        return filepath
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }

    private fun getFilename(context: Context): String {
        val mediaStorageDir =
            File("${getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/Android/data/${context.applicationContext.packageName}/Files/Compressed")
        //File("${Environment.getExternalStorageDirectory()}/Android/data/${context.applicationContext.packageName}/Files/Compressed")
        // Create the storage directory if it does not exist
        try {
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs()
            }
        } catch (e: Exception) {
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.createNewFile()
            }
        }

        val mImageName = "IMG_" + System.currentTimeMillis().toString() + ".jpg"
        return mediaStorageDir.absolutePath + "/" + mImageName
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("requestCode $requestCode lastSelectedPosition $lastSelectedPosition")
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            imageUri?.let { uriToBitmap(it) }
            if (imageUri != null) {
                callUploadImageApi(imageUri!!)
            }
        }
    }


    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera()
            else
                toast(getString(R.string.camera_permission_denied))

        }
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                downloadPdf()
            else
                toast(getString(R.string.write_permission_denied))
        }
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    override fun onSingleButtonClick(str: String) {
        Timber.d("onSingleButtonClick")
        if (str != null && str.isNotEmpty()) {
            val position = str.toDouble().toInt()
            if (stuffGoodsImages.size > position && stuffId != null) {
                val imageId = stuffGoodsImages[position].stuff_goods_image_id.toString()
                if (isNetworkAvailable())
                    deleteCrewImage(stuffId, imageId)
                else
                    noNetworkToast()
            }
        }
    }

    override fun onLeftButtonClick() {}

    override fun onRightButtonClick() {
        onBackPressed()
    }
}