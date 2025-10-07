package com.bitla.ts.presentation.view.activity.notifications

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.format_type
import com.bitla.ts.data.get_notification_details_method
import com.bitla.ts.data.update_notification_method
import com.bitla.ts.databinding.ActivityNotificationDetailsBinding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.notificationDetails.request.NotificationDetailsRequest
import com.bitla.ts.domain.pojo.update_notification.request.UpdateNotificationRequest
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
import timber.log.Timber
import toast
import visible

class NotificationDetailsActivity : BaseActivity() {
    private var notificationImage: String? = null
    private var notificationMessage: String? = null
    private var notificationDescription: String? = null
    private var notificationTitle: String? = null
    private var notificationDoj: String? = null
    private var notificationServiceNumber: String? = null
    private var notificationFares: String? = null
    private var notificationSeats: String? = null
    private var notificationId: Int? = null
    private lateinit var binding: ActivityNotificationDetailsBinding
    private var loginModelPref: LoginModel = LoginModel()
    private var bccId: Int? = 0

    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()

    override fun initUI() {
        getPref()

        lifecycleScope.launch {
            sharedViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }

    }

    private fun setInfo() {
        binding.tvNotificationType.text = ""
        binding.tvNotificationDetailsTitle.text = notificationTitle
        binding.tvNotificationBody.text = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDetailsBinding.inflate(layoutInflater)

        binding.toolbarNotificationsDetails.tvCurrentHeader.setText(R.string.notifications)
        binding.toolbarNotificationsDetails.imgSearch.setImageResource(R.drawable.ic_share_black)
        val view = binding.root
        setContentView(view)

        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }


        getFCMIntentData()

        setInfo()

        binding.toolbarNotificationsDetails.imgSearch.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "${notificationTitle ?: ""}\n${notificationServiceNumber ?: ""}\n${notificationDoj ?: ""}\n${notificationSeats ?: ""}\n${notificationFares ?: ""}\n${notificationMessage ?: ""}\n${notificationDescription ?: ""}"
                )
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        if (isNetworkAvailable()) {
            callNotificationDetailsApi()
            //callUpdateNotificationApi()
        } else
            noNetworkToast()

        setUpObserver()
    }

    override fun isInternetOnCallApisAndInitUI() {
        callNotificationDetailsApi()
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
    }

    private fun callNotificationDetailsApi() {

        val notificationDetailsRequest = NotificationDetailsRequest(
            bccId.toString(), format_type, get_notification_details_method,
            com.bitla.ts.domain.pojo.notificationDetails.request.ReqBody(
                loginModelPref.api_key,
                notification_id = notificationId ?: 0
            )
        )

        if (isNetworkAvailable()) {
            sharedViewModel.getNotificationDetails(
                loginModelPref.auth_token,
                loginModelPref.api_key, notificationDetailsRequest, get_notification_details_method
            )
        } else {
            noNetworkToast()
        }
    }

    private fun callUpdateNotificationApi() {
        val updateNotificationRequest = UpdateNotificationRequest(
            bccId.toString(), format_type, update_notification_method,
            com.bitla.ts.domain.pojo.update_notification.request.ReqBody(
                loginModelPref.api_key,
                getDeviceUniqueId(this),
                "true",  // "true/false" for mark read/unread APIs
                notificationId.toString()
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
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }
        }

        sharedViewModel.notificationDetailsModel.observe(this) {
            binding.includeProgress.progressBar.gone()
            Timber.d("notificationDetailsResponse $it")
            try {
                if (it != null) {
                    if (it.code == 200) {
                        binding.layoutCard.visible()
                        binding.layoutNoData.root.gone()
                        binding.toolbarNotificationsDetails.imgSearch.visible()

                        if (it.notification_type != null) {
                            binding.tvNotificationType.visible()
                            binding.tvNotificationType.text = it.notification_type
                        } else
                            binding.tvNotificationType.gone()

                        if (it.notification_msg != null) {
                            binding.tvNotificationDetailsTitle.visible()
                            binding.tvNotificationDetailsTitle.text = it.notification_msg
                        } else
                            binding.tvNotificationDetailsTitle.gone()

                        if (it.description != null) {
                            binding.tvNotificationBody.visible()
                            binding.tvNotificationBody.text = it.description
                        } else
                            binding.tvNotificationBody.gone()

                        if (it.service_number != null) {
                            binding.layoutServiceNo.visible()
                            binding.tvServiceNumber.text = it.service_number
                        } else
                            binding.layoutServiceNo.gone()

                        if (it.route != null) {
                            binding.layoutRoute.visible()
                            binding.tvRoute.text = it.route
                        } else
                            binding.layoutRoute.gone()

                        if (it.doj != null) {
                            binding.layoutDoj.visible()
                            binding.tvDoj.text = it.doj
                        } else
                            binding.layoutDoj.gone()

                        if (it.seats != null) {
                            binding.layoutSeats.visible()
                            binding.tvSeats.text = it.seats
                        } else
                            binding.layoutSeats.gone()

                        if (it.fares != null) {
                            binding.layoutFares.visible()
                            binding.tvFares.text = it.fares
                        } else
                            binding.layoutFares.gone()

                        if (it.booked_by != null) {
                            binding.layoutBookedBy.visible()
                            binding.tvBookedBy.text = it.booked_by
                        } else
                            binding.layoutBookedBy.gone()

                        notificationTitle = it.title
                        notificationMessage = it.notification_msg
                        notificationDescription = it.description
                        notificationDoj = it.doj
                        notificationServiceNumber = it.service_number
                        notificationSeats = it.seats
                        notificationFares = it.fares
                    } else {
                        binding.layoutCard.gone()
                        binding.toolbarNotificationsDetails.imgSearch.gone()
                        binding.layoutNoData.root.visible()
                        if (it.message != null) {
                            toast(it.message)
                            binding.layoutNoData.tvNoData.text = it.message
                        }
                    }

                } else
                    toast(getString(R.string.server_error))
            } catch (e: Exception) {
            }
        }

        /*sharedViewModel.updateNotificationModel.observe(this) {
            binding.includeProgress.progressBar.gone()
            Timber.d("updateNotificationResponse $it")
            if (it != null) {
                if (it.code != 200)
                    toast(it.message)
            } else
                toast(getString(R.string.server_error))
        }*/

    }

    fun onclickBack(v: View) {
        onBackPressed()
    }

    private fun getFCMIntentData() {
        if (!intent.getStringExtra(getString(R.string.notification_title)).isNullOrEmpty()) {
            notificationTitle =
                intent.getStringExtra(getString(R.string.notification_title)).toString()
        }
        if (!intent.getStringExtra(getString(R.string.notification_message)).isNullOrEmpty()) {
            notificationMessage =
                intent.getStringExtra(getString(R.string.notification_message)).toString()
        }
        if (!intent.getStringExtra(getString(R.string.notification_image)).isNullOrEmpty()) {
            notificationImage =
                intent.getStringExtra(getString(R.string.notification_image)).toString()
        }
        if (!intent.getStringExtra(getString(R.string.notification_id)).isNullOrEmpty()) {
            notificationId =
                intent.getStringExtra(getString(R.string.notification_id)).toString().toDouble()
                    .toInt()
        }
    }
}