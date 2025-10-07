package com.bitla.ts.presentation.view.activity.notifications

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.ApiInterface
import com.bitla.ts.data.format_type
import com.bitla.ts.data.get_notification_details_method
import com.bitla.ts.databinding.ActivityNotificationDetailsPhase3Binding
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.domain.pojo.notificationDetails.GetNotificationDetails
import com.bitla.ts.domain.pojo.notificationDetails.request.NotificationDetailsRequest
import com.bitla.ts.domain.pojo.notificationDetails.request.ReqBody
import com.bitla.ts.presentation.adapter.NotificationDetailsOuterCardAdapter
import com.bitla.ts.presentation.viewModel.SharedViewModel
import com.bitla.ts.utils.LoadingState
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import com.google.gson.GsonBuilder
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import toast
import visible

class NotificationDetailsPhase3Actvity : BaseActivity(){

    private lateinit var binding: ActivityNotificationDetailsPhase3Binding

    private var loginModelPref: LoginModel = LoginModel()
    private var bccId: Int? = 0

    private var notificationMessage: String? = null
    private var notificationDescription: String? = null
    private var notificationTitle: String? = null
    private var notificationDoj: String? = null
    private var notificationServiceNumber: String? = null
    private var notificationFares: String? = null
    private var notificationSeats: String? = null
    private var notificationId: Int? = null
    private var notificationImage: String? = null
    private val sharedViewModel by viewModel<SharedViewModel<Any?>>()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var notificationDetailsOuterCardAdapter: NotificationDetailsOuterCardAdapter
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
    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDetailsPhase3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        binding.toolbarNotificationsDetails.tvCurrentHeader.text = "Notification Details"
        binding.toolbarNotificationsDetails.imgSearch.setImageResource(R.drawable.ic_share_grey)
        binding.toolbarNotificationsDetails.imgBack.setOnClickListener{
            onBackPressed()
        }

        getFCMIntentData()
        Timber.d("NotificationNew Activity")


        if (isNetworkAvailable()) {
            callNotificationDetailsApi()

            //callUpdateNotificationApi()
        } else
            noNetworkToast()

        setUpObserver()


//        setNotificationDetailsAdapter()
        setNotificationTag("MIS")

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

    }

    private fun setUpObserver() {
        sharedViewModel.loadingState.observe(this) {
            when (it) {
                LoadingState.LOADING -> binding.includeProgress.progressBar.visible()
                LoadingState.LOADED -> binding.includeProgress.progressBar.gone()
                else -> binding.includeProgress.progressBar.gone()
            }
        }

        sharedViewModel.notificationDetailsModel.observe(this) {
//            binding.includeProgress.progressBar.gone()
            Timber.d("notificationDetailsResponse $it")
            try {
                if (it != null) {
                    if (it.code == 200) {
                        binding.layoutNoData.root.gone()
                        binding.toolbarNotificationsDetails.imgSearch.visible()
                        binding.includeProgress.root.gone()


                        if (it.updated_response != null){
                            binding.nestedNewNotificationLayout.visible()

                            binding.notifivationHeading.text= it.updated_response.message
                            binding.dateAndTime.text= it.updated_response.notification_date

                            setNotificationDetailsAdapter(it.updated_response.result)
                        }
                        else{
                            binding.oldLayoutCard.visible()

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
                        }
                    } else {
                        binding.oldLayoutCard.gone()
                        binding.nestedNewNotificationLayout.gone()
                        binding.toolbarNotificationsDetails.imgSearch.gone()
                        binding.layoutNoData.root.visible()
                        binding.includeProgress.root.gone()
                        if (!it.message.isEmpty()) {
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


    private fun callNotificationDetailsApi() {
//
        val notificationDetailsRequest = NotificationDetailsRequest(
            bccId.toString(), format_type, get_notification_details_method,
            ReqBody(
                loginModelPref.api_key,
                notification_id = notificationId ?: 0
            )
        )

        if (isNetworkAvailable()) {
            sharedViewModel.newGetNotificationDetails(

                loginModelPref.api_key,notificationId ?: 0, get_notification_details_method
            )
        } else {
            noNetworkToast()
        }
    }


    override fun isInternetOnCallApisAndInitUI() {

    }

    private fun setNotificationDetailsAdapter( notificationList: List<com.bitla.ts.domain.pojo.notificationDetails.Result>) {

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvNotificationDetails.layoutManager = layoutManager
        notificationDetailsOuterCardAdapter =
            NotificationDetailsOuterCardAdapter(this, notificationList)
        binding.rvNotificationDetails.adapter = notificationDetailsOuterCardAdapter

    }

    private fun setNotificationTag(notificationTag: String) {
        binding.tvNotificationTag.text = notificationTag
        when (notificationTag) {
            "Booking" -> {
                binding.notificationTagCard.background.setTint(ContextCompat.getColor(this, R.color.light_green_notification_booking))
                binding.tvNotificationTag.setTextColor(ContextCompat.getColor(this, R.color.booked_tickets))
            }
            "Cancellation" -> {
                binding.notificationTagCard.background.setTint(ContextCompat.getColor(this, R.color.light_red_notification_cancel))
                binding.tvNotificationTag.setTextColor(ContextCompat.getColor(this, R.color.colorRed2))
            }
            "Pickup Chart" -> {
                binding.notificationTagCard.background.setTint(ContextCompat.getColor(this, R.color.light_purple_notification_pickupchart))
                binding.tvNotificationTag.setTextColor(ContextCompat.getColor(this, R.color.button_secondary_bg))
            }
            "Review" -> {
                binding.notificationTagCard.background.setTint(ContextCompat.getColor(this, R.color.light_color_notification_review))
                binding.tvNotificationTag.setTextColor(ContextCompat.getColor(this, R.color.color_black_revenue))
            }
            "Updates" -> {
                binding.notificationTagCard.background.setTint(ContextCompat.getColor(this, R.color.light_blue_notification_updates))
                binding.tvNotificationTag.setTextColor(ContextCompat.getColor(this, R.color.color_black_revenue))
            }
            "General" -> {
                binding.notificationTagCard.background.setTint(ContextCompat.getColor(this, R.color.light_blue_notification_general))
                binding.tvNotificationTag.setTextColor(ContextCompat.getColor(this, R.color.button_secondary_bg))
            }
            "Blocking" -> {
                binding.notificationTagCard.background.setTint(ContextCompat.getColor(this, R.color.yellow_notification_blocking))
                binding.tvNotificationTag.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            "Unblocking" -> {
                binding.notificationTagCard.background.setTint(ContextCompat.getColor(this, R.color.blue_notification_unblocking))
                binding.tvNotificationTag.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            "MIS" -> {
                binding.notificationTagCard.background.setTint(ContextCompat.getColor(this, R.color.light_purple_notification_mis))
                binding.tvNotificationTag.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
        }
    }





    fun dummyAPI(){
        Timber.d("NotificationNew Activity 2")

        val client = OkHttpClient()
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.creatorsenclose.in/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        Timber.d("NotificationNew Activity 3")

        val apiInterface = retrofit.create(ApiInterface::class.java)
        apiInterface.dummy(
            "63fdc5886fa39"
        ).enqueue(object : retrofit2.Callback<GetNotificationDetails> {
            override fun onResponse(
                call: retrofit2.Call<GetNotificationDetails>,
                response: Response<GetNotificationDetails>
            ) {

                Timber.d("NotificationNew Activity 4")
                val it = response.body()
                Timber.d("viewReservationResponse ${response.body()!!.updated_response}")

                if (it != null) {

                    if (it.updated_response != null){
                        binding.notifivationHeading.text= it.updated_response.message
                        binding.dateAndTime.text= it.updated_response.notification_date

                        setNotificationDetailsAdapter(it.updated_response.result)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<GetNotificationDetails>, t: Throwable) {
                Timber.d("NotificationNew Activity 5 ${t.message}")

            }


        })

//        ApiRepo.callRetrofit(directionCall, this, directionUrl, this, progress_bar, this)

    }



}
