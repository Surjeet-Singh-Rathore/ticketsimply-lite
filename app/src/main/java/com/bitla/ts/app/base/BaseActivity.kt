package com.bitla.ts.app.base

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
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
import com.bitla.ts.utils.common.clearAndSave
import com.bitla.ts.utils.common.getTodayDateWithTime
import com.bitla.ts.utils.constants.REQUEST_READ_PERMISSION
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
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import gone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import toast
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Base64
import java.util.Locale

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        val tag: String = BaseActivity::class.java.simpleName
    }

    private var alreadyDialogVisible: Boolean = false
    private var userList = mutableListOf<LoginModel>()

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