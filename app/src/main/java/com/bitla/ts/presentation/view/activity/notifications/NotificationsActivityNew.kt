package com.bitla.ts.presentation.view.activity.notifications

import EndlessRecyclerOnScrollListener
import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.fetch_notification_method
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemNotificationClickListener
import com.bitla.ts.data.update_notification_method
import com.bitla.ts.databinding.ActivityNotificationNewBinding
import com.bitla.ts.databinding.LayoutCustomNotificationPopupBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.fetch_notification.Data
import com.bitla.ts.domain.pojo.fetch_notification.NotificationDetail
import com.bitla.ts.domain.pojo.fetch_notification.request.NotificationFilter
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.update_notification.request.ReqBody
import com.bitla.ts.domain.pojo.update_notification.request.UpdateNotificationRequest
import com.bitla.ts.presentation.adapter.NotificationChildAdapter
import com.bitla.ts.presentation.adapter.NotificationFilterAdapter
import com.bitla.ts.presentation.adapter.NotificationParentAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.getDeviceUniqueId
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
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


class NotificationsActivityNew : BaseActivity(), OnItemNotificationClickListener,
    OnItemClickListener, DialogSingleButtonListener {

    private lateinit var receiver: BroadcastReceiver
    private var maxPage: Int = 0
    private var isLoading: Boolean = false
    private var bccId: Int = 0
    private var filterMenu: PopupMenu? = null
    private var lastMessageTypeSelected = 0
    private var isUnreadSelected: Boolean = true
    private var lastFilterType = "-1"
    private var lastSelectedPosition = 0
    private var filterPosition = 0
    private var readType = 3
    private var notificationIds: String = ""
    private var page = 1
    private var perPage = 10
    private var currentDay = 1

    private lateinit var binding: ActivityNotificationNewBinding
    private var notificationFilters: MutableList<NotificationFilter> =
        mutableListOf()
    private lateinit var notificationFilterAdapter: NotificationFilterAdapter

    private var notificationDetailsList: MutableList<NotificationDetail> = mutableListOf()
    private lateinit var notificationAdapter: NotificationParentAdapter
    private lateinit var notificationAdapterNew: NotificationChildAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var isAllMarkAsReadUnread: Boolean = false

    private var loginModelPref: LoginModel = LoginModel()

    private lateinit var requestLauncher: ActivityResultLauncher<String>
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()

    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener

    override fun initUI() {
        getPref()
    }
    
    
    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
    }

    override fun onResume() {
        super.onResume()

        if (isUnreadSelected) {
            binding.progressPB.visible()

            if (isNetworkAvailable())
                callFetchNotificationApi()
            else
                noNetworkToast()

            setUpObserver()
        } else {
            readSelected()
        }
        enableNotification()

        if(::notificationFilterAdapter.isInitialized){
            notificationFilterAdapter.updatePosition(position = lastSelectedPosition)


        }


    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationNewBinding.inflate(layoutInflater)
        binding.toolbarNotifications.tvCurrentHeader.setText(R.string.notifications)
        binding.layoutNoNotificationData.tvNoData.text =
            getString(R.string.no_notifications_have_been_received)
        val view = binding.root

        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        binding.tvDay.text = this.getString(R.string.today)


        binding.tvDay.setOnClickListener {
            val customPopupWindow = showDayPopup(this)
            customPopupWindow.showAsDropDown(binding.tvDay)
        }



        binding.toolbarNotifications.imgSearch.gone()

        enableNotification()
        onReuestNotifyLaunchFun()
        binding.btnTurnOn.setOnClickListener {
            turnOnNotification()
            
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            page = 1
            binding.progressPB.visible()
            if (isNetworkAvailable()) {
                callFetchNotificationApi()
            } else {
                noNetworkToast()
            }

        }

        //fab implementation

        binding.fabBT.setOnClickListener {
            showPopupMenu(binding.fabBT)
        }

        binding.rvNotificationDetails.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(page < maxPage && !isLoading){
                        notificationAdapterNew.showProgress()
                        loadMoreData()
                        isLoading = true
                    }
                }
            }
        })

        val filter = IntentFilter("FCM_NOTIFICATION_RECEIVED")
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "FCM_NOTIFICATION_RECEIVED") {
                    page = 1
                    callFetchNotificationApi()

                }
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)


        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                   showToast(it)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(::receiver.isInitialized){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        }
    }


    private fun loadMoreData() {
        page+=1
        callFetchNotificationApi()
    }

    fun dataLoaded(){
        isLoading = false
    }



    private fun turnOnNotification() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
            startActivity(settingsIntent)
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                binding.notificationReqCard?.gone()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
                startActivity(settingsIntent)
            } else {
                binding.notificationReqCard?.visible()
                askPermissionNotification()
            }
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
                    page = 1
                    readType = 3
                    binding.progressPB.visible()
                    callFetchNotificationApi()



                    true
                }

                R.id.readMessagesTV -> {
                    lastMessageTypeSelected = 1
                    addSelectedTick(popupMenu, 1)
                    page = 1
                    readType = 1
                    binding.progressPB.visible()
                    callFetchNotificationApi()

                    true
                }

                R.id.unreadMessagesTV -> {
                    lastMessageTypeSelected = 2
                    page = 1
                    readType = 2
                    binding.progressPB.visible()
                    callFetchNotificationApi()



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
                binding.notificationReqCard?.gone()
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
        requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun enableNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.areNotificationsEnabled()) {
                binding.notificationReqCard?.gone()
            } else {
                binding.notificationReqCard?.visible()
            }
        }
    }

    override fun isInternetOnCallApisAndInitUI() {
        callFetchNotificationApi()
    }


    private fun callFetchNotificationApi() {
        if (isNetworkAvailable()) {
            sharedViewModel.newFetchNotifications(
                loginModelPref.api_key, fetch_notification_method,true,perPage,page,lastFilterType,readType,currentDay
            )
        } else {
            noNetworkToast()
        }
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
                LoadingState.LOADING -> binding.includeProgress.progressBar.gone()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }
        }

        sharedViewModel.fetchNotificationModel.observe(this) {
            binding.includeProgress.progressBar.gone()
            binding.swipeRefreshLayout?.isRefreshing = false


            if (it != null) {
                if (it.code == 200) {
                   binding.progressPB.gone()
                    binding.layoutMain.visible()
                    binding.dayLayout.visible()
                    binding.layoutNoData.root.gone()
                    binding.layoutNoNotificationData.root.gone()
                    binding.fabBT.visible()

                    binding.tvNotificationCount.text = it.result.totalItems.toString()
                    maxPage = it.result.numberOfPages

                    if(!it.result.notification_details.isEmpty()){
                        binding.layoutNoNotificationData.root.gone()
                        binding.rvNotificationDetails.visible()
                        val list : ArrayList<Data> = arrayListOf()
                        list.addAll(it.result.notification_details[0].data)
                        sharedViewModel.getNotificationList(list)
                    }else{
                        binding.layoutNoNotificationData.root.visible()
                        binding.rvNotificationDetails.gone()
                    }
                    if(sharedViewModel.notificationListData.value.isNullOrEmpty()){
                        sharedViewModel.getNotificationFilterList(it.result.notification_filter)
                    }

                }
                else if(it.code == 401){
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                    showUnauthorisedDialog()


                }
                else {
                    noDataLayout()
                }

            } else {
                noDataLayout()
                toast(getString(R.string.server_error))
            }
        }

        sharedViewModel.notificationListData.observe(this) {
                if(page ==1){
                    setNotificationDetailsAdapterNew(it)
                }else{
                    notificationAdapterNew.addData(it)
                }

        }

        sharedViewModel.notificationFilterData.observe(this) {
            setNotificationFilterAdapter(it)
        }

        sharedViewModel.updateNotificationModel.observe(this) {
            binding.includeProgress.progressBar.gone()
            if (it != null) {
                if (it.code == 200)
                    callFetchNotificationApi()
                if (it.message != null)
                    toast(it.message)
            } else
                toast(getString(R.string.server_error))
        }
    }

    fun showDayPopup(context: Context): PopupWindow {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dayBinding = LayoutCustomNotificationPopupBinding.inflate(inflater)

        val popupWindow = PopupWindow(context)
        popupWindow.contentView = dayBinding.root
        popupWindow.width = 600 // Example: Set width to 300 pixels
        popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_round_white_stroke))

        dayBinding.todayTV.setOnClickListener {
            setTodayDate(popupWindow)
        }

        dayBinding.yesterdayTV.setOnClickListener {
            setYesterday(popupWindow)

        }
        dayBinding.dayBeforeYestTV.setOnClickListener {
            setDayBeforeYesterday(popupWindow)
        }

        return popupWindow
    }

    private fun setDayBeforeYesterday(popupWindow: PopupWindow) {
        currentDay = 3
        page = 1
        binding.progressPB.visible()
        callFetchNotificationApi()
        binding.tvDay.text = this.getString(R.string.day_before_yesterday)
        popupWindow.dismiss()
    }

    private fun setYesterday(popupWindow: PopupWindow) {
        currentDay = 2
        page = 1
        binding.progressPB.visible()
        callFetchNotificationApi()
        binding.tvDay.text = this.getString(R.string.yesterday)
        popupWindow.dismiss()
    }

    private fun setTodayDate(popupWindow: PopupWindow) {
        currentDay = 1
        page = 1
        binding.progressPB.visible()
        callFetchNotificationApi()
        binding.tvDay.text = this.getString(R.string.today)
        popupWindow.dismiss()
    }

    private fun setNotificationDetailsAdapterNew(list: ArrayList<Data>) {
        setReadUnreadClickListener()
        binding.rvNotificationDetails.visible()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvNotificationDetails.layoutManager = layoutManager
        notificationAdapterNew = NotificationChildAdapter(
            this@NotificationsActivityNew,
            list,this
        )
        binding.rvNotificationDetails.adapter = notificationAdapterNew
    }



    private fun noDataLayout() {
        binding.layoutButtons.gone()
        binding.layoutMain.gone()
        binding.layoutNoData.root.visible()
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

    private fun setNotificationFilterAdapter(notificationFilterList: List<NotificationFilter>) {

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.filterTypeRV.layoutManager = layoutManager
        notificationFilterAdapter =
            NotificationFilterAdapter(this, notificationFilterList, this)
        binding.filterTypeRV.adapter = notificationFilterAdapter

    }



    private fun setReadUnreadClickListener() {
        binding.btnUnread.setOnClickListener {
            isUnreadSelected = true
            unreadSelected()
        }

        binding.btnRead.setOnClickListener {
            isUnreadSelected = false
            readSelected()
        }
    }

    private fun readSelected() {
        binding.layoutNoNotificationData.root.gone()
        binding.rvNotificationDetails.visible()
        binding.btnUnread.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.un_select_color
            )
        )
        binding.btnRead.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

        notificationAdapter.readUnreadFilter(true)
        notificationAdapter.notifyDataSetChanged()
    }

    private fun unreadSelected() {
        binding.layoutNoNotificationData.root.gone()
        binding.rvNotificationDetails.visible()
        binding.btnRead.setBackgroundColor(ContextCompat.getColor(this, R.color.un_select_color))
        binding.btnUnread.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

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
            if (view.tag == NotificationFilterAdapter.tag) {
                notificationFilterAdapter.updatePosition(position = position)
                lastSelectedPosition = position
                val filterType = sharedViewModel.getFilterType(sharedViewModel.notificationFilterData.value?.get(position)?.label?:"")
                lastFilterType = filterType.toString()
                page = 1
                if (isNetworkAvailable()) {
                    binding.progressPB.visible()

                    sharedViewModel.newFetchNotifications(
                        loginModelPref.api_key, fetch_notification_method,true,perPage,page,lastFilterType,readType,currentDay
                    )
                } else {
                    noNetworkToast()
                }


            } else if (view.tag == getString(R.string.more_actions)) {
                setPopup(view, position)
            }
        }

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

        binding.rvNotificationDetails.gone()
        binding.layoutNoNotificationData.root.visible()
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


}