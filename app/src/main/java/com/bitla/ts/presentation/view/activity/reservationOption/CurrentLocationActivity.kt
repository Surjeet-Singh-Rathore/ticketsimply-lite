package com.bitla.ts.presentation.view.activity.reservationOption


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.eta_details_name
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.LayoutActivityCurrentLocationBinding
import com.bitla.ts.domain.pojo.eta.EtaDetail
import com.bitla.ts.domain.pojo.eta.Request.EtaRequest
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.EtaEntriesAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.EtaViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.common.retrieveRouteId
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

class CurrentLocationActivity : BaseActivity(), View.OnClickListener, DialogSingleButtonListener {

    private lateinit var binding: LayoutActivityCurrentLocationBinding
    private lateinit var apiKey: String
    private lateinit var bccId: String
    private var loginModelPref: LoginModel = LoginModel()
    private val etaDetailsViewModel by viewModel<EtaViewModel<Any?>>()
    private var routeId: Int? = null
    private var travelDate: String = ""
    private lateinit var etaEntriesAdapter: EtaEntriesAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var locale: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTicketDetailsObserver()
    }

    override fun isInternetOnCallApisAndInitUI() {
        routeId = retrieveRouteId()
        getPref()
        binding.includeHeader.imageOptionLayout.gone()

        val subheader = PreferenceUtils.getString("toolbarsubheader")
        binding.includeHeader.textHeaderTitle.text = "ETA"
        binding.includeHeader.headerTitleDesc.text = "$subheader"

    }

    override fun initUI() {
        binding = LayoutActivityCurrentLocationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        routeId = retrieveRouteId()
        getPref()
//        val header = PreferenceUtils.getString("toolbarheader")
        binding.includeHeader.imageOptionLayout.gone()

        val subheader = PreferenceUtils.getString("toolbarsubheader")
        binding.includeHeader.textHeaderTitle.text = "ETA"
        binding.includeHeader.headerTitleDesc.text = "$subheader"
        binding.includeHeader.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        binding.includeHeader.imageHeaderRightImage.setOnClickListener(View.OnClickListener {
        })
        lifecycleScope.launch {
            etaDetailsViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                  showToast(it)
                }
            }
        }
    }

    private fun getPref() {
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
        bccId = PreferenceUtils.getBccId().toString()
        apiKey = loginModelPref.api_key
        travelDate = PreferenceUtils.getTravelDate()
        callEtaApi()
        startShimmerEffect()
    }

    private fun callEtaApi() {
        if (isNetworkAvailable()) {
            val reqBody =
                com.bitla.ts.domain.pojo.eta.Request.ReqBody(
                    apiKey,
                    routeId?.toString() ?: "",
                    travelDate,
                    locale = locale
                )
            val etaDetailsRequest =
                EtaRequest(bccId, format_type, eta_details_name, reqBody)

            /*etaDetailsViewModel.etaApi(
                loginModelPref.auth_token,
                loginModelPref.api_key,
                etaDetailsRequest,
                eta_details_name
            )
            */

            etaDetailsViewModel.etaApi(
                reqBody,
                eta_details_name
            )

        } else
            noNetworkToast()
    }

    @SuppressLint("SetTextI18n")
    private fun setTicketDetailsObserver() {

        etaDetailsViewModel.eta.observe(this) {
//            Timber.d("Test Run Eta Details ${it.toString()}")

            if (it != null) {
                when (it.code) {
                    200 -> {
                        binding.tableHeaderLayout.visible()
                        setEtaEntriesAdapter(it.eta_details)
                        stopShimmerEffect()
                    }
                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            this,
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/
                        showUnauthorisedDialog()

                    }
                    else -> {
//                      it.message?.let { it1 -> toast(it1) }
                        stopShimmerEffect()
                        binding.noDataText.text = it.message ?: ""
                        binding.NoResult.visible()
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun setEtaEntriesAdapter(list: MutableList<EtaDetail>) {
        layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvEtaEntries.layoutManager = layoutManager
        etaEntriesAdapter =
            EtaEntriesAdapter(this, list)
        binding.rvEtaEntries.adapter = etaEntriesAdapter
    }


    private fun startShimmerEffect() {

        binding.shimmerLayout.visible()
        binding.NoResult.gone()
        // binding.rvEtaEntries.gone()
        binding.shimmerLayout.startShimmer()

    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {

        binding.shimmerLayout.gone()
        if (binding.shimmerLayout.isShimmerStarted) {
            binding.shimmerLayout.stopShimmer()

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


}