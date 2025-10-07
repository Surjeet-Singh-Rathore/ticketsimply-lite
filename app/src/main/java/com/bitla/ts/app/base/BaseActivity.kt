package com.bitla.ts.app.base

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.postDelayed
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.NetworkOfflineOnlineLayoutBinding
import com.bitla.ts.domain.pojo.DeviceInfo
import com.bitla.ts.domain.pojo.booking.Tabs
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.view.activity.DomainActivity
import com.bitla.ts.utils.FontScaleContextWrapper
import com.bitla.ts.utils.application.DeviceDetails
import com.bitla.ts.utils.common.clearAndSave
import com.bitla.ts.utils.common.getTodayDateWithTime
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_Y
import com.bitla.ts.utils.constants.REALTIME_CHILD
import com.bitla.ts.utils.constants.REQUEST_READ_PERMISSION
import com.bitla.ts.utils.constants.STORAGE_PERMISSION
import com.bitla.ts.utils.constants.USER_COLLECTION
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.IS_LOGOUT_VIA_AUTH_FAIL
import com.bitla.ts.utils.sharedPref.PREF_FCM_TOKEN
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_LOGGED_IN_USER
import com.bitla.ts.utils.sharedPref.PREF_PRIVILEGE_DETAILS
import com.bitla.ts.utils.sharedPref.PREF_TRAVEL_DATE
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.sharedPref.PreferenceUtils.getObject
import com.bitla.ts.utils.sharedPref.PreferenceUtils.mLocalPreferences
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.gson.GsonBuilder
import gone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import toast
import visible
import java.io.File
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Base64
import java.util.Calendar
import java.util.Locale

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        val tag: String = BaseActivity::class.java.simpleName
    }

    private var alreadyDialogVisible: Boolean = false
    private var userList = mutableListOf<LoginModel>()
    private lateinit var childListener: ChildEventListener

    //Firebase Instace Variables//
    private val firestoreDb = FirebaseFirestore.getInstance()
    private val fbDbRef = FirebaseDatabase.getInstance().reference
    private val fileNameList = ArrayList<String>()

    // network connection
    private lateinit var networkErrorWithDisableAllViews: LinearLayout
    private lateinit var networkBackOnline: LinearLayout
    private lateinit var bottomSheetDialogOffline: BottomSheetDialog
    private lateinit var bottomSheetDialogOnline: BottomSheetDialog
    private lateinit var networkConnection: BaseNetworkConnectionObserver

    private var pendingPdfFilenameToShare: String? = null // To access the fileName after getting storage permission

    //    private var usersList = mutableListOf<SpinnerItems>()
    //abstract fun getLayout(): Int
    abstract fun initUI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        controller.isAppearanceLightStatusBars = true // false = white icons

        setStatusBarColor(this, R.color.colorPrimary, false)


        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        /* val layoutResId = getLayout()
         if (layoutResId != 0) {
             try {
                 setContentView(layoutResId)
             } catch (e: Exception) {
                 e.printStackTrace()
             }
         }*/
        initUI()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Timber.d("fcmToken-$token")
                PreferenceUtils.putString(PREF_FCM_TOKEN, token.toString())
            }
        }
        getDataFromFirebase()

        networkConnection = BaseNetworkConnectionObserver(this)
        networkConnection.observe(this) { isConnected ->
            if (isConnected != null) {
                if (isConnected) {
                    alreadyDialogVisible = false
                    isNetworkConnectionOn(this@BaseActivity)
                    isInternetOnCallApisAndInitUI()
                } else {
                    if (!alreadyDialogVisible) {
                        isNetworkConnectionOff(this@BaseActivity)
                        alreadyDialogVisible = true
                    }


                }
            }
        }
    }

    abstract fun isInternetOnCallApisAndInitUI()

    fun <T> putObjectBase(`object`: T, key: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val jsonString = GsonBuilder().create().toJson(`object`)

            if (key == PREF_PRIVILEGE_DETAILS) {
                mLocalPreferences.edit()?.putString(key, jsonString)?.apply()

                try {
                    openFileOutput("privilege_data", Context.MODE_PRIVATE)?.use { outputStream ->
                        outputStream.write(jsonString.toByteArray())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                mLocalPreferences.edit()?.putString(key, jsonString)?.apply()
            }
        }
    }
    fun getPrivilegeBase(): PrivilegeResponseModel? {
        try {
            // Read the JSON string from the file
            val jsonString =
                this.openFileInput("privilege_data").bufferedReader().use { it.readText() }
            // Convert the JSON string back to an object
            return GsonBuilder().create().fromJson(jsonString, PrivilegeResponseModel::class.java)
        } catch (e: Exception) {
            // Handle case where the file doesn't exist
            return null
        }
    }

    suspend fun getPrivilegeBaseSafely(context: Context? = null): PrivilegeResponseModel? {
        return withContext(Dispatchers.IO) {
            try {
                if (context != null) {
                    // Read the JSON string from the file
                    val jsonString = context.openFileInput("privilege_data").bufferedReader()
                        .use { it.readText() }
                    // Convert the JSON string back to an object
                    return@withContext GsonBuilder().create()
                        .fromJson(jsonString, PrivilegeResponseModel::class.java)
                } else {
                    return@withContext getObject<PrivilegeResponseModel>(PREF_PRIVILEGE_DETAILS)
                }
            } catch (e: Exception) {
                context?.toast(e.message)
                // Handle case where the file doesn't exist
                return@withContext null
            }
        }
    }

    object PrivilegeManager {
        fun getPrivilegeBase(context: Context): PrivilegeResponseModel? {
            return try {
                // Read the JSON string from the file
                val jsonString =
                    context.openFileInput("privilege_data").bufferedReader().use { it.readText() }
                // Convert the JSON string back to an object
                GsonBuilder().create().fromJson(jsonString, PrivilegeResponseModel::class.java)
            } catch (e: Exception) {
                // Handle case where the file doesn't exist or other exceptions
                null
            }
        }
    }


    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onClick(v: View) {
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        fbDbRef.child(REALTIME_CHILD).removeEventListener(childListener)
    }

    override fun onPause() {
        super.onPause()
        fbDbRef.child(REALTIME_CHILD).removeEventListener(childListener)

    }

    override fun onStop() {
        super.onStop()
        fbDbRef.child(REALTIME_CHILD).removeEventListener(childListener)
    }

    protected fun getSubString(str: String, startIndex: Int, endIndex: Int): CharSequence? {
        return try {
            if (str.isNotEmpty()) {
                str.substring(startIndex, endIndex)
            } else {
                str
            }
        } catch (e: Exception) {
            str
        }

    }

    protected fun replaceBracketsString(str: String): String {
        return str.replace("[", "").replace("]", "").trim()
    }

    protected fun setTabs(
        tabs: TabLayout,
        viewPager: ViewPager,
        fragmentTab1: Fragment,
        fragmentTab2: Fragment,
        titleTabLeft: String,
        titleTabRight: String
    ) {
        val tabsList: MutableList<Tabs> = mutableListOf()
        val tabAll = Tabs()
        tabAll.title = titleTabLeft
        tabsList.add(tabAll)
        if (titleTabRight.isNotEmpty()) {
            val tabSelected = Tabs()
            tabSelected.title = titleTabRight
            tabsList.add(tabSelected)
        }
        val fragmentAdapter = BasePagerAdapter(
            this,
            tabsList,
            supportFragmentManager,
            fragmentTab1,
            fragmentTab2,
            titleTabLeft,
            titleTabRight
        )
        viewPager.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewPager)
        // custom tabs
        for (i in 0..tabs.tabCount.minus(1)) {
            val tab = tabs.getTabAt(i)!!
            tab.customView = null
            //tab!!.customView = fragmentAdapter.getTabView(i)
            val tabTextView: TextView = TextView(this)
            tab.customView = tabTextView
            tabTextView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            tabTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            tabTextView.text = tab.text
            if (i == 0) {
                // This set the font style of the first tab
                tabTextView.setTypeface(null, Typeface.BOLD)
            }
            if (i == 1) {
                tabTextView.setTypeface(null, Typeface.NORMAL)
            }
        }


        if (titleTabRight.isEmpty()) {
            tabs.gone()
        }

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
                val text: TextView = tab.customView as TextView
                text.setTypeface(null, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val text: TextView = tab?.customView as TextView
                text.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    interface UpdateBulkTicketInterface {
        fun showEditPassengersSheet(pnrNumber: Any)
    }

    interface UpdateSingleTicketInterface {
        fun showSingleTicketUpdateSheet(pnrNumber: Any, seatNo: String)
    }

    interface  CancelTicketTicketInterface {
        fun showTicketCancellationSheet(pnrNumber: Any)
    }

    interface ViewPassengersSheetInterface {
        fun showViewPassengersSheet(pnrNumber: Any)
    }

    fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun isNumeric(string: String): Boolean {
        return string.matches("^[0-9]*$".toRegex())
    }

    fun roundOffDecimal(number: Double): Double? {
        return try {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            df.format(number).toDouble()
        } catch (ex: Exception) {
            number
        }

    }

    fun registerUserDataToFirestore(
        domainName: String, userName: String, userId: String, activity: Activity?, clazz: Class<*>?,
        progressBar: ProgressBar?
    ) {
        if (progressBar != null) {
            progressBar.visible()
        }

        val user = hashMapOf<String, String>()
        user["domain"] = domainName
        user["username"] = userName
        user["userId"] = userId
        user["versionCode"] = BuildConfig.VERSION_CODE.toString()
        user["versionName"] = BuildConfig.VERSION_NAME
        user["fcmKey"] = PreferenceUtils.getString(PREF_FCM_TOKEN)!!
        user["deviceId"] = DeviceDetails.getDeviceId(baseContext)
        user["deviceName"] =
            encodeString(DeviceDetails.getBrand()) + " " + encodeString(DeviceDetails.getModel())
        user["androidVersion"] = Build.VERSION.RELEASE
        user["deviceName"] =
            encodeString(DeviceDetails.getBrand()) + " " + encodeString(DeviceDetails.getModel())
        user["getLog"] = "false"
        user["regTime"] = LocalDate.now()
            .toString() + " " + LocalTime.now().hour.toString() + ":" + LocalTime.now().minute
        user["onTime"] = LocalDate.now()
            .toString() + " " + LocalTime.now().hour.toString() + ":" + LocalTime.now().minute

        val dbRef = firestoreDb.collection(USER_COLLECTION)
        val query = dbRef.whereEqualTo("username", userName)
            .whereEqualTo("domain", domainName).whereEqualTo("userId", userId)
            .whereEqualTo("deviceId", DeviceDetails.getDeviceId(baseContext))

        query.get()
            .addOnSuccessListener { documents ->

                if (!documents.isEmpty) {
                    for (document in documents) {
                        val update: MutableMap<String, Any> = HashMap()
                        update["onTime"] = LocalDate.now()
                            .toString() + " " + LocalTime.now().hour.toString() + ":" + LocalTime.now().minute
                        update["fcmKey"] = PreferenceUtils.getString(PREF_FCM_TOKEN)!!

                        dbRef.document(document.id)
                            .set(update, SetOptions.merge())
                            .addOnSuccessListener {
                                if (progressBar != null) {
                                    progressBar.visibility = View.GONE

                                }
                            }
                            .addOnFailureListener {
                                if (progressBar != null) {
                                    progressBar.visibility = View.GONE
                                }
                            }
                    }
                } else {
                    dbRef.add(user)
                        .addOnSuccessListener {
                            toast("Login Successful...")
                        }
                        .addOnFailureListener {
                            if (progressBar != null) {
                                progressBar.visibility = View.GONE
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                Timber.d("ERROR_CAUSE", "Error getting documents: ", exception)
            }
        if (clazz != null && activity != null) {
            intent = Intent(this, clazz)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun getDataFromFirebase() {
        var domainName = ""
        var userName = ""
        var userId = ""

        val deviceName = try {
            DeviceDetails.getDeviceId(applicationContext) + "_" + encodeString(DeviceDetails.getBrand()) + " " + encodeString(
                DeviceDetails.getModel()
            )
        } catch (e: Exception) {
            ""
        }


        val loginModel = PreferenceUtils.getLogin()

        domainName = loginModel.domainName
        userName = loginModel.userName
        userId = loginModel.user_id.toString()


        childListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()) {
                    val mapOfUser = HashMap<String, String>()
                    for (childSnapshot in snapshot.children) {

                        mapOfUser[childSnapshot.key.toString()] = childSnapshot.value.toString()
                    }

                    val uDomain = mapOfUser["domain"].toString()
                    val uUsername = mapOfUser["username"].toString()
                    val uUserId = mapOfUser["userId"].toString()
                    val uDocumentId = mapOfUser["documentId"]

                    if (domainName != null && domainName.isNotEmpty()) {
                        if (userName != null && userName.isNotEmpty()) {
                            if (userName == uUsername && domainName == uDomain && userId == uUserId) {
                                if (!PreferenceUtils.getLogFileNames().isNullOrEmpty()) {
                                    PreferenceUtils.getLogFileNames()?.forEach { nameString ->
                                        readFileDataBase(nameString)
                                    }

                                } else {

                                }
                                snapshot.ref.removeValue()

                                registerUserDataToFirestore(
                                    uDomain,
                                    uUsername,
                                    uUserId,
                                    null,
                                    null,
                                    null
                                )
                            }
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                Timber.i("LogFlow: Child Changed")
                if (!PreferenceUtils.getLogFileNames().isNullOrEmpty()) {
                    PreferenceUtils.getLogFileNames()?.forEach { nameString ->
                        readFileDataBase(nameString)
                    }

                } else
                    Timber.d(" fileName1: No File Found")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var mapOfUser = HashMap<String, String>()
                    for (childSnapshot in snapshot.children) {
                        mapOfUser[childSnapshot.key.toString()] = childSnapshot.value.toString()
                    }
                    val uDomain = mapOfUser["domain"].toString()
                    val uUsername = mapOfUser["username"].toString()
                    val uUserId = mapOfUser["userId"].toString()
                    val uDocumentId = mapOfUser["documentId"]

                    if (domainName != null && domainName.isNotEmpty()) {
                        if (userName != null && userName.isNotEmpty()) {
                            if (userName == uUsername && domainName == uDomain && userId == uUserId) {
                                if (!PreferenceUtils.getLogFileNames().isNullOrEmpty()) {
                                    PreferenceUtils.getLogFileNames()?.forEach { nameString ->
                                        readFileDataBase(nameString)
                                    }

                                } else
                                    Timber.d(" fileName: No File Found")

                                registerUserDataToFirestore(
                                    uDomain,
                                    uUsername,
                                    uUserId,
                                    null,
                                    null,
                                    null
                                )

                            }
                        }
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                if (!PreferenceUtils.getLogFileNames().isNullOrEmpty()) {
                    PreferenceUtils.getLogFileNames()?.forEach {
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                toast("Error Occured")
            }
        }
        fbDbRef.child(REALTIME_CHILD).ref.child(deviceName).addChildEventListener(childListener)

    }

    private fun readFileDataBase(fileName: String) {
        try {
            val storageRef = FirebaseStorage.getInstance().reference.child("logs/$fileName")
            val file = File(filesDir, fileName)
            val uri = Uri.fromFile(file)
            val uploadTask: UploadTask = storageRef.putFile(uri)

            uploadTask.addOnProgressListener { taskSnapshot ->
            }.addOnPausedListener {
                fun onPaused(taskSnapshot: UploadTask.TaskSnapshot?) {
                }
            }.addOnFailureListener {
                fun onFailure(exception: Exception?) {
                    // Handle unsuccessful uploads
                }
            }.addOnSuccessListener {
                fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot?) {
                    Timber.i("file uploaded successfully")
                }
            }
        } catch (e: IOException) {
            Timber.d("reading Logs exception ${e.message}")
//            toast("some error")
            e.printStackTrace()
        }
    }

    override fun fileList(): Array<String> {
        return super.fileList()
    }

    fun deleteFrmLocal() {

        fileList().forEach {
            if (it.endsWith(".txt")) {
                if (!it.contains("_info.txt") || !it.contains("_lang.txt")) {
                    if (!fileNameList.contains(it)) {
                        fileNameList.add(it)
                    }
                }
            }
        }

        for (i: Int in 0..fileNameList.size) {

            if (!fileNameList[i].endsWith("info.txt") ||
                !fileNameList[i].endsWith("lang.txt")
            ) {

                if (checkFilesLastFiveDays(fileNameList[i])) {
                    val file = File(filesDir, fileNameList[i])

                    file.delete()
                }
            }
        }
        fileNameList.clear()
    }

    private fun checkFilesLastFiveDays(fileName: String): Boolean {
        var isTrue = false
        val fileSplit = fileName.split("_")
        val fileSplit2 = fileSplit[3].split(".")
        val sdf = SimpleDateFormat(DATE_FORMAT_D_M_Y)
        val fileDate = sdf.parse(fileSplit2[0])

        val calendar = Calendar.getInstance()
        calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -5)
        val dayFiveDaysBack = calendar.time
        if (fileDate < dayFiveDaysBack) {
            isTrue = true
        }
        return isTrue
    }

    override fun attachBaseContext(newBase: Context?) {
        val wrappedContext = newBase?.let { FontScaleContextWrapper.wrap(it) }
        super.attachBaseContext(wrappedContext)
    }

    private fun isNetworkConnectionOff(context: Context) {

        bottomSheetDialogOffline = BottomSheetDialog(context, R.style.BottomSheetDialog)
        val binding = NetworkOfflineOnlineLayoutBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialogOffline.setContentView(binding.root)
        bottomSheetDialogOffline.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        networkErrorWithDisableAllViews = binding.disableAllViews
        networkBackOnline = binding.layoutNetworkBackOnline

        DialogUtils.showNetworkError(networkErrorWithDisableAllViews, networkBackOnline)

        bottomSheetDialogOffline.setCancelable(false)

        isNetworkConnectionOn(context)
        bottomSheetDialogOffline.show()
    }

    fun dismissBottomSheet() {
        bottomSheetDialogOffline.dismiss()
    }

    private fun isNetworkConnectionOn(context: Context) {
        if (::bottomSheetDialogOffline.isInitialized)
            bottomSheetDialogOffline.dismiss()

        bottomSheetDialogOnline = BottomSheetDialog(context, R.style.BottomSheetDialog)
        val binding = NetworkOfflineOnlineLayoutBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialogOnline.setContentView(binding.root)

        networkErrorWithDisableAllViews = binding.disableAllViews
        networkBackOnline = binding.layoutNetworkBackOnline
        DialogUtils.showNetworkBackOnline(networkErrorWithDisableAllViews, networkBackOnline)

        DialogUtils.enableDisableView(binding.root, true)

        Handler(Looper.getMainLooper()).postDelayed(1500) {
            if (context is Activity && !context.isFinishing && !context.isDestroyed) {
                if (::bottomSheetDialogOnline.isInitialized && bottomSheetDialogOnline.isShowing) {
                    bottomSheetDialogOnline.dismiss()
                }
            }
        }

        bottomSheetDialogOnline.setCancelable(true)
        bottomSheetDialogOnline.show()
    }

    private fun encodeString(string: String): String {
        return string.replace(".", " ").replace("#", " ").replace("[", " ").replace("]", " ")
            .replace("$", " ")
    }

    fun getDeviceInfo(loginModel: LoginModel): String {
        val deviceInfo = DeviceInfo(
            android_os = Build.MANUFACTURER,
            android_version = Build.VERSION.RELEASE,
            app_domain = loginModel.domainName,
            app_language = PreferenceUtils.getlang(),
            app_package_name = BuildConfig.APPLICATION_ID,
            app_user_login = loginModel.userName,
            app_version = BuildConfig.VERSION_NAME,
            app_version_code = BuildConfig.VERSION_CODE,
            device_date = getTodayDateWithTime(),
            device_info = "Brand: ${Build.BRAND} Model: ${Build.MODEL}",
            device_language = Locale.getDefault().displayLanguage,
        )

        return encodeInputString(deviceInfo.toString())
    }

    private fun encodeInputString(input: String): String {
        val byte = input.toByteArray(charset("UTF-8"))
        val base64 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(byte)
        } else {
            android.util.Base64.encodeToString(byte, 0)
        }
        return base64
    }

    fun showUnauthorisedDialog() {
        DialogUtils.unAuthorizedDialog(this,
            "${getString(R.string.authentication_failed)}\n\n",
            object : DialogSingleButtonListener {
                override fun onSingleButtonClick(str: String) {
                    openDomainActivity()
                }
            }
        )
    }

    private fun openDomainActivity() {
        PreferenceUtils.putObject(null, PREF_LOGGED_IN_USER)
        PreferenceUtils.removeKey(PREF_TRAVEL_DATE)
        clearAndSave(this)
        PreferenceUtils.putString(IS_LOGOUT_VIA_AUTH_FAIL, "true")
        PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
        val intent = Intent(this, DomainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun setStatusBarColor(
        activity: Activity,
        @ColorRes color: Int,
        isLightStatusBar: Boolean = false
    ) {
        try {
            val window = activity.window
            val colorValue = ContextCompat.getColor(activity, color)

            window.statusBarColor = colorValue

            // Set light status bar (dark icons) if needed
            if (isLightStatusBar) {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }catch (e: Exception){
            if(BuildConfig.DEBUG){
                e.printStackTrace()
            }
        }

    }

    /**
     * Gets the status bar height from resources.
     */
    private fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            // Default fallback value if resource not found
            (24 * context.resources.displayMetrics.density).toInt()
        }
    }

    fun sharePdfFile(pdfFilename: String) {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                // For Android 10+ (API 29+)
                sharePdfFileHighEnd(pdfFilename)
            } else {
                // For Android 10 and below (API 29 and below)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    pendingPdfFilenameToShare = pdfFilename // Storing the fileName to access after getting storage permission

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_PERMISSION
                    )
                    return
                } else {
                    sharePdfFileHighEnd(pdfFilename)
                }
            }
        } catch (e: Exception) {
            toast(getString(R.string.error_occured))
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pendingPdfFilenameToShare?.let {
                    sharePdfFile(it)
                }
            } else {
                toast(getString(R.string.please_grant_permission_to_share_pdf))
            }
            pendingPdfFilenameToShare = null // Clear after use
        }
    }

    private fun sharePdfFileLowEnd(pdfFilename: String) {
        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/${pdfFilename}"
        val pdfFile = File(filePath)

        val pdfUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile)

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
    }

    private fun sharePdfFileHighEnd(pdfFilename: String) {
        try {
            val pdfFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                pdfFilename
            )

            if (!pdfFile.exists()) {
                toast("File not found: ${pdfFile.absolutePath}")
                return
            }

            // Get URI using FileProvider for all Android versions
            val pdfUri = FileProvider.getUriForFile(
                this,
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                pdfFile
            )

            // Create share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, pdfUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share File")

            // Grant URI permissions to all target apps
            val resInfoList = packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                grantUriPermission(
                    packageName,
                    pdfUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }

            startActivity(chooser)
        } catch (e: Exception) {
            toast(getString(R.string.error_occured))
        }
    }

}