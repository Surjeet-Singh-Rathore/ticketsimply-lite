package com.bitla.ts.presentation.view.activity.notifications

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.fetch_notification_method
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemNotificationClickListener
import com.bitla.ts.data.update_notification_method
import com.bitla.ts.databinding.ActivityNotificationsBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.fetch_notification.NotificationDetail
import com.bitla.ts.domain.pojo.fetch_notification.request.FetchNotificationRequest
import com.bitla.ts.domain.pojo.fetch_notification.request.NotificationFilter
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.update_notification.request.ReqBody
import com.bitla.ts.domain.pojo.update_notification.request.UpdateNotificationRequest
import com.bitla.ts.presentation.adapter.NotificationFilterAdapter
import com.bitla.ts.presentation.adapter.NotificationParentAdapter
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getDeviceUniqueId
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible
import java.lang.reflect.Method

class NotificationsActivity : BaseActivity(), OnItemNotificationClickListener,
    OnItemClickListener {

    private var filterMenu: PopupMenu? = null
    private var lastMessageTypeSelected = 0
    private var isUnreadSelected: Boolean = true
    private var lastFilterType = ""
    private var lastSelectedPosition = 0
    private var notificationIds: String = ""
    private var binding: ActivityNotificationsBinding? = null
    private var notificationFilters: MutableList<NotificationFilter> =
        mutableListOf()
    private lateinit var notificationFilterAdapter: NotificationFilterAdapter

    private var notificationDetailsList: MutableList<NotificationDetail> = mutableListOf()
    private lateinit var notificationAdapter: NotificationParentAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var isAllMarkAsReadUnread: Boolean = false

    private var loginModelPref: LoginModel = LoginModel()
    private var bccId: Int? = 0
    //lateinit var mainHandler: Handler

    private lateinit var requestLauncher: ActivityResultLauncher<String>
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    override fun initUI() {
        getPref()
    }

    /*private val updateTextTask = object : Runnable {
        override fun run() {
            if (isNetworkAvailable())
                callFetchNotificationApi()
            else
                noNetworkToast()
            mainHandler.postDelayed(this, 5000)
        }
    }*/
    
    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                   showToast(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (isUnreadSelected) {
            if (isNetworkAvailable())
                callFetchNotificationApi()
            else
                noNetworkToast()

            setUpObserver()
        } else {
            readSelected()
        }
        enableNotification()

      //  mainHandler.post(updateTextTask)

    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        binding!!.toolbarNotifications.tvCurrentHeader.setText(R.string.notifications)
        binding!!.layoutNoNotificationData.tvNoData.text =
            getString(R.string.no_notifications_have_been_received)

        val view = binding!!.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(view)
        }


       // mainHandler = Handler(Looper.getMainLooper())


        binding!!.toolbarNotifications.imgSearch.gone()

        if (isNetworkAvailable()){
            binding!!.progressPB.visible()
            callFetchNotificationApi()
        }

        else
            noNetworkToast()

        setUpObserver()
//         setNetworkConnectionObserver()
        enableNotification()
        onReuestNotifyLaunchFun()
        binding?.btnTurnOn?.setOnClickListener {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
                startActivity(settingsIntent)
            } else {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.POST_NOTIFICATIONS,
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    binding?.notificationReqCard?.gone()
                } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                    val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
                    startActivity(settingsIntent)
                } else {
                    binding?.notificationReqCard?.visible()
                    askPermissionNotification()
                }
            }

        }

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            binding?.swipeRefreshLayout?.isRefreshing = true
            if (isNetworkAvailable()) {
                callFetchNotificationApi()
            } else {
                noNetworkToast()
            }

        }

        //fab implementation

        binding?.fabBT?.setOnClickListener {
            showPopupMenu(binding?.fabBT!!)
        }



    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        filterMenu = popupMenu
        popupMenu.menuInflater.inflate(R.menu.notification_menu, popupMenu.menu)

        when (lastMessageTypeSelected) {
            0 -> {
                addSelectedTick(popupMenu, 0)
            }

            1 -> {
                addSelectedTick(popupMenu, 1)
            }

            2 -> {
                addSelectedTick(popupMenu, 2)
            }
        }


        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.allMessagesTV -> {
                    lastMessageTypeSelected = 0
                    addSelectedTick(popupMenu, 0)

                    if (this::notificationAdapter.isInitialized) {
                        if (notificationDetailsList.size > 0) {
                            binding!!.rvNotificationDetails.visible()
                            binding!!.layoutNoNotificationData.root.gone()
                            binding!!.layoutNoData.root.gone()
                            notificationAdapter.readUnreadFilter(false, true)
                            notificationAdapter.notifyDataSetChanged()
                        }

                    }


                    true
                }

                R.id.readMessagesTV -> {
                    lastMessageTypeSelected = 1
                    addSelectedTick(popupMenu, 1)
                    if (this::notificationAdapter.isInitialized) {
                        isUnreadSelected = false
                        readSelected()
                    }
                    true
                }

                R.id.unreadMessagesTV -> {
                    lastMessageTypeSelected = 2
                    addSelectedTick(popupMenu, 2)
                    if (this::notificationAdapter.isInitialized) {
                        isUnreadSelected = true
                        unreadSelected()
                    }


                    true
                }

                else -> false
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        } else {
            try {
                val fields = popupMenu.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field[popupMenu]
                        val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        popupMenu.show()
    }

    private fun addSelectedTick(popupMenu: PopupMenu, itemId: Int) {
        val menuItem = popupMenu.menu.getItem(itemId)
        val spannableString = SpannableString("${menuItem.title} ")
        spannableString.setSpan(
            ImageSpan(this, R.drawable.ic_tick_blue, DynamicDrawableSpan.ALIGN_BASELINE),
            spannableString.length - 1,
            spannableString.length,
            Spannable.SPAN_INTERMEDIATE
        )
        menuItem.title = spannableString
    }

    private fun unselecteMenu(popupMenu: PopupMenu, itemId: Int) {
        val menuItem = popupMenu.menu.getItem(itemId)
        val spannableString = SpannableString("${menuItem.title} ")
        menuItem.title = spannableString
    }

    private fun onReuestNotifyLaunchFun() {
        requestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                binding?.notificationReqCard?.gone()
            } else {
                Toast.makeText(
                    this,
                    "Please Grant Notification Permission from App Settings",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun askPermissionNotification() {
        requestLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun enableNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.areNotificationsEnabled()) {
                binding?.notificationReqCard?.gone()
            } else {
                binding?.notificationReqCard?.visible()
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
        callFetchNotificationApi()
    }


    private fun callFetchNotificationApi() {
        val fetchNotificationRequest = FetchNotificationRequest(
            bccId.toString(), format_type, fetch_notification_method,
            com.bitla.ts.domain.pojo.fetch_notification.request.ReqBody(
                loginModelPref.api_key
            )
        )

     /*   if (isNetworkAvailable()) {
            sharedViewModel.newFetchNotifications(
                loginModelPref.api_key, fetch_notification_method
            )
        } else {
            noNetworkToast()
        }*/
    }

    private fun callUpdateNotificationApi() {
        val updateNotificationRequest = UpdateNotificationRequest(
            bccId.toString(), format_type, update_notification_method,
            ReqBody(
                loginModelPref.api_key,
                getDeviceUniqueId(this),
                "true",  // "true/false" for mark read/unread APIs
                notificationIds.removeSuffix(",")
            )
        )

        if (isNetworkAvailable()) {
            sharedViewModel.updateNotification(
                loginModelPref.auth_token,
                loginModelPref.api_key, updateNotificationRequest, update_notification_method
            )
        } else {
            noNetworkToast()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObserver() {
        sharedViewModel.loadingState.observe(this) {
            when (it) {
                LoadingState.LOADING -> binding!!.includeProgress.progressBar.gone()
                LoadingState.LOADED -> binding!!.includeProgress.progressBar.gone()
                else -> binding!!.includeProgress.progressBar.gone()
            }
        }

        sharedViewModel.fetchNotificationModel.observe(this) {
            binding!!.includeProgress.progressBar.gone()
            binding?.swipeRefreshLayout?.isRefreshing = false

           /* if (filterMenu != null) {
                lastMessageTypeSelected = 0
                addSelectedTick(filterMenu!!, 0)
            }*/


            if (it != null) {
                if (it.code == 200) {
                    binding!!.progressPB.gone()
                    binding!!.layoutMain.visible()
                    binding!!.layoutNoData.root.gone()
                    binding!!.layoutNoNotificationData.root.gone()
                    binding!!.fabBT.visible()

                    notificationDetailsList = it.result.notification_details

                    val obj = NotificationFilter("", "All")
                    if (!notificationFilters.contains(obj)) {
                        notificationFilters.add(0, obj)

                    }
                    notificationFilters = it.result.notification_filter
                    setNotificationFilterAdapter()
                    if (notificationDetailsList.isNotEmpty()) {
                        // binding!!.layoutButtons.visible()           // new functionality removed buttons
                        setNotificationDetailsAdapter()
                    } else {
                        //noDataLayout()
                        //binding!!.layoutButtons.gone()         // new functionality
                        binding!!.layoutNoNotificationData.tvNoData.text =
                            "No notifications have been received"
                        binding!!.layoutNoNotificationData.root.visible()
                    }

                    if(lastFilterType != "" && lastSelectedPosition != 0){
                        binding!!.layoutNoNotificationData.root.gone()
                        binding!!.rvNotificationDetails.visible()
                        binding!!.rvNotificationNavigation.scrollToPosition(lastSelectedPosition)
                        notificationFilterAdapter.updatePosition(position = lastSelectedPosition)
                        val filterType = lastFilterType
                        notificationAdapter.updateFilter(filterType = filterType)
                        notificationAdapter.notifyDataSetChanged()

                    }


                    if(lastMessageTypeSelected == 2){
                        unreadSelected()
                    }else if(lastMessageTypeSelected == 1){
                        readSelected()
                    }else{
                        if (this::notificationAdapter.isInitialized) {
                            if (notificationDetailsList.size > 0) {
                                binding!!.rvNotificationDetails.visible()
                                binding!!.layoutNoNotificationData.root.gone()
                                binding!!.layoutNoData.root.gone()
                                notificationAdapter.readUnreadFilter(false, true)
                                notificationAdapter.notifyDataSetChanged()
                            }

                        }
                    }

                } else {
                    noDataLayout()
                }

            } else {
                noDataLayout()
                toast(getString(R.string.server_error))
            }
        }

        sharedViewModel.updateNotificationModel.observe(this) {
            binding!!.includeProgress.progressBar.gone()
            //Timber.d("updateNotificationResponse $it")
            if (it != null) {
                if (it.code == 200)
                    callFetchNotificationApi()
                if (it.message != null)
                    toast(it.message)
            } else
                toast(getString(R.string.server_error))
        }
    }

    private fun noDataLayout() {
        binding!!.layoutButtons.gone()
        binding!!.layoutMain.gone()
        binding!!.layoutNoData.root.visible()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setPopup(view: View, position: Int) {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.notification_options_menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }
        val item = popup.menu.getItem(0)
        item.isVisible = true

        /* val item1 = popup.menu.getItem(1)
         item1.isVisible = true

         val item2 = popup.menu.getItem(2)
         item2.isVisible = true*/

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {

                R.id.notificationsMarksAllAsRead -> {
                    isAllMarkAsReadUnread = true

                    if (notificationDetailsList.isNotEmpty()) {
                        notificationDetailsList[position].data.forEach {
                            notificationIds += "${it.id},"
                        }
                    }

                    callUpdateNotificationApi()
                }

                /*  R.id.notificationsMarksAllAsUnRead -> {
                      isAllMarkAsReadUnread = false
                      setNotificationDetailsAdapter()
                  }

                  R.id.notificationDeleteAll -> {
                      notificationDetailsList.clear()
                      notificationAdapter.notifyDataSetChanged()

                  }*/
            }
            true
        }

        // show icons on popup menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        } else {
            try {
                val fields = popup.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field[popup]
                        val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        popup.show()

    }

    private fun setNotificationFilterAdapter() {

        /*notificationFilters.apply {
            add(NotificationFilter("All", R.drawable.ic_book))
            add(NotificationFilter("Booking", R.drawable.ic_book))
            add(NotificationFilter("Pickup Chart", R.drawable.ic_book))
            add(NotificationFilter("Cancelled", R.drawable.ic_book))
        }*/

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding!!.rvNotificationNavigation.layoutManager = layoutManager
        notificationFilterAdapter =
            NotificationFilterAdapter(this, notificationFilters, this)
        binding!!.rvNotificationNavigation.adapter = notificationFilterAdapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setNotificationDetailsAdapter() {
        setReadUnreadClickListener()
        binding!!.rvNotificationDetails.visible()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding!!.rvNotificationDetails.layoutManager = layoutManager
        notificationAdapter = NotificationParentAdapter(
            this@NotificationsActivity,
            this,
            notificationDetailsList,
        )
        binding!!.rvNotificationDetails.adapter = notificationAdapter
        notificationAdapter.notifyDataSetChanged()
    }

    private fun setReadUnreadClickListener() {
        binding!!.btnUnread.setOnClickListener {
            isUnreadSelected = true
            unreadSelected()
        }

        binding!!.btnRead.setOnClickListener {
            isUnreadSelected = false
            readSelected()
        }
    }

    private fun readSelected() {
        binding!!.layoutNoNotificationData.root.gone()
        binding!!.rvNotificationDetails.visible()
        binding!!.btnUnread.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.un_select_color
            )
        )
        binding!!.btnRead.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

        notificationAdapter.readUnreadFilter(true)
        notificationAdapter.notifyDataSetChanged()
    }

    private fun unreadSelected() {
        binding!!.layoutNoNotificationData.root.gone()
        binding!!.rvNotificationDetails.visible()
        binding!!.btnRead.setBackgroundColor(ContextCompat.getColor(this, R.color.un_select_color))
        binding!!.btnUnread.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

        notificationAdapter.readUnreadFilter(false)
        notificationAdapter.notifyDataSetChanged()
    }


    fun onclickBack(v: View) {
        onBackPressed()
    }

    override fun onClick(view: View?, position: Int?, view2: View?, isMarkRead: Boolean?) {

        val containerStateNotification = view as CardView
        val dottedViewNotification = view2 as ImageView

        if (view.tag == getString(R.string.notifications)) {

            if (isMarkRead == false) {
                toast("$isMarkRead")
                dottedViewNotification.visible()
                containerStateNotification.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.transparent
                    )
                )
            } else {
                dottedViewNotification.gone()
                containerStateNotification.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
            }
        }

    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            if (view.tag == NotificationFilterAdapter.tag && ::notificationFilterAdapter.isInitialized && ::notificationAdapter.isInitialized) {
                binding!!.layoutNoNotificationData.root.gone()
                binding!!.rvNotificationDetails.visible()
                notificationFilterAdapter.updatePosition(position = position)
                lastSelectedPosition = position
                val filterType = notificationFilters[position].label
                lastFilterType = filterType
                notificationAdapter.updateFilter(filterType = filterType)
                notificationAdapter.notifyDataSetChanged()
            } else if (view.tag == getString(R.string.more_actions)) {
                setPopup(view, position)
            }
        }

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {

        binding!!.rvNotificationDetails.gone()
        binding!!.layoutNoNotificationData.root.visible()
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }


}